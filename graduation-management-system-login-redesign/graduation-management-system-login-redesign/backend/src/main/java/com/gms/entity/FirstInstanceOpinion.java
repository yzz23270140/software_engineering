package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("First_instance_opinion")
public class FirstInstanceOpinion {
    @TableId(value = "Fio_id", type = IdType.AUTO)
    private Integer Fio_id;
    private Integer Paper_id;
    private String reviewer_id;
    private String Fio_opinion;
    private Double Fio_score;
    private String Fio_details;
    private java.sql.Timestamp Fio_time;
    private String Fio_status;

    public Integer getFio_id() {
        return Fio_id;
    }

    public void setFio_id(Integer fio_id) {
        Fio_id = fio_id;
    }

    public Integer getPaper_id() {
        return Paper_id;
    }

    public void setPaper_id(Integer paper_id) {
        Paper_id = paper_id;
    }

    public String getReviewer_id() {
        return reviewer_id;
    }

    public void setReviewer_id(String reviewer_id) {
        this.reviewer_id = reviewer_id;
    }

    public String getFio_opinion() {
        return Fio_opinion;
    }

    public void setFio_opinion(String fio_opinion) {
        Fio_opinion = fio_opinion;
    }

    public Double getFio_score() {
        return Fio_score;
    }

    public void setFio_score(Double fio_score) {
        Fio_score = fio_score;
    }

    public String getFio_details() {
        return Fio_details;
    }

    public void setFio_details(String fio_details) {
        Fio_details = fio_details;
    }

    public java.sql.Timestamp getFio_time() {
        return Fio_time;
    }

    public void setFio_time(java.sql.Timestamp fio_time) {
        Fio_time = fio_time;
    }

    public String getFio_status() {
        return Fio_status;
    }

    public void setFio_status(String fio_status) {
        Fio_status = fio_status;
    }
}
