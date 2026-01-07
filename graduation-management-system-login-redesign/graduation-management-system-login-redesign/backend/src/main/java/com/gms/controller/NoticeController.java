package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.Notice;
import com.gms.entity.NoticeRecipient;
import com.gms.mapper.NoticeMapper;
import com.gms.mapper.NoticeRecipientMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeMapper mapper;
    private final NoticeRecipientMapper recipientMapper;

    public NoticeController(NoticeMapper mapper, NoticeRecipientMapper recipientMapper) {
        this.mapper = mapper;
        this.recipientMapper = recipientMapper;
    }

    @GetMapping
    public ApiResponse<List<Notice>> list(@RequestParam(required = false) String type) {
        QueryWrapper<Notice> qw = new QueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            qw.eq("Notice_type", type);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody NoticeRequest request) {
        if (request == null) {
            return ApiResponse.error("通知数据不能为空");
        }
        Notice item = new Notice();
        item.setNotice_type(request.getNotice_type());
        item.setNotice_title(request.getNotice_title());
        item.setNotice_content(request.getNotice_content());
        mapper.insert(item);
        List<String> studentIds = request.getStudent_ids() == null ? new ArrayList<>() : request.getStudent_ids();
        if (!Boolean.TRUE.equals(request.getSend_all()) && !studentIds.isEmpty()) {
            for (String studentId : studentIds) {
                NoticeRecipient recipient = new NoticeRecipient();
                recipient.setNotice_id(item.getNotice_id());
                recipient.setStudent_Stu_id(studentId);
                recipientMapper.insert(recipient);
            }
        }
        return ApiResponse.ok(item.getNotice_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody Notice item) {
        item.setNotice_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.deleteById(id) > 0);
    }

    public static class NoticeRequest {
        private String Notice_type;
        private String Notice_title;
        private String Notice_content;
        private Boolean send_all;
        private List<String> student_ids;

        public String getNotice_type() {
            return Notice_type;
        }

        public void setNotice_type(String notice_type) {
            Notice_type = notice_type;
        }

        public String getNotice_title() {
            return Notice_title;
        }

        public void setNotice_title(String notice_title) {
            Notice_title = notice_title;
        }

        public String getNotice_content() {
            return Notice_content;
        }

        public void setNotice_content(String notice_content) {
            Notice_content = notice_content;
        }

        public Boolean getSend_all() {
            return send_all;
        }

        public void setSend_all(Boolean send_all) {
            this.send_all = send_all;
        }

        public List<String> getStudent_ids() {
            return student_ids;
        }

        public void setStudent_ids(List<String> student_ids) {
            this.student_ids = student_ids;
        }
    }
}
