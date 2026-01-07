package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.DefenseRecord;
import com.gms.mapper.DefenseRecordMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/defense-records")
public class DefenseRecordController {
    private final DefenseRecordMapper mapper;

    public DefenseRecordController(DefenseRecordMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<List<DefenseRecord>> list(@RequestParam(required = false) Integer paperId) {
        QueryWrapper<DefenseRecord> qw = new QueryWrapper<>();
        if (paperId != null) {
            qw.eq("Paper_id", paperId);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody DefenseRecord item) {
        if (item.getDr_time() == null) {
            item.setDr_time(LocalDateTime.now());
        }
        mapper.insert(item);
        return ApiResponse.ok(item.getDr_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody DefenseRecord item) {
        item.setDr_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }
}
