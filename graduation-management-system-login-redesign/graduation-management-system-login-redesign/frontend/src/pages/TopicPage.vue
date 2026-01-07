<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>课题选择</h2>
        <p>浏览可选课题并提交意向申请。</p>
      </div>
      <RolePill role-label="学生" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>课题选择</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>课题选择</span>
          <select v-model.number="selectionForm.topic_Topic_id">
            <option disabled value="">请选择课题</option>
            <option v-for="item in items" :key="item.topic_id" :value="item.topic_id">
              {{ item.topic_name }}（{{ item.topic_id }}）
            </option>
          </select>
        </label>
        <label class="field">
          <span>志愿顺序</span>
          <input v-model.number="selectionForm.tsa_order_for_topic" placeholder="1/2/3" />
        </label>
        <div class="field">
          <span>状态</span>
          <div class="readonly">未审核</div>
        </div>
        <button class="primary" @click="createSelection">提交选择</button>
      </div>
      <p class="hint">提交后等待指导教师审核，状态会在下方列表更新。</p>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>我的选题申请</h3>
        <button class="ghost" @click="loadApplications">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>申请ID</th>
            <th>课题ID</th>
            <th>志愿顺序</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in applications" :key="item.application_id">
            <td>{{ item.application_id }}</td>
            <td>{{ item.topic_Topic_id }}</td>
            <td>{{ item.tsa_order_for_topic }}</td>
            <td>
              <span :class="statusClass(item.tsa_status)">{{ item.tsa_status }}</span>
            </td>
            <td>
              <button class="ghost danger" @click="removeApplication(item.application_id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>已发布课题</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题名称</th>
            <th>课题描述</th>
            <th>所属专业</th>
            <th>学年</th>
            <th>教师姓名</th>
          </tr>
        </thead>
        <tbody>
        <tr v-for="item in items" :key="item.topic_id">
          <td>{{ item.topic_id }}</td>
          <td>{{ item.topic_name }}</td>
          <td>{{ item.topic_description }}</td>
          <td>{{ formatMajor(item.topic_major) }}</td>
          <td>{{ item.academic_year }}</td>
          <td>{{ teacherNameMap[item.teacher_Tea_id] || '' }}</td>
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
const applications = ref([]);
const teacherNameMap = ref({});
const selectionForm = ref({
  student_Stu_id: '',
  topic_Topic_id: null,
  tsa_order_for_topic: 1,
  tsa_status: '未审核'
});

const load = async () => {
  const { data } = await api.get('/topics', {
    params: { status: '已发布' }
  });
  items.value = data.data || [];
  if (!selectionForm.value.topic_Topic_id && items.value.length > 0) {
    selectionForm.value.topic_Topic_id = items.value[0].topic_id;
  }
};

const loadApplications = async () => {
  let studentId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    studentId = stored?.username || '';
  } catch (err) {
    studentId = '';
  }
  const { data } = await api.get('/topic-applications', {
    params: { studentId }
  });
  applications.value = data.data || [];
};

const removeApplication = async (id) => {
  if (!confirm('确认删除该申请吗？')) {
    return;
  }
  const { data } = await api.delete(`/topic-applications/${id}`);
  if (!data.success) {
    alert(data.message || '删除失败');
    return;
  }
  await loadApplications();
};

const statusClass = (status) => {
  if (status === '审核通过') return 'status ok';
  if (status === '审核不通过' || status === '已拒绝') return 'status danger';
  return 'status pending';
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

const createSelection = async () => {
  try {
    if (!selectionForm.value.student_Stu_id) {
      alert('学生ID为空，请重新登录。');
      return;
    }
    if (!selectionForm.value.topic_Topic_id) {
      alert('请选择课题后再提交。');
      return;
    }
    const exists = items.value.some((item) => item.topic_id === selectionForm.value.topic_Topic_id);
    if (!exists) {
      alert('所选课题不存在或未发布。');
      return;
    }
    const { data } = await api.post('/topic-applications', {
      ...selectionForm.value,
      tsa_status: '未审核'
    });
    if (!data.success) {
      alert(data.message || '提交失败');
      return;
    }
    return data.data;
  } catch (err) {
    alert('提交失败，请稍后重试。');
    return null;
  }
};

const setStudentId = () => {
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    selectionForm.value.student_Stu_id = stored?.username || '';
  } catch (err) {
    selectionForm.value.student_Stu_id = '';
  }
};

onMounted(() => {
  setStudentId();
  load();
  loadApplications();
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
input, textarea {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
}
textarea {
  min-height: 90px;
}
select {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
  background: #ffffff;
}
.readonly {
  padding: 8px 10px;
  border: 1px dashed #cbd5e1;
  border-radius: 4px;
  background: #f8fafc;
  font-size: 13px;
  color: #475569;
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
  text-align: left;
}
.table th {
  background: #f1f7fb;
  color: #0f3a55;
}
.status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
}
.status.pending {
  background: #fef3c7;
  color: #92400e;
}
.status.ok {
  background: #dcfce7;
  color: #166534;
}
.status.danger {
  background: #fee2e2;
  color: #991b1b;
}
.hint {
  margin: 12px 0 0;
  font-size: 12px;
  color: #64748b;
}
</style>
