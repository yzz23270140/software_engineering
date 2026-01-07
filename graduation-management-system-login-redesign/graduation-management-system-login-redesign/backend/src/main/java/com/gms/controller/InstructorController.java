package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.Instructor;
import com.gms.entity.Student;
import com.gms.mapper.InstructorMapper;
import com.gms.mapper.StudentMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructors")
public class InstructorController {
    private final InstructorMapper mapper;
    private final StudentMapper studentMapper;

    public InstructorController(InstructorMapper mapper, StudentMapper studentMapper) {
        this.mapper = mapper;
        this.studentMapper = studentMapper;
    }

    @GetMapping
    public ApiResponse<List<Instructor>> list() {
        return ApiResponse.ok(mapper.selectList(null));
    }

    @PostMapping
    public ApiResponse<String> create(@RequestBody Instructor item) {
        try {
            if (item.getIns_id() != null && mapper.selectById(item.getIns_id()) != null) {
                return ApiResponse.error("辅导员ID已存在");
            }
            mapper.insert(item);
            return ApiResponse.ok(item.getIns_id());
        } catch (Exception ex) {
            return ApiResponse.error("创建辅导员失败，请检查数据是否完整。");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable String id) {
        try {
            QueryWrapper<Student> qw = new QueryWrapper<>();
            qw.eq("Instructor_Ins_id", id);
            studentMapper.delete(qw);
            return ApiResponse.ok(mapper.deleteById(id) > 0);
        } catch (Exception ex) {
            return ApiResponse.error("删除辅导员失败，请稍后重试。");
        }
    }
}
