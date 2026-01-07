package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.Objection;
import com.gms.mapper.ObjectionMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/objections")
public class ObjectionController {
    private final ObjectionMapper mapper;

    public ObjectionController(ObjectionMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<List<Objection>> list(@RequestParam(required = false) Integer paperId,
                                             @RequestParam(required = false) String status) {
        QueryWrapper<Objection> qw = new QueryWrapper<>();
        if (paperId != null) {
            qw.eq("Paper_id", paperId);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq("Objection_status", status);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody Objection item) {
        mapper.insert(item);
        return ApiResponse.ok(item.getObjection_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody Objection item) {
        item.setObjection_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }
}
