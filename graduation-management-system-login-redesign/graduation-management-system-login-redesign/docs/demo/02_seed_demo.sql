USE graduation_management_system;

-- 1) 已发布课题
INSERT INTO Topic (
  Topic_name,
  Topic_description,
  Topic_type,
  Teacher_Tea_id,
  Topic_major,
  Topic_status,
  review_status,
  academic_year
) VALUES (
  '基于Web的毕业设计管理系统',
  '围绕毕业设计全过程管理，完成选题、开题、中期、论文与答辩的协同平台。',
  '教师自拟',
  'T000001',
  JSON_ARRAY('软件工程'),
  '已发布',
  '审核通过',
  '2024-2025'
);
SET @topic_id = LAST_INSERT_ID();

-- 2) 选题申请（已通过）
INSERT INTO Topic_selection_application (
  Student_Stu_id,
  Topic_Topic_id,
  Tsa_order_for_topic,
  Tsa_status
) VALUES (
  'S20240000001',
  @topic_id,
  1,
  '审核通过'
);
SET @application_id = LAST_INSERT_ID();

-- 3) 开题报告（已通过）
INSERT INTO Thesis_proposal (
  Topic_selection_application_id,
  Tp_title,
  Tp_text,
  Tp_status,
  reviewer_id,
  review_score,
  review_time
) VALUES (
  @application_id,
  '开题报告：毕业设计管理系统',
  '本报告阐述研究背景、目标与技术路线。',
  '已通过',
  'J000002',
  88.50,
  NOW()
);
SET @tp_id = LAST_INSERT_ID();

-- 4) 中期汇报（已通过）
INSERT INTO Mid_term_report (
  Thesis_proposal_Tp_id,
  Mtr_progress,
  Mtr_plan,
  Mtr_status,
  checker_id,
  check_time
) VALUES (
  @tp_id,
  '完成核心功能开发与基础联调。',
  '继续完善评审与答辩流程。',
  '已通过',
  'J000003',
  NOW()
);

-- 5) 中期指导记录（已确认）
INSERT INTO Guidance_record (
  Thesis_proposal_Tp_id,
  Gr_time,
  Gr_method,
  Gr_record,
  Gr_confirm,
  confirmed_by,
  confirmed_time
) VALUES (
  @tp_id,
  NOW(),
  '面谈',
  '讨论系统流程与数据一致性。',
  '已确认',
  'S20240000001',
  NOW()
);

-- 6) 论文初稿
INSERT INTO Paper (
  Thesis_proposal_Tp_id,
  Paper_version,
  Paper_title,
  Paper_abstract,
  Paper_keywords,
  Paper_text,
  Paper_status,
  file_url
) VALUES (
  @tp_id,
  '初稿',
  '毕业设计管理系统',
  '本文描述毕业设计管理系统的设计与实现。',
  JSON_ARRAY('毕业设计', '管理系统'),
  '初稿内容示例。',
  '已提交',
  'about:blank'
);
SET @paper_first_id = LAST_INSERT_ID();

-- 7) 论文一审稿
INSERT INTO Paper (
  Thesis_proposal_Tp_id,
  Paper_version,
  Paper_title,
  Paper_abstract,
  Paper_keywords,
  Paper_text,
  Paper_status,
  file_url
) VALUES (
  @tp_id,
  '一审稿',
  '毕业设计管理系统',
  '本文描述毕业设计管理系统的设计与实现。',
  JSON_ARRAY('毕业设计', '管理系统'),
  '一审稿内容示例。',
  '评审中',
  'about:blank'
);
SET @paper_second_id = LAST_INSERT_ID();

-- 8) 评审意见（两位评委）
INSERT INTO Second_instance_opinion (
  Paper_id,
  reviewer_id,
  Sio_opinion,
  Sio_score,
  Sio_details,
  Sio_status
) VALUES
  (@paper_second_id, 'J000004', '结构清晰，建议补充测试部分。', 86.00, JSON_OBJECT('result', '已通过'), '提交'),
  (@paper_second_id, 'J000006', '整体完整，建议优化图表说明。', 88.00, JSON_OBJECT('result', '已通过'), '提交');

-- 9) 论文终稿
INSERT INTO Paper (
  Thesis_proposal_Tp_id,
  Paper_version,
  Paper_title,
  Paper_abstract,
  Paper_keywords,
  Paper_text,
  Paper_status,
  file_url
) VALUES (
  @tp_id,
  '终稿',
  '毕业设计管理系统',
  '本文描述毕业设计管理系统的设计与实现。',
  JSON_ARRAY('毕业设计', '管理系统'),
  '终稿内容示例。',
  '已提交',
  'about:blank'
);
SET @paper_final_id = LAST_INSERT_ID();

-- 10) 答辩记录
INSERT INTO Defense_record (
  Paper_id,
  Dr_time,
  Dr_location,
  Dr_presentation_score,
  Dr_response_score,
  Dr_paper_score,
  Dr_total_score,
  Dr_record,
  Dr_comment,
  Dr_result
) VALUES (
  @paper_final_id,
  NOW(),
  'A101',
  88.00,
  85.00,
  90.00,
  87.67,
  '答辩过程顺畅，回答问题准确。',
  '建议完善部署说明。',
  '良好'
);
