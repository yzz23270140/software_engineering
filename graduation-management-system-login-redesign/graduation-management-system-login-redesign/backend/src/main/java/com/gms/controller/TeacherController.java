package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.Student;
import com.gms.entity.Teacher;
import com.gms.mapper.StudentMapper;
import com.gms.mapper.TeacherMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    private final TeacherMapper mapper;
    private final StudentMapper studentMapper;

    public TeacherController(TeacherMapper mapper, StudentMapper studentMapper) {
        this.mapper = mapper;
        this.studentMapper = studentMapper;
    }

    @GetMapping
    public ApiResponse<List<Teacher>> list() {
        return ApiResponse.ok(mapper.selectList(null));
    }

    @PostMapping
    public ApiResponse<String> create(@RequestBody Teacher item) {
        try {
            if (item.getTea_id() != null && mapper.selectById(item.getTea_id()) != null) {
                return ApiResponse.error("教师ID已存在");
            }
            mapper.insert(item);
            return ApiResponse.ok(item.getTea_id());
        } catch (Exception ex) {
            return ApiResponse.error("创建教师失败，请检查数据是否完整。");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable String id) {
        try {
            QueryWrapper<Student> qw = new QueryWrapper<>();
            qw.eq("Teacher_Tea_id", id);
            studentMapper.delete(qw);
            return ApiResponse.ok(mapper.deleteById(id) > 0);
        } catch (Exception ex) {
            return ApiResponse.error("删除教师失败，请稍后重试。");
        }
    }
}
