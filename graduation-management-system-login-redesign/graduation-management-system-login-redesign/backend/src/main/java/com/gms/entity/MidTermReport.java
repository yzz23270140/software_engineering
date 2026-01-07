package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Mid_term_report")
public class MidTermReport {
    @TableId(value = "Mtr_id", type = IdType.AUTO)
    private Integer Mtr_id;
    private Integer Thesis_proposal_Tp_id;
    private java.sql.Timestamp Mtr_submission_time;
    private String Mtr_progress;
    private String Mtr_problems;
    private String Mtr_plan;
    private String Mtr_evaluation;
    private String Mtr_comment;
    private java.math.BigDecimal Mtr_score;
    private String checker_id;
    private java.time.LocalDateTime check_time;
    private String Mtr_status;
    private String attachment_url;

    public Integer getMtr_id() {
        return Mtr_id;
    }

    public void setMtr_id(Integer mtr_id) {
        Mtr_id = mtr_id;
    }

    public Integer getThesis_proposal_Tp_id() {
        return Thesis_proposal_Tp_id;
    }

    public void setThesis_proposal_Tp_id(Integer thesis_proposal_Tp_id) {
        Thesis_proposal_Tp_id = thesis_proposal_Tp_id;
    }

    public java.sql.Timestamp getMtr_submission_time() {
        return Mtr_submission_time;
    }

    public void setMtr_submission_time(java.sql.Timestamp mtr_submission_time) {
        Mtr_submission_time = mtr_submission_time;
    }

    public String getMtr_progress() {
        return Mtr_progress;
    }

    public void setMtr_progress(String mtr_progress) {
        Mtr_progress = mtr_progress;
    }

    public String getMtr_problems() {
        return Mtr_problems;
    }

    public void setMtr_problems(String mtr_problems) {
        Mtr_problems = mtr_problems;
    }

    public String getMtr_plan() {
        return Mtr_plan;
    }

    public void setMtr_plan(String mtr_plan) {
        Mtr_plan = mtr_plan;
    }

    public String getMtr_evaluation() {
        return Mtr_evaluation;
    }

    public void setMtr_evaluation(String mtr_evaluation) {
        Mtr_evaluation = mtr_evaluation;
    }

    public String getMtr_comment() {
        return Mtr_comment;
    }

    public void setMtr_comment(String mtr_comment) {
        Mtr_comment = mtr_comment;
    }

    public java.math.BigDecimal getMtr_score() {
        return Mtr_score;
    }

    public void setMtr_score(java.math.BigDecimal mtr_score) {
        Mtr_score = mtr_score;
    }

    public String getChecker_id() {
        return checker_id;
    }

    public void setChecker_id(String checker_id) {
        this.checker_id = checker_id;
    }

    public java.time.LocalDateTime getCheck_time() {
        return check_time;
    }

    public void setCheck_time(java.time.LocalDateTime check_time) {
        this.check_time = check_time;
    }

    public String getMtr_status() {
        return Mtr_status;
    }

    public void setMtr_status(String mtr_status) {
        Mtr_status = mtr_status;
    }

    public String getAttachment_url() {
        return attachment_url;
    }

    public void setAttachment_url(String attachment_url) {
        this.attachment_url = attachment_url;
    }
}
