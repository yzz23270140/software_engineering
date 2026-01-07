package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Paper")
public class Paper {
    @TableId(value = "Paper_id", type = IdType.AUTO)
    private Integer Paper_id;
    private Integer Thesis_proposal_Tp_id;
    private String Paper_version;
    private String Paper_title;
    private String Paper_abstract;
    private String Paper_text;
    private String Paper_status;
    private java.sql.Timestamp Paper_submission_time;
    private String file_url;

    public Integer getPaper_id() {
        return Paper_id;
    }

    public void setPaper_id(Integer paper_id) {
        Paper_id = paper_id;
    }

    public Integer getThesis_proposal_Tp_id() {
        return Thesis_proposal_Tp_id;
    }

    public void setThesis_proposal_Tp_id(Integer thesis_proposal_Tp_id) {
        Thesis_proposal_Tp_id = thesis_proposal_Tp_id;
    }

    public String getPaper_version() {
        return Paper_version;
    }

    public void setPaper_version(String paper_version) {
        Paper_version = paper_version;
    }

    public String getPaper_title() {
        return Paper_title;
    }

    public void setPaper_title(String paper_title) {
        Paper_title = paper_title;
    }

    public String getPaper_abstract() {
        return Paper_abstract;
    }

    public void setPaper_abstract(String paper_abstract) {
        Paper_abstract = paper_abstract;
    }

    public String getPaper_text() {
        return Paper_text;
    }

    public void setPaper_text(String paper_text) {
        Paper_text = paper_text;
    }

    public String getPaper_status() {
        return Paper_status;
    }

    public void setPaper_status(String paper_status) {
        Paper_status = paper_status;
    }

    public java.sql.Timestamp getPaper_submission_time() {
        return Paper_submission_time;
    }

    public void setPaper_submission_time(java.sql.Timestamp paper_submission_time) {
        Paper_submission_time = paper_submission_time;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }
}
