package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.FirstInstanceOpinion;
import com.gms.mapper.FirstInstanceOpinionMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/first-opinions")
public class FirstInstanceOpinionController {
    private final FirstInstanceOpinionMapper mapper;

    public FirstInstanceOpinionController(FirstInstanceOpinionMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<List<FirstInstanceOpinion>> list(@RequestParam(required = false) Integer paperId,
                                                        @RequestParam(required = false) String reviewerId) {
        QueryWrapper<FirstInstanceOpinion> qw = new QueryWrapper<>();
        if (paperId != null) {
            qw.eq("Paper_id", paperId);
        }
        if (reviewerId != null && !reviewerId.isEmpty()) {
            qw.eq("reviewer_id", reviewerId);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody FirstInstanceOpinion item) {
        if (item.getPaper_id() == null) {
            return ApiResponse.error("初审意见提交失败：缺少论文信息。");
        }
        QueryWrapper<FirstInstanceOpinion> qw = new QueryWrapper<>();
        qw.eq("Paper_id", item.getPaper_id());
        FirstInstanceOpinion existing = mapper.selectOne(qw);
        if (existing != null) {
            item.setFio_id(existing.getFio_id());
            mapper.updateById(item);
            return ApiResponse.ok(existing.getFio_id());
        }
        mapper.insert(item);
        return ApiResponse.ok(item.getFio_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody FirstInstanceOpinion item) {
        item.setFio_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }
}
