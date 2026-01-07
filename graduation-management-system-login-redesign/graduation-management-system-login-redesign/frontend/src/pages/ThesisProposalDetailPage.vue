<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>开题报告详情</h2>
        <p>查看开题报告内容与关联信息。</p>
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
          <span>报告ID</span>
          <strong>{{ detail.tp_id || '-' }}</strong>
        </div>
        <div class="meta">
          <span>选题申请ID</span>
          <strong>{{ detail.topic_selection_application_id || '-' }}</strong>
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
          <span>专业</span>
          <strong>{{ detail.topic_major || '-' }}</strong>
        </div>
        <div class="meta">
          <span>教师</span>
          <strong>{{ detail.teacher_name || '-' }}</strong>
        </div>
        <div class="meta">
          <span>学生</span>
          <strong>{{ detail.student_name || '-' }}</strong>
        </div>
        <div class="meta">
          <span>状态</span>
          <strong>{{ detail.tp_status || '-' }}</strong>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>报告内容</h3>
      </div>
      <div class="content">
        <h4>{{ detail.tp_title || '未填写标题' }}</h4>
        <p>{{ detail.tp_text || '暂无内容' }}</p>
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

const formatMajor = (value) => {
  if (!value) return '';
  try {
    const parsed = JSON.parse(value);
    if (Array.isArray(parsed)) {
      return parsed.join('、');
    }
  } catch (err) {
    return value;
  }
  return value;
};

const load = async () => {
  const id = route.params.id;
  const [proposalRes, applicationRes, topicRes, teacherRes, studentRes] = await Promise.all([
    api.get(`/thesis-proposals/${id}`),
    api.get('/topic-applications'),
    api.get('/topics'),
    api.get('/teachers'),
    api.get('/students')
  ]);

  const proposal = proposalRes.data.data || {};
  const appRows = applicationRes.data.data || [];
  const topicRows = topicRes.data.data || [];
  const teacherRows = teacherRes.data.data || [];
  const studentRows = studentRes.data.data || [];

  const app = appRows.find((row) => row.application_id === proposal.topic_selection_application_id);
  const topic = app ? topicRows.find((row) => row.topic_id === app.topic_Topic_id) : null;
  const teacherName = topic ? (teacherRows.find((row) => row.tea_id === topic.teacher_Tea_id)?.tea_name || '') : '';
  const studentName = app ? (studentRows.find((row) => row.stu_id === app.student_Stu_id)?.stu_name || '') : '';

  detail.value = {
    ...proposal,
    topic_id: app?.topic_Topic_id || '',
    topic_name: topic?.topic_name || '',
    topic_major: formatMajor(topic?.topic_major || ''),
    teacher_name: teacherName ? `${teacherName}（${topic?.teacher_Tea_id || ''}）` : '',
    student_name: studentName ? `${studentName}（${app?.student_Stu_id || ''}）` : ''
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
.content h4 {
  margin: 0 0 8px;
  font-size: 16px;
}
.content p {
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
