<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>中期检查</h2>
        <p>查看中期汇报并给出检查结论。</p>
      </div>
      <RolePill role-label="评审组" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>中期报告列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>学生</th>
            <th>提交时间</th>
            <th>进展</th>
            <th>状态</th>
            <th>审核操作</th>
            <th>查看</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.mtr_id">
            <td>{{ item.mtr_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.student_name }}</td>
            <td>{{ item.mtr_submission_time }}</td>
            <td>{{ item.mtr_progress }}</td>
            <td>{{ formatStatus(item.mtr_status) }}</td>
            <td>
              <button class="ghost" @click="updateStatus(item.mtr_id, '已通过')">通过</button>
              <button class="ghost danger" @click="updateStatus(item.mtr_id, '未通过')">不通过</button>
            </td>
            <td>
              <button class="ghost" @click="goDetail(item.mtr_id)">查看</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const router = useRouter();

const load = async () => {
  const [midtermRes, proposalRes, applicationRes, topicRes, studentRes] = await Promise.all([
    api.get('/midterm-reports'),
    api.get('/thesis-proposals'),
    api.get('/topic-applications'),
    api.get('/topics'),
    api.get('/students')
  ]);
  const proposals = proposalRes.data.data || [];
  const apps = applicationRes.data.data || [];
  const topics = topicRes.data.data || [];
  const students = studentRes.data.data || [];

  const appMap = new Map();
  apps.forEach((row) => appMap.set(row.application_id, row));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));
  const studentMap = new Map();
  students.forEach((row) => studentMap.set(row.stu_id, row.stu_name));

  items.value = (midtermRes.data.data || []).map((row) => {
    const proposal = proposals.find((p) => p.tp_id === row.thesis_proposal_Tp_id);
    const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
    const topic = app ? topicMap.get(app.topic_Topic_id) : null;
    const studentName = app ? (studentMap.get(app.student_Stu_id) || '') : '';
    return {
      ...row,
      topic_name: topic?.topic_name || '',
      student_name: studentName ? `${studentName}（${app?.student_Stu_id || ''}）` : '',
      mtr_submission_time: formatTime(row.mtr_submission_time)
    };
  });
};

const updateStatus = async (id, status) => {
  let judgeId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    judgeId = stored?.username || '';
  } catch (err) {
    judgeId = '';
  }
  const { data } = await api.put(`/midterm-reports/${id}`, {
    mtr_status: status,
    checker_id: judgeId
  });
  if (!data.success) {
    alert(data.message || '更新失败');
    return;
  }
  await load();
};

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const formatStatus = (value) => {
  if (value === '已通过') return '通过';
  if (value === '未通过') return '不通过';
  return value || '';
};

const goDetail = (id) => {
  router.push(`/midterm-reports/${id}`);
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
.role-pill {
  background: #1e98d7;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
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
.form-grid {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
  gap: 12px;
}
.field {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: #1f2937;
}
input {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
}
select {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
  background: #ffffff;
}
button {
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}
.primary {
  background: #1e98d7;
  border: none;
  color: #ffffff;
  justify-self: end;
  width: 120px;
}
.ghost {
  background: #eff6fb;
  border: 1px solid #cfe1ee;
  color: #0f3a55;
}
.ghost.danger {
  border-color: #fecaca;
  color: #b91c1c;
  background: #fff1f2;
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
</style>
