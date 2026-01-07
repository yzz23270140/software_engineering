package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.MidTermReport;
import com.gms.entity.Paper;
import com.gms.entity.SecondInstanceOpinion;
import com.gms.entity.ThesisProposal;
import com.gms.mapper.MidTermReportMapper;
import com.gms.mapper.PaperMapper;
import com.gms.mapper.SecondInstanceOpinionMapper;
import com.gms.mapper.ThesisProposalMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/papers")
public class PaperController {
    private final PaperMapper mapper;
    private final ThesisProposalMapper thesisProposalMapper;
    private final MidTermReportMapper midTermReportMapper;
    private final SecondInstanceOpinionMapper secondInstanceOpinionMapper;

    public PaperController(PaperMapper mapper,
                           ThesisProposalMapper thesisProposalMapper,
                           MidTermReportMapper midTermReportMapper,
                           SecondInstanceOpinionMapper secondInstanceOpinionMapper) {
        this.mapper = mapper;
        this.thesisProposalMapper = thesisProposalMapper;
        this.midTermReportMapper = midTermReportMapper;
        this.secondInstanceOpinionMapper = secondInstanceOpinionMapper;
    }

    @GetMapping
    public ApiResponse<List<Paper>> list(@RequestParam(required = false) Integer thesisId,
                                         @RequestParam(required = false) String status) {
        QueryWrapper<Paper> qw = new QueryWrapper<>();
        if (thesisId != null) {
            qw.eq("Thesis_proposal_Tp_id", thesisId);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq("Paper_status", status);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody Paper item) {
        if (item.getThesis_proposal_Tp_id() == null) {
            return ApiResponse.error("论文提交失败：未选择开题报告。");
        }
        if (item.getPaper_version() == null || item.getPaper_version().isEmpty()) {
            return ApiResponse.error("论文提交失败：请填写论文版本。");
        }
        if (!"初稿".equals(item.getPaper_version())
            && !"一审稿".equals(item.getPaper_version())
            && !"终稿".equals(item.getPaper_version())) {
            return ApiResponse.error("论文提交失败：版本仅支持初稿、一审稿、终稿。");
        }
        ThesisProposal thesis = thesisProposalMapper.selectById(item.getThesis_proposal_Tp_id());
        if (thesis == null) {
            return ApiResponse.error("论文提交失败：开题报告不存在。");
        }
        if (!"已通过".equals(thesis.getTp_status())) {
            return ApiResponse.error("论文提交失败：仅可基于已通过的开题报告提交。");
        }
        QueryWrapper<MidTermReport> midQw = new QueryWrapper<>();
        midQw.eq("Thesis_proposal_Tp_id", item.getThesis_proposal_Tp_id());
        midQw.eq("Mtr_status", "已通过");
        if (midTermReportMapper.selectCount(midQw) == 0) {
            return ApiResponse.error("论文提交失败：中期检查未通过或尚未完成。");
        }
        if ("一审稿".equals(item.getPaper_version())) {
            QueryWrapper<Paper> firstQw = new QueryWrapper<>();
            firstQw.eq("Thesis_proposal_Tp_id", item.getThesis_proposal_Tp_id());
            firstQw.eq("Paper_version", "初稿");
            firstQw.eq("Paper_status", "已通过");
            if (mapper.selectCount(firstQw) == 0) {
                return ApiResponse.error("论文提交失败：初稿尚未通过教师初审。");
            }
        }
        if ("终稿".equals(item.getPaper_version())) {
            QueryWrapper<Paper> secondQw = new QueryWrapper<>();
            secondQw.eq("Thesis_proposal_Tp_id", item.getThesis_proposal_Tp_id());
            secondQw.eq("Paper_version", "一审稿");
            secondQw.orderByDesc("Paper_submission_time").last("LIMIT 1");
            Paper reviewPaper = mapper.selectOne(secondQw);
            if (reviewPaper == null) {
                return ApiResponse.error("论文提交失败：尚未提交一审稿。");
            }
            QueryWrapper<SecondInstanceOpinion> opinionQw = new QueryWrapper<>();
            opinionQw.eq("Paper_id", reviewPaper.getPaper_id());
            List<SecondInstanceOpinion> opinions = normalizeOpinions(secondInstanceOpinionMapper.selectList(opinionQw));
            if (opinions.size() < 2) {
                return ApiResponse.error("论文提交失败：一审稿评审人数不足（至少2位）。");
            }
            for (SecondInstanceOpinion opinion : opinions) {
                String result = extractResult(opinion.getSio_details(), opinion.getSio_status());
                if (!"通过".equals(result) && !"已通过".equals(result)) {
                    return ApiResponse.error("论文提交失败：一审稿评审未全部通过。");
                }
            }
        }
        QueryWrapper<Paper> existingQw = new QueryWrapper<>();
        existingQw.eq("Thesis_proposal_Tp_id", item.getThesis_proposal_Tp_id());
        existingQw.eq("Paper_version", item.getPaper_version());
        List<Paper> existing = mapper.selectList(existingQw);
        if (!existing.isEmpty()) {
            Paper first = existing.get(0);
            if (!"未通过".equals(first.getPaper_status())) {
                return ApiResponse.error("论文提交失败：该版本已有待处理或通过的记录。");
            }
            item.setPaper_id(first.getPaper_id());
            item.setPaper_status("已提交");
            mapper.updateById(item);
            return ApiResponse.ok(first.getPaper_id());
        }
        item.setPaper_status("已提交");
        mapper.insert(item);
        return ApiResponse.ok(item.getPaper_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody Paper item) {
        item.setPaper_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.deleteById(id) > 0);
    }

    private String extractResult(String details, String fallbackStatus) {
        if (details != null) {
            if (details.contains("未通过") || details.contains("不通过")) {
                return "未通过";
            }
            if (details.contains("通过")) {
                return "通过";
            }
        }
        if ("已通过".equals(fallbackStatus) || "通过".equals(fallbackStatus)
            || "未通过".equals(fallbackStatus) || "不通过".equals(fallbackStatus)) {
            return fallbackStatus;
        }
        return "";
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
}
