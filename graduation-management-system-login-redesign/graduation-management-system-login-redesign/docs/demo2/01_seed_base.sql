USE graduation_management_system;

INSERT INTO Instructor (Ins_id, Ins_name, Ins_college)
VALUES
  ('I000101', '周辅导员', '计算机学院'),
  ('I000102', '陈辅导员', '卓越学院');

INSERT INTO Teacher (Tea_id, Tea_name, Tea_institute, Tea_education_level)
VALUES
  ('T000101', '王老师', '软件工程', '硕士'),
  ('T000102', '赵老师', '计算机科学', '博士');

INSERT INTO Student (Stu_id, Stu_name, Stu_college, Stu_major, enrollment_year, Instructor_Ins_id, Teacher_Tea_id)
VALUES
  ('S20250000001', '刘一凡', '计算机学院', '软件工程', 2025, 'I000101', NULL),
  ('S20250000002', '孙佳琪', '计算机学院', '人工智能', 2025, 'I000101', NULL),
  ('S20250000003', '马子涵', '卓越学院', '计算机科学与技术', 2025, 'I000102', NULL);

INSERT INTO Judge (Jud_id, Jud_name, Jud_group, Jud_institute)
VALUES
  ('J100001', '审题高', '审题组', '软件工程'),
  ('J200001', '开题孙', '开题评审组', '计算机科学'),
  ('J300001', '中期韩', '中期检查小组', '人工智能'),
  ('J400001', '二审杨', '论文二审组', '软件工程'),
  ('J400002', '二审郑', '论文二审组', '软件工程'),
  ('J500001', '答辩胡', '毕设评委组', '计算机科学');

INSERT INTO Office (Off_id, Off_name)
VALUES ('1', '教科办');
