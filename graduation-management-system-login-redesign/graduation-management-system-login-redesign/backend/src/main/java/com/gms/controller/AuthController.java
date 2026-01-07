package com.gms.controller;

import com.gms.common.ApiResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JdbcTemplate jdbcTemplate;

    public AuthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        String displayName = null;
        if ("student".equals(request.getRole())) {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Student WHERE Stu_id = ?",
                Integer.class,
                request.getUsername()
            );
            if (count == null || count == 0) {
                return ApiResponse.error("学生ID不存在");
            }
            displayName = jdbcTemplate.queryForObject(
                "SELECT Stu_name FROM Student WHERE Stu_id = ?",
                String.class,
                request.getUsername()
            );
        } else if ("teacher".equals(request.getRole())) {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Teacher WHERE Tea_id = ?",
                Integer.class,
                request.getUsername()
            );
            if (count == null || count == 0) {
                return ApiResponse.error("教师ID不存在");
            }
            displayName = jdbcTemplate.queryForObject(
                "SELECT Tea_name FROM Teacher WHERE Tea_id = ?",
                String.class,
                request.getUsername()
            );
        } else if ("judge".equals(request.getRole())) {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Judge WHERE Jud_id = ?",
                Integer.class,
                request.getUsername()
            );
            if (count == null || count == 0) {
                return ApiResponse.error("评审组ID不存在");
            }
            displayName = jdbcTemplate.queryForObject(
                "SELECT Jud_name FROM Judge WHERE Jud_id = ?",
                String.class,
                request.getUsername()
            );
        } else if ("counselor".equals(request.getRole())) {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Instructor WHERE Ins_id = ?",
                Integer.class,
                request.getUsername()
            );
            if (count == null || count == 0) {
                return ApiResponse.error("辅导员ID不存在");
            }
            displayName = jdbcTemplate.queryForObject(
                "SELECT Ins_name FROM Instructor WHERE Ins_id = ?",
                String.class,
                request.getUsername()
            );
        } else if ("office".equals(request.getRole())) {
            if (!"1".equals(request.getUsername())) {
                return ApiResponse.error("教科办ID必须为1");
            }
            displayName = "教科办";
        }
        Map<String, Object> data = new HashMap<>();
        data.put("token", "demo-token");
        data.put("role", request.getRole());
        data.put("username", request.getUsername());
        data.put("name", displayName);
        return ApiResponse.ok(data);
    }

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout() {
        return ApiResponse.ok(true);
    }

    public static class LoginRequest {
        private String username;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
