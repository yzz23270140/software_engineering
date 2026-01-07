package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("Defense_record")
public class DefenseRecord {
    @TableId(value = "Dr_id", type = IdType.AUTO)
    private Integer Dr_id;
    private Integer Paper_id;
    private LocalDateTime Dr_time;
    private String Dr_location;
    private Double Dr_presentation_score;
    private Double Dr_response_score;
    private Double Dr_paper_score;
    private Double Dr_total_score;
    private String Dr_record;
    private String Dr_comment;
    private String Dr_result;

    public Integer getDr_id() {
        return Dr_id;
    }

    public void setDr_id(Integer dr_id) {
        Dr_id = dr_id;
    }

    public Integer getPaper_id() {
        return Paper_id;
    }

    public void setPaper_id(Integer paper_id) {
        Paper_id = paper_id;
    }

    public LocalDateTime getDr_time() {
        return Dr_time;
    }

    public void setDr_time(LocalDateTime dr_time) {
        Dr_time = dr_time;
    }

    public String getDr_location() {
        return Dr_location;
    }

    public void setDr_location(String dr_location) {
        Dr_location = dr_location;
    }

    public Double getDr_presentation_score() {
        return Dr_presentation_score;
    }

    public void setDr_presentation_score(Double dr_presentation_score) {
        Dr_presentation_score = dr_presentation_score;
    }

    public Double getDr_response_score() {
        return Dr_response_score;
    }

    public void setDr_response_score(Double dr_response_score) {
        Dr_response_score = dr_response_score;
    }

    public Double getDr_paper_score() {
        return Dr_paper_score;
    }

    public void setDr_paper_score(Double dr_paper_score) {
        Dr_paper_score = dr_paper_score;
    }

    public Double getDr_total_score() {
        return Dr_total_score;
    }

    public void setDr_total_score(Double dr_total_score) {
        Dr_total_score = dr_total_score;
    }

    public String getDr_record() {
        return Dr_record;
    }

    public void setDr_record(String dr_record) {
        Dr_record = dr_record;
    }

    public String getDr_comment() {
        return Dr_comment;
    }

    public void setDr_comment(String dr_comment) {
        Dr_comment = dr_comment;
    }

    public String getDr_result() {
        return Dr_result;
    }

    public void setDr_result(String dr_result) {
        Dr_result = dr_result;
    }
}
