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
  '智能选题推荐模块设计',
  '基于历史数据与偏好进行选题推荐与流程管理。',
  '科研项目',
  'T000101',
  JSON_ARRAY('软件工程', '人工智能'),
  '已发布',
  '审核通过',
  '2025-2026'
);
SET @topic_id = LAST_INSERT_ID();

-- 2) 选题申请（已通过）
INSERT INTO Topic_selection_application (
  Student_Stu_id,
  Topic_Topic_id,
  Tsa_order_for_topic,
  Tsa_status
) VALUES (
  'S20250000001',
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
  '开题报告：选题推荐模块设计',
  '报告涵盖研究背景、方案与实施计划。',
  '已通过',
  'J200001',
  90.00,
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
  '已完成推荐模型与基础界面开发。',
  '继续优化算法与联调。',
  '已通过',
  'J300001',
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
  '线上会议',
  '讨论推荐逻辑与数据一致性。',
  '已确认',
  'S20250000001',
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
  '选题推荐模块设计',
  '本文描述推荐模块设计与实现。',
  JSON_ARRAY('推荐系统', '毕业设计'),
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
  '选题推荐模块设计',
  '本文描述推荐模块设计与实现。',
  JSON_ARRAY('推荐系统', '毕业设计'),
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
  (@paper_second_id, 'J400001', '结构完整，建议丰富实验。', 87.00, JSON_OBJECT('result', '已通过'), '提交'),
  (@paper_second_id, 'J400002', '内容可读性高，建议补充对比。', 89.00, JSON_OBJECT('result', '已通过'), '提交');

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
  '选题推荐模块设计',
  '本文描述推荐模块设计与实现。',
  JSON_ARRAY('推荐系统', '毕业设计'),
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
  'B201',
  89.00,
  86.00,
  90.00,
  88.33,
  '答辩过程条理清晰。',
  '建议补充部署说明。',
  '良好'
);
