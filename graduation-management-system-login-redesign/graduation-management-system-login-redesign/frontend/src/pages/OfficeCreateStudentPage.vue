<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>学生账号创建</h2>
        <p>录入学生基础信息并生成账户。</p>
      </div>
      <div class="role-pill">当前身份：教科办</div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>新增学生</h3>
        <button class="ghost" @click="resetForm">清空未创建</button>
      </div>
      <div v-if="error" class="error">{{ error }}</div>
      <div class="form-grid">
        <label class="field">
          <span>学号</span>
          <input v-model="form.stu_id" placeholder="例如 S20240000001" />
        </label>
        <label class="field">
          <span>姓名</span>
          <input v-model="form.stu_name" placeholder="学生姓名" />
        </label>
        <label class="field">
          <span>学院</span>
          <select v-model="form.stu_college">
            <option disabled value="">请选择学院</option>
            <option value="计算机学院">计算机学院</option>
            <option value="卓越学院">卓越学院</option>
          </select>
        </label>
        <label class="field">
          <span>专业</span>
          <select v-model="form.stu_major">
            <option disabled value="">请选择专业</option>
            <option
              v-for="item in filteredMajors"
              :key="item"
              :value="item"
            >
              {{ item }}
            </option>
          </select>
        </label>
        <label class="field">
          <span>入学年份</span>
          <input v-model.number="form.enrollment_year" placeholder="2024" />
        </label>
        <label class="field">
          <span>辅导员 ID</span>
          <select v-model="form.instructor_Ins_id">
            <option disabled value="">请选择辅导员</option>
            <option
              v-for="item in filteredInstructors"
              :key="item.ins_id"
              :value="item.ins_id"
            >
              {{ item.ins_name }}（{{ item.ins_id }}）
            </option>
          </select>
        </label>
        <label class="field">
          <span>指导教师 ID</span>
          <select v-model="form.teacher_Tea_id">
            <option disabled value="">请选择指导教师</option>
            <option
              v-for="item in teachers"
              :key="item.tea_id"
              :value="item.tea_id"
            >
              {{ item.tea_name }}（{{ item.tea_id }}）
            </option>
          </select>
        </label>
        <button class="primary" @click="createItem">创建学生</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>学生列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>学号</th>
            <th>姓名</th>
            <th>学院</th>
            <th>专业</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.stu_id">
            <td>{{ item.stu_id }}</td>
            <td>{{ item.stu_name }}</td>
            <td>{{ item.stu_college }}</td>
            <td>{{ item.stu_major }}</td>
            <td>
              <button class="ghost" @click="removeItem(item.stu_id)">删除</button>
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
const instructors = ref([]);
const teachers = ref([]);
const error = ref('');
const form = ref({
  stu_id: '',
  stu_name: '',
  stu_college: '',
  stu_major: '',
  enrollment_year: 2024,
  instructor_Ins_id: '',
  teacher_Tea_id: ''
});

const filteredInstructors = computed(() =>
  instructors.value.filter((item) => item.ins_college === form.value.stu_college)
);

const majorsByCollege = {
  '卓越学院': ['计算机科学与技术', 'EDA'],
  '计算机学院': ['计算机科学', '软件工程', '人工智能']
};

const filteredMajors = computed(() => majorsByCollege[form.value.stu_college] || []);

const load = async () => {
  const { data } = await api.get('/students');
  items.value = data.data || [];
};

const loadInstructors = async () => {
  const { data } = await api.get('/instructors');
  instructors.value = data.data || [];
};

const loadTeachers = async () => {
  const { data } = await api.get('/teachers');
  teachers.value = data.data || [];
};

const createItem = async () => {
  error.value = '';
  try {
    const { data } = await api.post('/students', form.value);
    if (!data.success) {
      error.value = data.message || '创建失败';
      return;
    }
    await load();
  } catch (err) {
    error.value = '创建失败，请检查辅导员/指导教师ID是否存在。';
  }
};

const resetForm = () => {
  form.value = {
    stu_id: '',
    stu_name: '',
    stu_college: '',
    stu_major: '',
    enrollment_year: 2024,
    instructor_Ins_id: '',
    teacher_Tea_id: ''
  };
  error.value = '';
};

const removeItem = async (id) => {
  if (!confirm('确认删除该学生吗？')) {
    return;
  }
  error.value = '';
  try {
    const { data } = await api.delete(`/students/${id}`);
    if (!data.success) {
      error.value = data.message || '删除失败';
      return;
    }
    await load();
  } catch (err) {
    error.value = '删除失败，请稍后重试。';
  }
};

watch(
  () => form.value.stu_college,
  () => {
    form.value.instructor_Ins_id = '';
    form.value.stu_major = '';
  }
);

onMounted(() => {
  load();
  loadInstructors();
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
.error {
  margin-top: 12px;
  color: #dc2626;
  font-size: 13px;
}
</style>
