USE graduation_management_system;

INSERT IGNORE INTO Instructor (Ins_id, Ins_name, Ins_college)
VALUES ('I000001','指导老师1','计算机学院');

INSERT IGNORE INTO Teacher (Tea_id, Tea_name, Tea_institute)
VALUES ('T000001','张老师','软件工程');

INSERT IGNORE INTO Judge (Jud_id, Jud_name)
VALUES ('J000001','评委1');

INSERT IGNORE INTO Student (Stu_id, Stu_name, Stu_college, Stu_major, enrollment_year, Instructor_Ins_id, Teacher_Tea_id)
VALUES ('S20240000001','学生1','计算机学院','软件工程',2024,'I000001','T000001');

INSERT IGNORE INTO Topic (Topic_id, Topic_name, Topic_description, Topic_type, Teacher_Tea_id, Topic_major, academic_year, Topic_status)
VALUES
  (1, '题目1', '示例描述', '教师自拟', 'T000001', '["软件工程"]', '2024-2025', '已发布'),
  (2, '题目2', '基于Web的毕业设计管理系统', '科研项目', 'T000001', '["软件工程","计算机科学"]', '2024-2025', '已发布'),
  (3, '题目3', '智能选题推荐模块设计', '教师自拟', 'T000001', '["软件工程"]', '2024-2025', '审核通过');
