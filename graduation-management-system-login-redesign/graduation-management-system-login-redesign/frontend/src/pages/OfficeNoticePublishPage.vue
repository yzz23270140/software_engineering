<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>通知发布</h2>
        <p>创建并发布系统通知。</p>
      </div>
      <div class="role-pill">当前身份：教科办</div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>发布通知</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>通知类型</span>
          <select v-model="form.notice_type">
            <option value="系统通知">系统通知</option>
            <option value="选题通知">选题通知</option>
            <option value="开题通知">开题通知</option>
            <option value="中期检查通知">中期检查通知</option>
            <option value="答辩通知">答辩通知</option>
            <option value="成绩发布">成绩发布</option>
          </select>
        </label>
        <label class="field">
          <span>通知标题</span>
          <input v-model="form.notice_title" placeholder="标题" />
        </label>
        <label class="field field-wide">
          <span>学生筛选（仅用于选择接收人）</span>
          <div class="filter-grid">
            <select v-model="filters.college">
              <option value="">全部学院</option>
              <option v-for="item in colleges" :key="item" :value="item">{{ item }}</option>
            </select>
            <select v-model="filters.major">
              <option value="">全部专业</option>
              <option v-for="item in majors" :key="item" :value="item">{{ item }}</option>
            </select>
            <select v-model="filters.year">
              <option value="">全部年级</option>
              <option v-for="item in years" :key="item" :value="item">{{ item }}</option>
            </select>
            <select v-model="filters.teacherId">
              <option value="">全部教师</option>
              <option v-for="item in teachers" :key="item.tea_id" :value="item.tea_id">
                {{ item.tea_name }}（{{ item.tea_id }}）
              </option>
            </select>
            <select v-model="filters.instructorId">
              <option value="">全部辅导员</option>
              <option v-for="item in instructors" :key="item.ins_id" :value="item.ins_id">
                {{ item.ins_name }}（{{ item.ins_id }}）
              </option>
            </select>
          </div>
        </label>
        <label class="field field-wide">
          <span>发送对象</span>
          <div class="recipient-tools">
            <label class="inline-toggle">
              <input type="checkbox" v-model="sendAll" />
              全部学生
            </label>
            <button class="ghost" type="button" @click="selectAllVisible" :disabled="sendAll">全选当前筛选</button>
            <button class="ghost" type="button" @click="clearSelection" :disabled="sendAll">清空选择</button>
            <span class="count">已选 {{ selectedStudentIds.length }} 人</span>
          </div>
          <div class="student-list" :class="{ disabled: sendAll }">
            <label v-for="item in filteredStudents" :key="item.stu_id" class="student-item">
              <input
                type="checkbox"
                :value="item.stu_id"
                v-model="selectedStudentIds"
                :disabled="sendAll"
              />
              {{ item.stu_name }}（{{ item.stu_id }}）
            </label>
          </div>
        </label>
        <label class="field field-wide">
          <span>通知内容</span>
          <textarea v-model="form.notice_content" placeholder="发布内容"></textarea>
        </label>
        <button class="primary" @click="createItem">发布</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>通知列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>类型</th>
            <th>发布时间</th>
            <th>内容</th>
            <th>接收人</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.notice_id">
            <td>{{ item.notice_id }}</td>
            <td>{{ item.notice_title }}</td>
            <td>{{ item.notice_type }}</td>
            <td>{{ formatTime(item.notice_time) }}</td>
            <td>{{ item.notice_content }}</td>
            <td>{{ formatRecipients(item.notice_id) }}</td>
            <td>
              <button class="ghost" @click="withdraw(item.notice_id)">撤回</button>
            </td>
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
const students = ref([]);
const teachers = ref([]);
const instructors = ref([]);
const selectedStudentIds = ref([]);
const sendAll = ref(false);
const recipients = ref([]);
const recipientMap = ref(new Map());
const form = ref({
  notice_type: '系统通知',
  notice_title: '',
  notice_content: ''
});
const filters = ref({
  college: '',
  major: '',
  year: '',
  teacherId: '',
  instructorId: ''
});

