package com.gms.controller;

import com.gms.common.ApiResponse;
import com.gms.entity.Student;
import com.gms.mapper.StudentMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentMapper mapper;

    public StudentController(StudentMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<List<Student>> list() {
        return ApiResponse.ok(mapper.selectList(null));
    }

    @PostMapping
    public ApiResponse<String> create(@RequestBody Student item) {
        try {
            if (item.getStu_id() != null && mapper.selectById(item.getStu_id()) != null) {
                return ApiResponse.error("学生ID已存在");
            }
            mapper.insert(item);
            return ApiResponse.ok(item.getStu_id());
        } catch (Exception ex) {
            return ApiResponse.error("创建学生失败，请检查数据是否完整。");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable String id) {
        try {
            return ApiResponse.ok(mapper.deleteById(id) > 0);
        } catch (Exception ex) {
            return ApiResponse.error("删除学生失败，请稍后重试。");
        }
    }
}
