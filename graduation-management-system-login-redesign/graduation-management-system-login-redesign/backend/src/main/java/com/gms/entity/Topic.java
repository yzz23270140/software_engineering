package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Topic")
public class Topic {
    @TableId(value = "Topic_id", type = IdType.AUTO)
    private Integer Topic_id;
    private String Topic_name;
    private String Topic_description;
    private String Topic_type;
    private String Teacher_Tea_id;
    private String Topic_major;
    private String academic_year;
    private String Topic_status;
    private String created_at;

    public Integer getTopic_id() {
        return Topic_id;
    }

    public void setTopic_id(Integer topic_id) {
        Topic_id = topic_id;
    }

    public String getTopic_name() {
        return Topic_name;
    }

    public void setTopic_name(String topic_name) {
        Topic_name = topic_name;
    }

    public String getTopic_description() {
        return Topic_description;
    }

    public void setTopic_description(String topic_description) {
        Topic_description = topic_description;
    }

    public String getTopic_type() {
        return Topic_type;
    }

    public void setTopic_type(String topic_type) {
        Topic_type = topic_type;
    }

    public String getTeacher_Tea_id() {
        return Teacher_Tea_id;
    }

    public void setTeacher_Tea_id(String teacher_Tea_id) {
        Teacher_Tea_id = teacher_Tea_id;
    }

    public String getTopic_major() {
        return Topic_major;
    }

    public void setTopic_major(String topic_major) {
        Topic_major = topic_major;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public String getTopic_status() {
        return Topic_status;
    }

    public void setTopic_status(String topic_status) {
        Topic_status = topic_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
