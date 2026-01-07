package com.gms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("Student")
public class Student {
    @TableId(value = "Stu_id", type = IdType.INPUT)
    private String Stu_id;
    private String Stu_name;
    private String Stu_college;
    private String Stu_major;
    private Integer enrollment_year;
    private String Instructor_Ins_id;
    private String Teacher_Tea_id;

    public String getStu_id() {
        return Stu_id;
    }

    public void setStu_id(String stu_id) {
        Stu_id = stu_id;
    }

    public String getStu_name() {
        return Stu_name;
    }

    public void setStu_name(String stu_name) {
        Stu_name = stu_name;
    }

    public String getStu_college() {
        return Stu_college;
    }

    public void setStu_college(String stu_college) {
        Stu_college = stu_college;
    }

    public String getStu_major() {
        return Stu_major;
    }

    public void setStu_major(String stu_major) {
        Stu_major = stu_major;
    }

    public Integer getEnrollment_year() {
        return enrollment_year;
    }

    public void setEnrollment_year(Integer enrollment_year) {
        this.enrollment_year = enrollment_year;
    }

    public String getInstructor_Ins_id() {
        return Instructor_Ins_id;
    }

    public void setInstructor_Ins_id(String instructor_Ins_id) {
        Instructor_Ins_id = instructor_Ins_id;
    }

    public String getTeacher_Tea_id() {
        return Teacher_Tea_id;
    }

    public void setTeacher_Tea_id(String teacher_Tea_id) {
        Teacher_Tea_id = teacher_Tea_id;
    }
}
