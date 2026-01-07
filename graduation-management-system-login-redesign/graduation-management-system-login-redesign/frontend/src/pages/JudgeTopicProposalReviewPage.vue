<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>课题审核</h2>
        <p>审题组对教师申报课题进行审批。</p>
      </div>
      <RolePill role-label="评审组" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>课题审核</h3>
        <button class="ghost" @click="loadTopics">刷新课题</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题名称</th>
            <th>描述</th>
            <th>类型</th>
            <th>专业</th>
            <th>学年</th>
            <th>申报教师</th>
            <th>教师姓名</th>
            <th>提交时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in topics" :key="item.topic_id">
            <td>{{ item.topic_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.topic_description }}</td>
            <td>{{ item.topic_type }}</td>
            <td>{{ formatMajor(item.topic_major) }}</td>
            <td>{{ item.academic_year }}</td>
            <td>{{ item.teacher_Tea_id }}</td>
            <td>{{ teacherNameMap[item.teacher_Tea_id] || '' }}</td>
            <td>{{ item.created_at }}</td>
            <td>
              <button class="ghost" @click="quickUpdateTopic(item.topic_id, '已发布')">通过</button>
              <button class="ghost danger" @click="quickUpdateTopic(item.topic_id, '已驳回')">驳回</button>
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

const topics = ref([]);
const teacherNameMap = ref({});

const loadTopics = async () => {
  const { data } = await api.get('/topics', {
    params: { status: '审核中' }
  });
  topics.value = data.data || [];
};

const loadTeachers = async () => {
  const { data } = await api.get('/teachers');
  const map = {};
  (data.data || []).forEach((item) => {
    map[item.tea_id] = item.tea_name;
  });
  teacherNameMap.value = map;
};

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


const quickUpdateTopic = async (id, status) => {
  await api.put(`/topics/${id}`, { topic_status: status });
  await loadTopics();
};


onMounted(() => {
  loadTopics();
  loadTeachers();
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
