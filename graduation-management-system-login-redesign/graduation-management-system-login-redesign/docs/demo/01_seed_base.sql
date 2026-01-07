USE graduation_management_system;

INSERT INTO Instructor (Ins_id, Ins_name, Ins_college)
VALUES
  ('I000001', '王辅导员', '计算机学院'),
  ('I000002', '李辅导员', '卓越学院');

INSERT INTO Teacher (Tea_id, Tea_name, Tea_institute, Tea_education_level)
VALUES
  ('T000001', '张老师', '软件工程', '硕士'),
  ('T000002', '李老师', '计算机科学', '博士');

INSERT INTO Student (Stu_id, Stu_name, Stu_college, Stu_major, enrollment_year, Instructor_Ins_id, Teacher_Tea_id)
VALUES
  ('S20240000001', '王小明', '计算机学院', '软件工程', 2024, 'I000001', NULL),
  ('S20240000002', '李小红', '计算机学院', '人工智能', 2024, 'I000001', NULL),
  ('S20240000003', '赵小强', '卓越学院', '计算机科学与技术', 2024, 'I000002', NULL);

INSERT INTO Judge (Jud_id, Jud_name, Jud_group, Jud_institute)
VALUES
  ('J000001', '审题张', '审题组', '软件工程'),
  ('J000002', '开题李', '开题评审组', '计算机科学'),
  ('J000003', '中期王', '中期检查小组', '人工智能'),
  ('J000004', '二审刘', '论文二审组', '软件工程'),
  ('J000006', '二审周', '论文二审组', '软件工程'),
  ('J000005', '答辩陈', '毕设评委组', '计算机科学');

INSERT INTO Office (Off_id, Off_name)
VALUES ('1', '教科办');
