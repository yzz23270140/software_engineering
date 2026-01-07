<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>中期指导确认</h2>
        <p>查看教师指导记录并完成确认。</p>
      </div>
      <RolePill role-label="学生" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>指导记录</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>教师</th>
            <th>指导时间</th>
            <th>方式</th>
            <th>确认状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.gr_id">
            <td>{{ item.gr_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.teacher_name }}</td>
            <td>{{ item.gr_time }}</td>
            <td>{{ item.gr_method }}</td>
            <td>{{ item.gr_confirm }}</td>
            <td>
              <button
                v-if="item.gr_confirm !== '已确认'"
                class="ghost"
                @click="confirmRecord(item.gr_id)"
              >
                确认
              </button>
              <span v-else class="muted">已确认</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>指导内容</h3>
      </div>
      <div class="content-grid">
        <div v-for="item in items" :key="`detail-${item.gr_id}`" class="content-card">
          <div class="content-title">
            {{ item.topic_name }}（{{ item.gr_time }}）
          </div>
          <p><strong>指导记录：</strong>{{ item.gr_record || '暂无' }}</p>
          <p><strong>指导意见：</strong>{{ item.gr_suggestions || '暂无' }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const load = async () => {
  let studentId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    studentId = stored?.username || '';
  } catch (err) {
    studentId = '';
  }
  const [guidanceRes, proposalRes, applicationRes, topicRes, teacherRes] = await Promise.all([
    api.get('/guidance-records'),
    api.get('/thesis-proposals'),
    api.get('/topic-applications', { params: { studentId } }),
    api.get('/topics'),
    api.get('/teachers')
  ]);

  const proposals = proposalRes.data.data || [];
  const apps = applicationRes.data.data || [];
  const topics = topicRes.data.data || [];
  const teachers = teacherRes.data.data || [];

  const appMap = new Map();
  apps.forEach((row) => appMap.set(row.application_id, row));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));
  const teacherMap = new Map();
  teachers.forEach((row) => teacherMap.set(row.tea_id, row.tea_name));

  const proposalIds = new Set(
    proposals
      .filter((row) => row.tp_status === '已通过' && appMap.has(row.topic_selection_application_id))
      .map((row) => row.tp_id)
  );

  items.value = (guidanceRes.data.data || [])
    .filter((row) => proposalIds.has(row.thesis_proposal_Tp_id))
    .map((row) => {
      const proposal = proposals.find((p) => p.tp_id === row.thesis_proposal_Tp_id);
      const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
      const topic = app ? topicMap.get(app.topic_Topic_id) : null;
      const teacherName = topic ? teacherMap.get(topic.teacher_Tea_id) : '';
      return {
        ...row,
        topic_name: topic?.topic_name || '',
        teacher_name: teacherName ? `${teacherName}（${topic?.teacher_Tea_id || ''}）` : '',
        gr_time: formatTime(row.gr_time)
      };
    });
};

const confirmRecord = async (id) => {
  let studentId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    studentId = stored?.username || '';
  } catch (err) {
    studentId = '';
  }
  const { data } = await api.put(`/guidance-records/${id}`, {
    gr_confirm: '已确认',
    confirmed_by: studentId
  });
  if (!data.success) {
    alert(data.message || '确认失败');
    return;
  }
  await load();
};

load();
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
  font-size: 24px;
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
.table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 12px;
  font-size: 13px;
}
.table th, .table td {
  border: 1px solid #e2e8f0;
  padding: 8px 10px;
}
.table th {
  background: #f1f7fb;
  color: #0f3a55;
}
.ghost {
  background: #eff6fb;
  border: 1px solid #cfe1ee;
  color: #0f3a55;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}
.muted {
  color: #64748b;
}
.content-grid {
  margin-top: 12px;
  display: grid;
  gap: 12px;
}
.content-card {
  border: 1px dashed #cbd5e1;
  padding: 12px;
  border-radius: 6px;
  background: #f8fafc;
  font-size: 13px;
}
.content-title {
  font-weight: 600;
  margin-bottom: 6px;
}
.content-card p {
  margin: 4px 0;
}
</style>
