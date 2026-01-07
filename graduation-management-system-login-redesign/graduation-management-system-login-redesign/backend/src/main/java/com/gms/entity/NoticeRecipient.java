package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Notice_recipient")
public class NoticeRecipient {
    @TableId(value = "Nr_id", type = IdType.AUTO)
    private Integer Nr_id;
    private Integer Notice_id;
    private String Student_Stu_id;

    public Integer getNr_id() {
        return Nr_id;
    }

    public void setNr_id(Integer nr_id) {
        Nr_id = nr_id;
    }

    public Integer getNotice_id() {
        return Notice_id;
    }

    public void setNotice_id(Integer notice_id) {
        Notice_id = notice_id;
    }

    public String getStudent_Stu_id() {
        return Student_Stu_id;
    }

    public void setStudent_Stu_id(String student_Stu_id) {
        Student_Stu_id = student_Stu_id;
    }
}
