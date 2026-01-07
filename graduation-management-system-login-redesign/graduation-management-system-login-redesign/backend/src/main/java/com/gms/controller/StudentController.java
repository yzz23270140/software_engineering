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
            if (item.getStu_id() == null || item.getStu_id().trim().isEmpty()) {
                return ApiResponse.error("学生ID不能为空");
            }
            String stuId = item.getStu_id().trim();
            if (stuId.length() != 12) {
                return ApiResponse.error("学生ID长度必须为12位");
            }
            if (!stuId.matches("^S\\d{11}$")) {
                return ApiResponse.error("学生ID格式错误，应为S开头+11位数字");
            }
            if (item.getEnrollment_year() == null) {
                return ApiResponse.error("入学年份不能为空");
            }
            String yearStr = stuId.substring(1, 5);
            try {
                int year = Integer.parseInt(yearStr);
                if (year != item.getEnrollment_year()) {
                    return ApiResponse.error("年份不匹配");
                }
            } catch (NumberFormatException ex) {
                return ApiResponse.error("学生ID年份格式错误");
            }
            if (item.getStu_id() != null && mapper.selectById(item.getStu_id()) != null) {
                return ApiResponse.error("学生ID已存在");
            }
            mapper.insert(item);
            return ApiResponse.ok(item.getStu_id());
        } catch (Exception ex) {
            return ApiResponse.error("创建学生失败：" + ex.getMessage());
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
