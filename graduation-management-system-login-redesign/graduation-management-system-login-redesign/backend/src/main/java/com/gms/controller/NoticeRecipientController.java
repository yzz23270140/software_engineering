package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.NoticeRecipient;
import com.gms.mapper.NoticeRecipientMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notice-recipients")
public class NoticeRecipientController {
    private final NoticeRecipientMapper mapper;

    public NoticeRecipientController(NoticeRecipientMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ApiResponse<List<NoticeRecipient>> list(
        @RequestParam(required = false) Integer noticeId,
        @RequestParam(required = false) String studentId
    ) {
        QueryWrapper<NoticeRecipient> qw = new QueryWrapper<>();
        if (noticeId != null) {
            qw.eq("Notice_id", noticeId);
        }
        if (studentId != null && !studentId.isEmpty()) {
            qw.eq("Student_Stu_id", studentId);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }
}
