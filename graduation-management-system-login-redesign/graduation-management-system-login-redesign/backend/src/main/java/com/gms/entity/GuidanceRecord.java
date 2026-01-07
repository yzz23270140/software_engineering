package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("Guidance_record")
public class GuidanceRecord {
    @TableId(value = "Gr_id", type = IdType.AUTO)
    private Integer Gr_id;
    private Integer Thesis_proposal_Tp_id;
    private LocalDateTime Gr_time;
    private Integer Gr_duration;
    private String Gr_method;
    private String Gr_record;
    private String Gr_suggestions;
    private String Gr_confirm;
    private String confirmed_by;
    private LocalDateTime confirmed_time;
    private String attachment_url;

    public Integer getGr_id() {
        return Gr_id;
    }

    public void setGr_id(Integer gr_id) {
        Gr_id = gr_id;
    }

    public Integer getThesis_proposal_Tp_id() {
        return Thesis_proposal_Tp_id;
    }

    public void setThesis_proposal_Tp_id(Integer thesis_proposal_Tp_id) {
        Thesis_proposal_Tp_id = thesis_proposal_Tp_id;
    }

    public LocalDateTime getGr_time() {
        return Gr_time;
    }

    public void setGr_time(LocalDateTime gr_time) {
        Gr_time = gr_time;
    }

    public Integer getGr_duration() {
        return Gr_duration;
    }

    public void setGr_duration(Integer gr_duration) {
        Gr_duration = gr_duration;
    }

    public String getGr_method() {
        return Gr_method;
    }

    public void setGr_method(String gr_method) {
        Gr_method = gr_method;
    }

    public String getGr_record() {
        return Gr_record;
    }

    public void setGr_record(String gr_record) {
        Gr_record = gr_record;
    }

    public String getGr_suggestions() {
        return Gr_suggestions;
    }

    public void setGr_suggestions(String gr_suggestions) {
        Gr_suggestions = gr_suggestions;
    }

    public String getGr_confirm() {
        return Gr_confirm;
    }

    public void setGr_confirm(String gr_confirm) {
        Gr_confirm = gr_confirm;
    }

    public String getConfirmed_by() {
        return confirmed_by;
    }

    public void setConfirmed_by(String confirmed_by) {
        this.confirmed_by = confirmed_by;
    }

    public LocalDateTime getConfirmed_time() {
        return confirmed_time;
    }

    public void setConfirmed_time(LocalDateTime confirmed_time) {
        this.confirmed_time = confirmed_time;
    }

    public String getAttachment_url() {
        return attachment_url;
    }

    public void setAttachment_url(String attachment_url) {
        this.attachment_url = attachment_url;
    }
}
