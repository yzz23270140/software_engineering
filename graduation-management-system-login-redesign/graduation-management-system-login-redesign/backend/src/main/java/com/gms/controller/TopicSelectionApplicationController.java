package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.TopicSelectionApplication;
import com.gms.mapper.TopicSelectionApplicationMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic-applications")
public class TopicSelectionApplicationController {
    private final TopicSelectionApplicationMapper mapper;

    public TopicSelectionApplicationController(TopicSelectionApplicationMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<List<TopicSelectionApplication>> list(@RequestParam(required = false) String studentId,
                                                             @RequestParam(required = false) Integer topicId,
                                                             @RequestParam(required = false) String status) {
        QueryWrapper<TopicSelectionApplication> qw = new QueryWrapper<>();
        if (studentId != null && !studentId.isEmpty()) {
            qw.eq("Student_Stu_id", studentId);
        }
        if (topicId != null) {
            qw.eq("Topic_Topic_id", topicId);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq("Tsa_status", status);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody TopicSelectionApplication item) {
        try {
            QueryWrapper<TopicSelectionApplication> qw = new QueryWrapper<>();
            qw.eq("Student_Stu_id", item.getStudent_Stu_id());
            qw.eq("Topic_Topic_id", item.getTopic_Topic_id());
            if (mapper.selectCount(qw) > 0) {
                return ApiResponse.error("该课题已提交过申请");
            }
            mapper.insert(item);
            return ApiResponse.ok(item.getApplication_id());
        } catch (Exception ex) {
            return ApiResponse.error("选题申请失败：" + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody TopicSelectionApplication item) {
        item.setApplication_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.deleteById(id) > 0);
    }
}
