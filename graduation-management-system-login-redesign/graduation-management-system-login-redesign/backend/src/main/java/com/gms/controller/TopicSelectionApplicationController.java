package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.Student;
import com.gms.entity.Topic;
import com.gms.entity.TopicSelectionApplication;
import com.gms.mapper.StudentMapper;
import com.gms.mapper.TopicMapper;
import com.gms.mapper.TopicSelectionApplicationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic-applications")
public class TopicSelectionApplicationController {
    private final TopicSelectionApplicationMapper mapper;
    private final StudentMapper studentMapper;
    private final TopicMapper topicMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TopicSelectionApplicationController(TopicSelectionApplicationMapper mapper,
                                               StudentMapper studentMapper,
                                               TopicMapper topicMapper) {
        this.mapper = mapper;
        this.studentMapper = studentMapper;
        this.topicMapper = topicMapper;
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
            if (item.getStudent_Stu_id() == null || item.getStudent_Stu_id().isEmpty()) {
                return ApiResponse.error("学生ID不能为空");
            }
            if (item.getTopic_Topic_id() == null) {
                return ApiResponse.error("课题ID不能为空");
            }
            Student student = studentMapper.selectById(item.getStudent_Stu_id());
            if (student == null) {
                return ApiResponse.error("学生不存在");
            }
            Topic topic = topicMapper.selectById(item.getTopic_Topic_id());
            if (topic == null) {
                return ApiResponse.error("课题不存在");
            }
            if (!isMajorAllowed(topic.getTopic_major(), student.getStu_major())) {
                return ApiResponse.error("该课题不面向当前学生专业");
            }
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

    private boolean isMajorAllowed(String topicMajor, String studentMajor) {
        if (topicMajor == null || studentMajor == null) {
            return false;
        }
        try {
            String[] majors = objectMapper.readValue(topicMajor, String[].class);
            for (String major : majors) {
                if (studentMajor.equals(major)) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return topicMajor.contains(studentMajor);
        }
    }
}
