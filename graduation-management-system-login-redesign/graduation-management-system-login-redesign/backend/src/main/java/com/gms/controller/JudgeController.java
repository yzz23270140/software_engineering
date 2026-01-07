package com.gms.controller;

import com.gms.common.ApiResponse;
import com.gms.entity.Judge;
import com.gms.mapper.JudgeMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/judges")
public class JudgeController {
    private final JudgeMapper mapper;

    public JudgeController(JudgeMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<List<Judge>> list() {
        return ApiResponse.ok(mapper.selectList(null));
    }

    @PostMapping
    public ApiResponse<String> create(@RequestBody Judge item) {
        try {
            if (item.getJud_id() != null && mapper.selectById(item.getJud_id()) != null) {
                return ApiResponse.error("评委ID已存在");
            }
            mapper.insert(item);
            return ApiResponse.ok(item.getJud_id());
        } catch (Exception ex) {
            return ApiResponse.error("创建评委失败，请检查数据是否完整。");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable String id) {
        try {
            return ApiResponse.ok(mapper.deleteById(id) > 0);
        } catch (Exception ex) {
            return ApiResponse.error("删除评委失败，请稍后重试。");
        }
    }
}
