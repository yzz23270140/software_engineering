package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.Paper;
import com.gms.entity.SecondInstanceOpinion;
import com.gms.mapper.PaperMapper;
import com.gms.mapper.SecondInstanceOpinionMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/second-opinions")
public class SecondInstanceOpinionController {
    private final SecondInstanceOpinionMapper mapper;
    private final PaperMapper paperMapper;

    public SecondInstanceOpinionController(SecondInstanceOpinionMapper mapper,
                                           PaperMapper paperMapper) {
        this.mapper = mapper;
        this.paperMapper = paperMapper;
    }

    @GetMapping
    public ApiResponse<List<SecondInstanceOpinion>> list(@RequestParam(required = false) Integer paperId,
                                                         @RequestParam(required = false) String reviewerId) {
        QueryWrapper<SecondInstanceOpinion> qw = new QueryWrapper<>();
        if (paperId != null) {
            qw.eq("Paper_id", paperId);
        }
        if (reviewerId != null && !reviewerId.isEmpty()) {
            qw.eq("reviewer_id", reviewerId);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody SecondInstanceOpinion item) {
        if (item.getPaper_id() == null || item.getReviewer_id() == null || item.getReviewer_id().isEmpty()) {
            return ApiResponse.error("评审意见提交失败：缺少论文或评审人信息。");
        }
        if (item.getSio_opinion() == null || item.getSio_opinion().isEmpty()) {
            return ApiResponse.error("评审意见提交失败：请填写评审意见。");
        }
        if (item.getSio_score() == null) {
            return ApiResponse.error("评审意见提交失败：请填写评分。");
        }
        if (!hasResult(item.getSio_details())) {
            return ApiResponse.error("评审意见提交失败：请填写评审结论。");
        }
        QueryWrapper<SecondInstanceOpinion> qw = new QueryWrapper<>();
        qw.eq("Paper_id", item.getPaper_id());
        qw.eq("reviewer_id", item.getReviewer_id());
        List<SecondInstanceOpinion> existingList = mapper.selectList(qw);
        if (existingList.size() > 1) {
            mapper.delete(qw);
            mapper.insert(item);
            updatePaperStatus(item.getPaper_id());
            return ApiResponse.ok(item.getSio_id());
        }
        if (!existingList.isEmpty()) {
            SecondInstanceOpinion existing = existingList.get(0);
            item.setSio_id(existing.getSio_id());
            mapper.updateById(item);
            updatePaperStatus(item.getPaper_id());
            return ApiResponse.ok(existing.getSio_id());
        }
        mapper.insert(item);
        updatePaperStatus(item.getPaper_id());
        return ApiResponse.ok(item.getSio_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody SecondInstanceOpinion item) {
        item.setSio_id(id);
        boolean updated = mapper.updateById(item) > 0;
        if (updated && item.getPaper_id() != null) {
            updatePaperStatus(item.getPaper_id());
        }
        return ApiResponse.ok(updated);
    }

    private void updatePaperStatus(Integer paperId) {
        QueryWrapper<SecondInstanceOpinion> qw = new QueryWrapper<>();
        qw.eq("Paper_id", paperId);
        List<SecondInstanceOpinion> opinions = normalizeOpinions(mapper.selectList(qw));
        if (opinions.isEmpty()) {
            return;
        }
        boolean hasReject = opinions.stream().anyMatch((row) -> hasRejectResult(row.getSio_details(), row.getSio_status()));
        String status;
        if (hasReject) {
            status = "未通过";
        } else if (opinions.size() >= 2) {
            status = "已通过";
        } else {
            status = "评审中";
        }
        Paper paper = new Paper();
        paper.setPaper_id(paperId);
        paper.setPaper_status(status);
        paperMapper.updateById(paper);
    }

    private List<SecondInstanceOpinion> normalizeOpinions(List<SecondInstanceOpinion> rows) {
        Map<String, SecondInstanceOpinion> latest = new HashMap<>();
        for (SecondInstanceOpinion row : rows) {
            String reviewerId = row.getReviewer_id();
            if (reviewerId == null) {
                continue;
            }
            SecondInstanceOpinion current = latest.get(reviewerId);
            if (current == null || (row.getSio_id() != null && row.getSio_id() > current.getSio_id())) {
                latest.put(reviewerId, row);
            }
        }
        return new ArrayList<>(latest.values());
    }

    private boolean hasResult(String details) {
        if (details == null || details.isEmpty()) {
            return false;
        }
        return details.contains("\"result\"");
    }

    private boolean hasRejectResult(String details, String fallbackStatus) {
        if (details != null && details.contains("\"result\":\"未通过\"")) {
            return true;
        }
        return "未通过".equals(fallbackStatus);
    }
}
