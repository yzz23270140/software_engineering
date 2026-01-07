<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>开题报告审核</h2>
        <p>评审开题内容并给出审核结论。</p>
      </div>
      <RolePill role-label="评审组" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>开题报告审核</h3>
        <button class="ghost" @click="loadProposals">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>教师</span>
          <select v-model="filters.teacherId">
            <option value="">全部</option>
            <option v-for="item in teachers" :key="item.tea_id" :value="item.tea_id">
              {{ item.tea_name }}（{{ item.tea_id }}）
            </option>
          </select>
        </label>
        <label class="field">
          <span>学生</span>
          <select v-model="filters.studentId">
            <option value="">全部</option>
            <option v-for="item in students" :key="item.stu_id" :value="item.stu_id">
              {{ item.stu_name }}（{{ item.stu_id }}）
            </option>
          </select>
        </label>
        <label class="field">
          <span>专业</span>
          <select v-model="filters.major">
            <option value="">全部</option>
            <option v-for="item in majors" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题ID</th>
            <th>课题名称</th>
            <th>专业</th>
            <th>教师</th>
            <th>学生</th>
            <th>申请 ID</th>
            <th>标题</th>
            <th>状态</th>
            <th>审核操作</th>
            <th>查看</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredProposals" :key="item.tp_id">
            <td>{{ item.tp_id }}</td>
            <td>{{ item.topic_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.topic_major }}</td>
            <td>{{ item.teacher_name }}</td>
            <td>{{ item.student_name }}</td>
            <td>{{ item.topic_selection_application_id }}</td>
            <td>{{ item.tp_title }}</td>
            <td>{{ item.tp_status }}</td>
            <td>
              <button class="ghost" @click="updateProposal(item.tp_id, '已通过')">通过</button>
              <button class="ghost danger" @click="updateProposal(item.tp_id, '未通过')">未通过</button>
            </td>
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

const proposals = ref([]);
const teachers = ref([]);
const students = ref([]);
const majors = ref([]);
const router = useRouter();
const filters = ref({
  teacherId: '',
  studentId: '',
  major: ''
});

const loadProposals = async () => {
  const [proposalRes, applicationRes, topicRes, teacherRes, studentRes] = await Promise.all([
    api.get('/thesis-proposals'),
    api.get('/topic-applications'),
    api.get('/topics'),
    api.get('/teachers'),
    api.get('/students')
  ]);
  const apps = applicationRes.data.data || [];
  const topics = topicRes.data.data || [];
  const teacherRows = teacherRes.data.data || [];
  const studentRows = studentRes.data.data || [];

  const appMap = new Map();
  apps.forEach((row) => {
    appMap.set(row.application_id, row);
  });
  const topicMap = new Map();
  topics.forEach((row) => {
    topicMap.set(row.topic_id, row);
  });
  const teacherMap = new Map();
  teacherRows.forEach((row) => {
    teacherMap.set(row.tea_id, row.tea_name);
  });
  const studentMap = new Map();
  studentRows.forEach((row) => {
    studentMap.set(row.stu_id, row.stu_name);
  });

  proposals.value = (proposalRes.data.data || []).map((row) => {
    const app = appMap.get(row.topic_selection_application_id);
    const topic = app ? topicMap.get(app.topic_Topic_id) : null;
    return {
      ...row,
      topic_id: app?.topic_Topic_id,
      topic_name: topic?.topic_name || '',
      topic_major: formatMajor(topic?.topic_major || ''),
      teacher_id: topic?.teacher_Tea_id || '',
      teacher_name: teacherMap.get(topic?.teacher_Tea_id) || '',
      student_id: app?.student_Stu_id || '',
      student_name: studentMap.get(app?.student_Stu_id) || ''
    };
  });

  teachers.value = teacherRows;
  students.value = studentRows;
  majors.value = Array.from(new Set(topics.flatMap((t) => {
    try {
      const parsed = JSON.parse(t.topic_major || '[]');
      return Array.isArray(parsed) ? parsed : [];
    } catch (err) {
      return [];
    }
  }))).filter(Boolean);
};

const updateProposal = async (id, status) => {
  const { data } = await api.put(`/thesis-proposals/${id}`, {
    tp_status: status
  });
  if (!data.success) {
    alert(data.message || '审核失败');
    return;
  }
  await loadProposals();
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

const filteredProposals = computed(() => proposals.value.filter((item) => {
  if (filters.value.teacherId && item.teacher_id !== filters.value.teacherId) return false;
  if (filters.value.studentId && item.student_id !== filters.value.studentId) return false;
  if (filters.value.major && !item.topic_major.includes(filters.value.major)) return false;
  return true;
}));

const goDetail = (id) => {
  router.push(`/thesis-proposals/${id}`);
};

onMounted(() => {
  loadProposals();
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
