package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Instructor")
public class Instructor {
    @TableId(value = "Ins_id", type = IdType.INPUT)
    private String Ins_id;
    private String Ins_name;
    private String Ins_college;

    public String getIns_id() {
        return Ins_id;
    }

    public void setIns_id(String ins_id) {
        Ins_id = ins_id;
    }

    public String getIns_name() {
        return Ins_name;
    }

    public void setIns_name(String ins_name) {
        Ins_name = ins_name;
    }

    public String getIns_college() {
        return Ins_college;
    }

    public void setIns_college(String ins_college) {
        Ins_college = ins_college;
    }
}
