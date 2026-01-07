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
        <h3>查询条件</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>学生 ID</span>
          <input v-model="filters.studentId" placeholder="学号" />
        </label>
        <label class="field">
          <span>课题 ID</span>
          <input v-model.number="filters.topicId" placeholder="课题编号" />
        </label>
        <label class="field">
          <span>状态</span>
          <input v-model="filters.status" placeholder="未审核/审核通过" />
        </label>
        <button class="primary" @click="load">查询</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>选题申请列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>学生</th>
            <th>课题</th>
            <th>志愿顺序</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.application_id">
            <td>{{ item.application_id }}</td>
            <td>{{ item.student_Stu_id }}</td>
            <td>{{ item.topic_Topic_id }}</td>
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
import { ref, onMounted } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const teacherTopicIds = ref([]);
const filters = ref({
  studentId: '',
  topicId: null,
  status: ''
});

const load = async () => {
  const params = {};
  if (filters.value.studentId) {
    params.studentId = filters.value.studentId;
  }
  if (filters.value.topicId) {
    params.topicId = filters.value.topicId;
  }
  if (filters.value.status) {
    params.status = filters.value.status;
  }
  const { data } = await api.get('/topic-applications', { params });
  const list = data.data || [];
  if (teacherTopicIds.value.length > 0) {
    items.value = list.filter((row) => teacherTopicIds.value.includes(row.topic_Topic_id));
  } else {
    items.value = list;
  }
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

onMounted(() => {
  loadTeacherTopics();
  load();
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
