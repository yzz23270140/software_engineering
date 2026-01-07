package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Thesis_proposal")
public class ThesisProposal {
    @TableId(value = "Tp_id", type = IdType.AUTO)
    private Integer Tp_id;
    private Integer Topic_selection_application_id;
    private String Tp_title;
    private String Tp_text;
    private String Tp_status;

    public Integer getTp_id() {
        return Tp_id;
    }

    public void setTp_id(Integer tp_id) {
        Tp_id = tp_id;
    }

    public Integer getTopic_selection_application_id() {
        return Topic_selection_application_id;
    }

    public void setTopic_selection_application_id(Integer topic_selection_application_id) {
        Topic_selection_application_id = topic_selection_application_id;
    }

    public String getTp_title() {
        return Tp_title;
    }

    public void setTp_title(String tp_title) {
        Tp_title = tp_title;
    }

    public String getTp_text() {
        return Tp_text;
    }

    public void setTp_text(String tp_text) {
        Tp_text = tp_text;
    }

    public String getTp_status() {
        return Tp_status;
    }

    public void setTp_status(String tp_status) {
        Tp_status = tp_status;
    }
}
