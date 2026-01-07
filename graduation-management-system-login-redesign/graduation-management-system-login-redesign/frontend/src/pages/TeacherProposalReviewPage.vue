<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>开题报告查询</h2>
        <p>查看选择本人课题的开题报告信息。</p>
      </div>
      <RolePill role-label="教师" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>开题报告列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>课题 ID</span>
          <input v-model.number="filters.topicId" placeholder="课题编号" />
        </label>
        <label class="field">
          <span>学生 ID</span>
          <input v-model="filters.studentId" placeholder="学号" />
        </label>
        <button class="primary" @click="load">查询</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题ID</th>
            <th>学生ID</th>
            <th>标题</th>
            <th>状态</th>
            <th>查看</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.tp_id">
            <td>{{ item.tp_id }}</td>
            <td>{{ item.topic_Topic_id }}</td>
            <td>{{ item.student_Stu_id }}</td>
            <td>{{ item.tp_title }}</td>
            <td>{{ item.tp_status }}</td>
            <td>
              <button class="ghost" @click="goDetail(item.tp_id)">查看</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const filters = ref({
  topicId: null,
  studentId: ''
});
const topicIds = ref([]);
const router = useRouter();
const load = async () => {
  const [proposals, applications] = await Promise.all([
    api.get('/thesis-proposals'),
    api.get('/topic-applications')
  ]);
  const appRows = applications.data.data || [];
  const appMap = new Map();
  appRows.forEach((row) => {
    appMap.set(row.application_id, row);
  });
  const filtered = (proposals.data.data || []).map((row) => {
    const app = appMap.get(row.topic_selection_application_id);
    return {
      ...row,
      topic_Topic_id: app?.topic_Topic_id,
      student_Stu_id: app?.student_Stu_id
    };
  }).filter((row) => topicIds.value.includes(row.topic_Topic_id));
  items.value = filtered;
};

const filteredItems = computed(() => items.value.filter((row) => {
  if (filters.value.topicId && row.topic_Topic_id !== filters.value.topicId) return false;
  if (filters.value.studentId && row.student_Stu_id !== filters.value.studentId) return false;
  return true;
}));

const loadTeacherTopics = async () => {
  let teacherId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    teacherId = stored?.username || '';
  } catch (err) {
    teacherId = '';
  }
  const { data } = await api.get('/topics', { params: { teacherId } });
  topicIds.value = (data.data || []).map((item) => item.topic_id);
};

const goDetail = (id) => {
  router.push(`/thesis-proposals/${id}`);
};

onMounted(async () => {
  await loadTeacherTopics();
  await load();
});
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
