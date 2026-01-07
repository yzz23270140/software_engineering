package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("Notice")
public class Notice {
    @TableId(value = "Notice_id", type = IdType.AUTO)
    private Integer Notice_id;
    private String Notice_type;
    private String Notice_title;
    private String Notice_content;
    private LocalDateTime Notice_time;
    private String Notice_target_college;
    private String Notice_target_major;
    private Integer Notice_target_year;
    private String Notice_target_teacher_id;
    private String Notice_target_instructor_id;
    private String Notice_target_student_id;

    public Integer getNotice_id() {
        return Notice_id;
    }

    public void setNotice_id(Integer notice_id) {
        Notice_id = notice_id;
    }

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

    public LocalDateTime getNotice_time() {
        return Notice_time;
    }

    public void setNotice_time(LocalDateTime notice_time) {
        Notice_time = notice_time;
    }

    public String getNotice_target_college() {
        return Notice_target_college;
    }

    public void setNotice_target_college(String notice_target_college) {
        Notice_target_college = notice_target_college;
    }

    public String getNotice_target_major() {
        return Notice_target_major;
    }

    public void setNotice_target_major(String notice_target_major) {
        Notice_target_major = notice_target_major;
    }

    public Integer getNotice_target_year() {
        return Notice_target_year;
    }

    public void setNotice_target_year(Integer notice_target_year) {
        Notice_target_year = notice_target_year;
    }

    public String getNotice_target_teacher_id() {
        return Notice_target_teacher_id;
    }

    public void setNotice_target_teacher_id(String notice_target_teacher_id) {
        Notice_target_teacher_id = notice_target_teacher_id;
    }

    public String getNotice_target_instructor_id() {
        return Notice_target_instructor_id;
    }

    public void setNotice_target_instructor_id(String notice_target_instructor_id) {
        Notice_target_instructor_id = notice_target_instructor_id;
    }

    public String getNotice_target_student_id() {
        return Notice_target_student_id;
    }

    public void setNotice_target_student_id(String notice_target_student_id) {
        Notice_target_student_id = notice_target_student_id;
    }
}
