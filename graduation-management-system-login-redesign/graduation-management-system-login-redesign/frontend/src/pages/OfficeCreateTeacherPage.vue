<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>教师账号创建</h2>
        <p>录入教师基础信息。</p>
      </div>
      <div class="role-pill">当前身份：教科办</div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>新增教师</h3>
        <button class="ghost" @click="resetForm">清空未创建</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>教师 ID</span>
          <input v-model="form.tea_id" placeholder="例如 T000001" />
        </label>
        <label class="field">
          <span>姓名</span>
          <input v-model="form.tea_name" placeholder="教师姓名" />
        </label>
        <label class="field">
          <span>研究所/室/部</span>
          <select v-model="form.tea_institute">
            <option disabled value="">请选择研究所/室/部</option>
            <option value="软件工程">软件工程</option>
            <option value="计算机科学">计算机科学</option>
            <option value="人工智能">人工智能</option>
          </select>
        </label>
        <button class="primary" @click="createItem">创建教师</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>教师列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>部门</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.tea_id">
            <td>{{ item.tea_id }}</td>
            <td>{{ item.tea_name }}</td>
            <td>{{ item.tea_institute }}</td>
            <td>
              <button class="ghost" @click="removeItem(item.tea_id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';

const items = ref([]);
const form = ref({
  tea_id: '',
  tea_name: '',
  tea_institute: ''
});

const load = async () => {
  const { data } = await api.get('/teachers');
  items.value = data.data || [];
};

const createItem = async () => {
  await api.post('/teachers', form.value);
  await load();
};

const resetForm = () => {
  form.value = {
    tea_id: '',
    tea_name: '',
    tea_institute: ''
  };
};

const removeItem = async (id) => {
  if (!confirm('确认删除该教师吗？该教师名下学生会被一起删除。')) {
    return;
  }
  await api.delete(`/teachers/${id}`);
  await load();
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
