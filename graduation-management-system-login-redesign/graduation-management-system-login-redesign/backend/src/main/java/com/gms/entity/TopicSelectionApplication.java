package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Topic_selection_application")
public class TopicSelectionApplication {
    @TableId(value = "application_id", type = IdType.AUTO)
    private Integer application_id;
    private String Student_Stu_id;
    private Integer Topic_Topic_id;
    private Integer Tsa_order_for_topic;
    private String Tsa_status;

    public Integer getApplication_id() {
        return application_id;
    }

    public void setApplication_id(Integer application_id) {
        this.application_id = application_id;
    }

    public String getStudent_Stu_id() {
        return Student_Stu_id;
    }

    public void setStudent_Stu_id(String student_Stu_id) {
        Student_Stu_id = student_Stu_id;
    }

    public Integer getTopic_Topic_id() {
        return Topic_Topic_id;
    }

    public void setTopic_Topic_id(Integer topic_Topic_id) {
        Topic_Topic_id = topic_Topic_id;
    }

    public Integer getTsa_order_for_topic() {
        return Tsa_order_for_topic;
    }

    public void setTsa_order_for_topic(Integer tsa_order_for_topic) {
        Tsa_order_for_topic = tsa_order_for_topic;
    }

    public String getTsa_status() {
        return Tsa_status;
    }

    public void setTsa_status(String tsa_status) {
        Tsa_status = tsa_status;
    }
}
