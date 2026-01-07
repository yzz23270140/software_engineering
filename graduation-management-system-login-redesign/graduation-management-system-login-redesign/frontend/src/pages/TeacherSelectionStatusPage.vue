<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>选题申请审批</h2>
        <p>查看并审批学生选题申请。</p>
      </div>
      <RolePill role-label="教师" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>选题申请列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>学生 ID</span>
          <input v-model="filters.studentId" placeholder="学号" />
        </label>
        <label class="field">
          <span>学生姓名</span>
          <input v-model="filters.studentName" placeholder="姓名" />
        </label>
        <label class="field">
          <span>课题名称</span>
          <input v-model="filters.topicName" placeholder="课题名称" />
        </label>
        <label class="field">
          <span>志愿顺序</span>
          <input v-model.number="filters.order" placeholder="1/2/3" />
        </label>
        <label class="field">
          <span>状态</span>
          <select v-model="filters.status">
            <option value="">全部</option>
            <option value="未审核">未审核</option>
            <option value="审核通过">审核通过</option>
            <option value="审核不通过">审核不通过</option>
          </select>
        </label>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>学生</th>
            <th>学生姓名</th>
            <th>课题</th>
            <th>课题名称</th>
            <th>志愿顺序</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.application_id">
            <td>{{ item.application_id }}</td>
            <td>{{ item.student_Stu_id }}</td>
            <td>{{ item.student_name }}</td>
            <td>{{ item.topic_Topic_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.tsa_order_for_topic }}</td>
            <td>{{ item.tsa_status }}</td>
            <td>
              <button class="ghost" @click="updateStatus(item.application_id, '审核通过')">通过</button>
              <button class="ghost danger" @click="updateStatus(item.application_id, '审核不通过')">驳回</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const filteredItems = computed(() => items.value.filter((row) => {
  if (filters.value.studentId && row.student_Stu_id !== filters.value.studentId) return false;
  if (filters.value.studentName && !row.student_name.includes(filters.value.studentName)) return false;
  if (filters.value.topicName && !row.topic_name.includes(filters.value.topicName)) return false;
  if (filters.value.order && Number(row.tsa_order_for_topic) !== Number(filters.value.order)) return false;
  if (filters.value.status && row.tsa_status !== filters.value.status) return false;
  return true;
}));
const teacherTopicIds = ref([]);
const filters = ref({
  studentId: '',
  studentName: '',
  topicName: '',
  order: null,
  status: ''
});

const load = async () => {
  const [appRes, topicRes, studentRes] = await Promise.all([
    api.get('/topic-applications'),
    api.get('/topics'),
    api.get('/students')
  ]);
  const list = appRes.data.data || [];
  const topics = topicRes.data.data || [];
  const students = studentRes.data.data || [];
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row.topic_name));
  const studentMap = new Map();
  students.forEach((row) => studentMap.set(row.stu_id, row.stu_name));
  const filtered = teacherTopicIds.value.length > 0
    ? list.filter((row) => teacherTopicIds.value.includes(row.topic_Topic_id))
    : list;
  items.value = filtered.map((row) => ({
    ...row,
    topic_name: topicMap.get(row.topic_Topic_id) || '',
    student_name: studentMap.get(row.student_Stu_id) || ''
  }));
};

const loadTeacherTopics = async () => {
  let teacherId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    teacherId = stored?.username || '';
  } catch (err) {
    teacherId = '';
  }
  if (!teacherId) {
    teacherTopicIds.value = [];
    return;
  }
  const { data } = await api.get('/topics', { params: { teacherId } });
  teacherTopicIds.value = (data.data || []).map((item) => item.topic_id);
};

const updateStatus = async (id, status) => {
  await api.put(`/topic-applications/${id}`, {
    tsa_status: status
  });
  await load();
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
.danger {
  background: #fee2e2;
  border: 1px solid #fecaca;
  color: #991b1b;
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
