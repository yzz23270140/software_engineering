package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Judge")
public class Judge {
    @TableId(value = "Jud_id", type = IdType.INPUT)
    private String Jud_id;
    private String Jud_name;
    private String Jud_group;

    public String getJud_id() {
        return Jud_id;
    }

    public void setJud_id(String jud_id) {
        Jud_id = jud_id;
    }

    public String getJud_name() {
        return Jud_name;
    }

    public void setJud_name(String jud_name) {
        Jud_name = jud_name;
    }

    public String getJud_group() {
        return Jud_group;
    }

    public void setJud_group(String jud_group) {
        Jud_group = jud_group;
    }
}
