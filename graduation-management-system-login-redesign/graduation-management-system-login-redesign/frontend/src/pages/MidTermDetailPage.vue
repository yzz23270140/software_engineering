<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>中期汇报详情</h2>
        <p>查看中期汇报内容与关联信息。</p>
      </div>
      <RolePill role-label="教师/评审组" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>基本信息</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <div class="meta-grid">
        <div class="meta">
          <span>汇报ID</span>
          <strong>{{ detail.mtr_id || '-' }}</strong>
        </div>
        <div class="meta">
          <span>开题ID</span>
          <strong>{{ detail.thesis_proposal_Tp_id || '-' }}</strong>
        </div>
        <div class="meta">
          <span>课题ID</span>
          <strong>{{ detail.topic_id || '-' }}</strong>
        </div>
        <div class="meta">
          <span>课题名称</span>
          <strong>{{ detail.topic_name || '-' }}</strong>
        </div>
        <div class="meta">
          <span>学生</span>
          <strong>{{ detail.student_name || '-' }}</strong>
        </div>
        <div class="meta">
          <span>教师</span>
          <strong>{{ detail.teacher_name || '-' }}</strong>
        </div>
        <div class="meta">
          <span>提交时间</span>
          <strong>{{ detail.mtr_submission_time || '-' }}</strong>
        </div>
        <div class="meta">
          <span>状态</span>
          <strong>{{ formatStatus(detail.mtr_status) || '-' }}</strong>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>汇报内容</h3>
      </div>
      <div class="content">
        <div class="content-block">
          <h4>进展情况</h4>
          <p>{{ detail.mtr_progress || '暂无内容' }}</p>
        </div>
        <div class="content-block">
          <h4>存在问题</h4>
          <p>{{ detail.mtr_problems || '暂无内容' }}</p>
        </div>
        <div class="content-block">
          <h4>后续计划</h4>
          <p>{{ detail.mtr_plan || '暂无内容' }}</p>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>检查信息</h3>
      </div>
      <div class="meta-grid">
        <div class="meta">
          <span>检查意见</span>
          <strong>{{ detail.mtr_comment || '-' }}</strong>
        </div>
        <div class="meta">
          <span>检查评价</span>
          <strong>{{ detail.mtr_evaluation || '-' }}</strong>
        </div>
        <div class="meta">
          <span>检查人</span>
          <strong>{{ detail.checker_name || '-' }}</strong>
        </div>
        <div class="meta">
          <span>检查时间</span>
          <strong>{{ detail.check_time || '-' }}</strong>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const route = useRoute();
const detail = ref({});

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const formatStatus = (value) => {
  if (value === '已通过') return '通过';
  if (value === '未通过') return '不通过';
  return value || '';
};

const load = async () => {
  const id = route.params.id;
  const [midtermRes, proposalRes, applicationRes, topicRes, teacherRes, studentRes, judgeRes] = await Promise.all([
    api.get(`/midterm-reports/${id}`),
    api.get('/thesis-proposals'),
    api.get('/topic-applications'),
    api.get('/topics'),
    api.get('/teachers'),
    api.get('/students'),
    api.get('/judges')
  ]);

  const midterm = midtermRes.data.data || {};
  const proposals = proposalRes.data.data || [];
  const apps = applicationRes.data.data || [];
  const topics = topicRes.data.data || [];
  const teachers = teacherRes.data.data || [];
  const students = studentRes.data.data || [];
  const judges = judgeRes.data.data || [];

  const proposal = proposals.find((row) => row.tp_id === midterm.thesis_proposal_Tp_id);
  const app = proposal ? apps.find((row) => row.application_id === proposal.topic_selection_application_id) : null;
  const topic = app ? topics.find((row) => row.topic_id === app.topic_Topic_id) : null;
  const teacherName = topic ? (teachers.find((row) => row.tea_id === topic.teacher_Tea_id)?.tea_name || '') : '';
  const studentName = app ? (students.find((row) => row.stu_id === app.student_Stu_id)?.stu_name || '') : '';
  const checkerName = midterm.checker_id
    ? (judges.find((row) => row.jud_id === midterm.checker_id)?.jud_name || '')
    : '';

  detail.value = {
    ...midterm,
    topic_id: app?.topic_Topic_id || '',
    topic_name: topic?.topic_name || '',
    teacher_name: teacherName ? `${teacherName}（${topic?.teacher_Tea_id || ''}）` : '',
    student_name: studentName ? `${studentName}（${app?.student_Stu_id || ''}）` : '',
    checker_name: checkerName ? `${checkerName}（${midterm.checker_id}）` : '',
    mtr_submission_time: formatTime(midterm.mtr_submission_time),
    check_time: formatTime(midterm.check_time)
  };
};

onMounted(load);
</script>

<style scoped>
.page {
  display: grid;
  gap: 18px;
}
.page-header {
  background: #016fa0;
  color: #ffffff;
  padding: 20px 24px;
  border-radius: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page-header h2 {
  margin: 0 0 6px;
  font-size: 22px;
}
.page-header p {
  margin: 0;
  font-size: 13px;
  opacity: 0.9;
}
.panel {
  background: #ffffff;
  border: 1px solid #c9d6e1;
  border-radius: 6px;
  padding: 16px 18px;
  box-shadow: 0 8px 20px rgba(15, 55, 78, 0.08);
}
.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.panel-head h3 {
  margin: 0;
  font-size: 18px;
}
.meta-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}
.meta {
  display: grid;
  gap: 4px;
  font-size: 13px;
  color: #1f2937;
}
.meta span {
  color: #64748b;
  font-size: 12px;
}
.content {
  display: grid;
  gap: 14px;
}
.content-block h4 {
  margin: 0 0 6px;
  font-size: 15px;
}
.content-block p {
  margin: 0;
  line-height: 1.6;
  white-space: pre-wrap;
}
.ghost {
  background: #eff6fb;
  border: 1px solid #cfe1ee;
  color: #0f3a55;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}
</style>
