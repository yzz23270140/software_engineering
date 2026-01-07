package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.Topic;
import com.gms.mapper.TopicMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicController {
    private final TopicMapper topicMapper;

    public TopicController(TopicMapper topicMapper) {
        this.topicMapper = topicMapper;
    }

    @GetMapping
    public ApiResponse<List<Topic>> list(@RequestParam(required = false) String status,
                                         @RequestParam(required = false) String teacherId) {
        QueryWrapper<Topic> qw = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            qw.eq("Topic_status", status);
        }
        if (teacherId != null && !teacherId.isEmpty()) {
            qw.eq("Teacher_Tea_id", teacherId);
        }
        return ApiResponse.ok(topicMapper.selectList(qw));
    }

    @GetMapping("/{id}")
    public ApiResponse<Topic> get(@PathVariable Integer id) {
        return ApiResponse.ok(topicMapper.selectById(id));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody Topic topic) {
        try {
            if (topic.getTeacher_Tea_id() == null || topic.getTeacher_Tea_id().isEmpty()) {
                return ApiResponse.error("教师ID为空，请重新登录");
            }
            topicMapper.insert(topic);
            return ApiResponse.ok(topic.getTopic_id());
        } catch (Exception ex) {
            return ApiResponse.error("课题申报失败，请检查数据是否完整。");
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody Topic topic) {
        topic.setTopic_id(id);
        return ApiResponse.ok(topicMapper.updateById(topic) > 0);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        return ApiResponse.ok(topicMapper.deleteById(id) > 0);
    }
}