const load = async () => {
  const [noticeRes, studentRes, teacherRes, instructorRes, recipientRes] = await Promise.all([
    api.get('/notices'),
    api.get('/students'),
    api.get('/teachers'),
    api.get('/instructors'),
    api.get('/notice-recipients')
  ]);
  items.value = noticeRes.data.data || [];
  students.value = studentRes.data.data || [];
  teachers.value = teacherRes.data.data || [];
  instructors.value = instructorRes.data.data || [];
  recipients.value = recipientRes.data.data || [];
  const map = new Map();
  recipients.value.forEach((row) => {
    const list = map.get(row.notice_id) || [];
    list.push(row.student_Stu_id);
    map.set(row.notice_id, list);
  });
  recipientMap.value = map;
};

const createItem = async () => {
  if (!sendAll.value && selectedStudentIds.value.length === 0) {
    alert('请至少选择一名学生或勾选全部学生');
    return;
  }
  const payload = {
    ...form.value,
    send_all: sendAll.value,
    student_ids: sendAll.value ? [] : selectedStudentIds.value
  };
  await api.post('/notices', payload);
  await load();
  selectedStudentIds.value = [];
  sendAll.value = false;
};

const colleges = computed(() => {
  const set = new Set();
  students.value.forEach((row) => row.stu_college && set.add(row.stu_college));
  return Array.from(set);
});

const majors = computed(() => {
  const set = new Set();
  students.value.forEach((row) => row.stu_major && set.add(row.stu_major));
  return Array.from(set);
});

const years = computed(() => {
  const set = new Set();
  students.value.forEach((row) => row.enrollment_year && set.add(row.enrollment_year));
  return Array.from(set).sort();
});

const filteredStudents = computed(() => {
  return students.value.filter((row) => {
    if (filters.value.college && row.stu_college !== filters.value.college) return false;
    if (filters.value.major && row.stu_major !== filters.value.major) return false;
    if (filters.value.year && String(row.enrollment_year) !== String(filters.value.year)) return false;
    if (filters.value.teacherId && row.teacher_Tea_id !== filters.value.teacherId) return false;
    if (filters.value.instructorId && row.instructor_Ins_id !== filters.value.instructorId) return false;
    return true;
  });
});

const selectAllVisible = () => {
  selectedStudentIds.value = filteredStudents.value.map((row) => row.stu_id);
};

const clearSelection = () => {
  selectedStudentIds.value = [];
};

const withdraw = async (noticeId) => {
  await api.delete(`/notices/${noticeId}`);
  await load();
};

const formatRecipients = (noticeId) => {
  const list = recipientMap.value.get(noticeId) || [];
  if (!list.length) return '全部学生';
  return `指定 ${list.length} 人`;
};

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

watch(filteredStudents, (list) => {
  const visibleIds = new Set(list.map((row) => row.stu_id));
  selectedStudentIds.value = selectedStudentIds.value.filter((id) => visibleIds.has(id));
});

watch(sendAll, (value) => {
  if (value) {
    selectedStudentIds.value = [];
  }
});

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
.field-wide {
  grid-column: 1 / -1;
}
input, textarea, select {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
  resize: vertical;
  min-height: 36px;
}
textarea {
  min-height: 90px;
}
.filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 10px;
}
.recipient-tools {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}
.inline-toggle {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}
.count {
  font-size: 12px;
  color: #475569;
}
.student-list {
  margin-top: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 10px;
  max-height: 220px;
  overflow: auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 8px;
}
.student-list.disabled {
  opacity: 0.5;
  pointer-events: none;
}
.student-item {
  display: flex;
  gap: 6px;
  align-items: center;
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
  vertical-align: top;
}
.table th {
  background: #f1f7fb;
  color: #0f3a55;
}
</style>
