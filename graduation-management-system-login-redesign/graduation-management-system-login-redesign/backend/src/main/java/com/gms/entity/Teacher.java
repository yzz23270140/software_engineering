package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Teacher")
public class Teacher {
    @TableId(value = "Tea_id", type = IdType.INPUT)
    private String Tea_id;
    private String Tea_name;
    private String Tea_institute;

    public String getTea_id() {
        return Tea_id;
    }

    public void setTea_id(String tea_id) {
        Tea_id = tea_id;
    }

    public String getTea_name() {
        return Tea_name;
    }

    public void setTea_name(String tea_name) {
        Tea_name = tea_name;
    }

    public String getTea_institute() {
        return Tea_institute;
    }

    public void setTea_institute(String tea_institute) {
        Tea_institute = tea_institute;
    }
}
