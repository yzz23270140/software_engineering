package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Objection")
public class Objection {
    @TableId(value = "Objection_id", type = IdType.AUTO)
    private Integer Objection_id;
    private Integer Paper_id;
    private String Objection_reason;
    private String Objection_status;

    public Integer getObjection_id() {
        return Objection_id;
    }

    public void setObjection_id(Integer objection_id) {
        Objection_id = objection_id;
    }

    public Integer getPaper_id() {
        return Paper_id;
    }

    public void setPaper_id(Integer paper_id) {
        Paper_id = paper_id;
    }

    public String getObjection_reason() {
        return Objection_reason;
    }

    public void setObjection_reason(String objection_reason) {
        Objection_reason = objection_reason;
    }

    public String getObjection_status() {
        return Objection_status;
    }

    public void setObjection_status(String objection_status) {
        Objection_status = objection_status;
    }
}
