<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>通知公告</h2>
        <p>系统发布的最新通知。</p>
      </div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>通知列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>通知类型</span>
          <select v-model="filterType">
            <option value="">全部</option>
            <option v-for="item in typeOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>类型</th>
            <th>时间</th>
            <th>内容</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in displayItems" :key="item.notice_id">
            <td>{{ item.notice_id }}</td>
            <td>{{ item.notice_title }}</td>
            <td>{{ item.notice_type }}</td>
            <td>{{ formatTime(item.notice_time) }}</td>
            <td>{{ item.notice_content }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import api from '../api';

const items = ref([]);
const filteredItems = ref([]);
const displayItems = ref([]);
const filterType = ref('');
const studentInfo = ref(null);
const currentRole = ref('');
const currentId = ref('');
const recipients = ref([]);
const recipientMap = ref(new Map());

const load = async () => {
  let noticeRes;
  let recipientRes;
  try {
    [noticeRes, recipientRes] = await Promise.all([
      api.get('/notices'),
      api.get('/notice-recipients')
    ]);
  } catch (err) {
    noticeRes = await api.get('/notices');
    recipientRes = { data: { data: [] } };
  }
  items.value = noticeRes.data.data || [];
  recipients.value = recipientRes.data.data || [];
  const map = new Map();
  recipients.value.forEach((row) => {
    const list = map.get(row.notice_id) || [];
    list.push(row.student_Stu_id);
    map.set(row.notice_id, list);
  });
  recipientMap.value = map;
  if (currentRole.value === 'student' && studentInfo.value) {
    filteredItems.value = items.value.filter((item) => matchNotice(item, studentInfo.value));
  } else {
    filteredItems.value = items.value;
  }
  applyFilters();
};

const typeOptions = computed(() => {
  const set = new Set();
  filteredItems.value.forEach((item) => {
    if (item.notice_type) set.add(item.notice_type);
  });
  return Array.from(set);
});

const applyFilters = () => {
  displayItems.value = filteredItems.value.filter((item) => {
    if (filterType.value && item.notice_type !== filterType.value) return false;
    return true;
  });
};

const matchNotice = (notice, student) => {
  const targetedStudents = recipientMap.value.get(notice.notice_id);
  if (targetedStudents && targetedStudents.length) {
    return targetedStudents.includes(student.stu_id);
  }
  if (notice.notice_target_student_id && notice.notice_target_student_id !== student.stu_id) {
    return false;
  }
  if (notice.notice_target_college && notice.notice_target_college !== student.stu_college) {
    return false;
  }
  if (notice.notice_target_major && notice.notice_target_major !== student.stu_major) {
    return false;
  }
  if (notice.notice_target_year && Number(notice.notice_target_year) !== Number(student.enrollment_year)) {
    return false;
  }
  if (notice.notice_target_teacher_id && notice.notice_target_teacher_id !== student.teacher_Tea_id) {
    return false;
  }
  if (notice.notice_target_instructor_id && notice.notice_target_instructor_id !== student.instructor_Ins_id) {
    return false;
  }
  return true;
};

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const loadStudentInfo = async () => {
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    currentRole.value = stored?.role || '';
    currentId.value = stored?.username || '';
  } catch (err) {
    currentRole.value = '';
    currentId.value = '';
  }
  if (currentRole.value !== 'student' || !currentId.value) {
    studentInfo.value = null;
    return;
  }
  const { data } = await api.get('/students');
  studentInfo.value = (data.data || []).find((row) => row.stu_id === currentId.value) || null;
};

onMounted(async () => {
  await loadStudentInfo();
  await load();
});

watch([filterType, filteredItems], () => {
  applyFilters();
}, { deep: true });
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
.form-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}
.field {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: #1f2937;
}
select {
  padding: 6px 8px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
  background: #ffffff;
}
.panel-head h3 {
  margin: 0;
  font-size: 18px;
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
.table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 12px;
  font-size: 13px;
}
.table th, .table td {
  border: 1px solid #e2e8f0;
  padding: 8px 10px;
  vertical-align: top;
}
.table th {
  background: #f1f7fb;
  color: #0f3a55;
}
</style>
