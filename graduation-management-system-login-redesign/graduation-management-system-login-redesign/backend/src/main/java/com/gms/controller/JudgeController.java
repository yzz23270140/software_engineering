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
            if (item.getJud_id() == null || item.getJud_id().trim().isEmpty()) {
                return ApiResponse.error("评委ID不能为空");
            }
            if (item.getJud_group() == null || item.getJud_group().trim().isEmpty()) {
                return ApiResponse.error("评审组不能为空");
            }
            String judId = item.getJud_id().trim();
            if (!judId.matches("^J[1-5]\\d{5}$")) {
                return ApiResponse.error("评委ID格式错误，应为J+组别编号+5位数字");
            }
            String groupCode = judId.substring(1, 2);
            String groupName = item.getJud_group().trim();
            String expectedCode = mapGroupToCode(groupName);
            if (expectedCode.isEmpty()) {
                return ApiResponse.error("评审组不合法");
            }
            if (!groupCode.equals(expectedCode)) {
                return ApiResponse.error("评委ID组别编号与评审组不匹配");
            }
            if (item.getJud_id() != null && mapper.selectById(item.getJud_id()) != null) {
                return ApiResponse.error("评委ID已存在");
            }
            mapper.insert(item);
            return ApiResponse.ok(item.getJud_id());
        } catch (Exception ex) {
            return ApiResponse.error("创建评委失败：" + ex.getMessage());
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

    private String mapGroupToCode(String groupName) {
        if ("审题组".equals(groupName)) return "1";
        if ("开题评审组".equals(groupName)) return "2";
        if ("中期检查小组".equals(groupName)) return "3";
        if ("论文二审组".equals(groupName)) return "4";
        if ("毕设评委组".equals(groupName)) return "5";
        return "";
    }
}
