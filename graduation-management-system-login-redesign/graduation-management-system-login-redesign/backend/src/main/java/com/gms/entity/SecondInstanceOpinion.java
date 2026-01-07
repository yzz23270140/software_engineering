package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Second_instance_opinion")
public class SecondInstanceOpinion {
    @TableId(value = "Sio_id", type = IdType.AUTO)
    private Integer Sio_id;
    private Integer Paper_id;
    private String reviewer_id;
    private String Sio_opinion;
    private Double Sio_score;
    private String Sio_details;
    private java.sql.Timestamp Sio_time;
    private String Sio_status;

    public Integer getSio_id() {
        return Sio_id;
    }

    public void setSio_id(Integer sio_id) {
        Sio_id = sio_id;
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

    public String getSio_opinion() {
        return Sio_opinion;
    }

    public void setSio_opinion(String sio_opinion) {
        Sio_opinion = sio_opinion;
    }

    public Double getSio_score() {
        return Sio_score;
    }

    public void setSio_score(Double sio_score) {
        Sio_score = sio_score;
    }

    public String getSio_details() {
        return Sio_details;
    }

    public void setSio_details(String sio_details) {
        Sio_details = sio_details;
    }

    public java.sql.Timestamp getSio_time() {
        return Sio_time;
    }

    public void setSio_time(java.sql.Timestamp sio_time) {
        Sio_time = sio_time;
    }

    public String getSio_status() {
        return Sio_status;
    }

    public void setSio_status(String sio_status) {
        Sio_status = sio_status;
    }
}
