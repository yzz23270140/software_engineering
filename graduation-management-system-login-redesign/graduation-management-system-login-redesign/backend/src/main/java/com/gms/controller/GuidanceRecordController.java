package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.GuidanceRecord;
import com.gms.mapper.GuidanceRecordMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/guidance-records")
public class GuidanceRecordController {
    private final GuidanceRecordMapper mapper;

    public GuidanceRecordController(GuidanceRecordMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<List<GuidanceRecord>> list(@RequestParam(required = false) Integer thesisId,
                                                  @RequestParam(required = false) String confirm) {
        QueryWrapper<GuidanceRecord> qw = new QueryWrapper<>();
        if (thesisId != null) {
            qw.eq("Thesis_proposal_Tp_id", thesisId);
        }
        if (confirm != null && !confirm.isEmpty()) {
            qw.eq("Gr_confirm", confirm);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody GuidanceRecord item) {
        if (item.getThesis_proposal_Tp_id() == null) {
            return ApiResponse.error("指导记录提交失败：未选择开题报告。");
        }
        if (item.getGr_time() == null) {
            item.setGr_time(LocalDateTime.now());
        }
        if (item.getGr_confirm() == null || item.getGr_confirm().isEmpty()) {
            item.setGr_confirm("未确认");
        }
        mapper.insert(item);
        return ApiResponse.ok(item.getGr_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody GuidanceRecord item) {
        if ("已确认".equals(item.getGr_confirm()) && item.getConfirmed_time() == null) {
            item.setConfirmed_time(LocalDateTime.now());
        }
        item.setGr_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.deleteById(id) > 0);
    }
}
