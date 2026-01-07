<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>辅导员账号创建</h2>
        <p>录入辅导员基础信息。</p>
      </div>
      <div class="role-pill">当前身份：教科办</div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>新增辅导员</h3>
        <button class="ghost" @click="resetForm">清空未创建</button>
      </div>
      <div v-if="error" class="error">{{ error }}</div>
      <div v-if="success" class="success">{{ success }}</div>
      <div class="form-grid">
        <label class="field">
          <span>辅导员 ID</span>
          <input v-model="form.ins_id" placeholder="例如 I000001" />
        </label>
        <label class="field">
          <span>姓名</span>
          <input v-model="form.ins_name" placeholder="辅导员姓名" />
        </label>
        <label class="field">
          <span>学院</span>
          <select v-model="form.ins_college">
            <option disabled value="">请选择学院</option>
            <option value="计算机学院">计算机学院</option>
            <option value="卓越学院">卓越学院</option>
          </select>
        </label>
        <button class="primary" @click="createItem">创建辅导员</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>辅导员列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>ID</span>
          <input v-model="listFilters.id" placeholder="辅导员ID" />
        </label>
        <label class="field">
          <span>姓名</span>
          <input v-model="listFilters.name" placeholder="姓名" />
        </label>
        <label class="field">
          <span>学院</span>
          <select v-model="listFilters.college">
            <option value="">全部</option>
            <option value="计算机学院">计算机学院</option>
            <option value="卓越学院">卓越学院</option>
          </select>
        </label>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>学院</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.ins_id">
            <td>{{ item.ins_id }}</td>
            <td>{{ item.ins_name }}</td>
            <td>{{ item.ins_college }}</td>
            <td>
              <button class="ghost" @click="removeItem(item.ins_id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../api';

const items = ref([]);
const error = ref('');
const success = ref('');
const listFilters = ref({
  id: '',
  name: '',
  college: ''
});
const form = ref({
  ins_id: '',
  ins_name: '',
  ins_college: ''
});

const filteredItems = computed(() => items.value.filter((row) => {
  if (listFilters.value.id && row.ins_id !== listFilters.value.id) return false;
  if (listFilters.value.name && !row.ins_name.includes(listFilters.value.name)) return false;
  if (listFilters.value.college && row.ins_college !== listFilters.value.college) return false;
  return true;
}));

const load = async () => {
  const { data } = await api.get('/instructors');
  items.value = data.data || [];
};

const createItem = async () => {
  error.value = '';
  success.value = '';
  const insId = String(form.value.ins_id || '').trim();
  if (!insId) {
    error.value = '辅导员ID不能为空';
    return;
  }
  if (!/^I\d{6}$/.test(insId)) {
    error.value = '辅导员ID格式错误，应为I开头+6位数字';
    return;
  }
  try {
    const { data } = await api.post('/instructors', form.value);
    if (!data.success) {
      error.value = data.message || '创建失败';
      return;
    }
    await load();
    success.value = '创建成功';
  } catch (err) {
    error.value = '创建失败，请稍后重试。';
  }
};

const resetForm = () => {
  form.value = {
    ins_id: '',
    ins_name: '',
    ins_college: ''
  };
  error.value = '';
  success.value = '';
};

const removeItem = async (id) => {
  if (!confirm('确认删除该辅导员吗？该辅导员名下学生会被一起删除。')) {
    return;
  }
  error.value = '';
  success.value = '';
  try {
    const { data } = await api.delete(`/instructors/${id}`);
    if (!data.success) {
      error.value = data.message || '删除失败';
      return;
    }
    await load();
  } catch (err) {
    error.value = '删除失败，请稍后重试。';
  }
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
.error {
  color: #dc2626;
  font-size: 13px;
  margin-bottom: 8px;
}
.success {
  color: #16a34a;
  font-size: 13px;
  margin-bottom: 8px;
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
</style>
