<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>结果汇总发布与归档</h2>
        <p>汇总论文结果并更新归档状态。</p>
      </div>
      <div class="role-pill">当前身份：教科办</div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>归档操作</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>论文 ID</span>
          <input v-model.number="archiveForm.paper_id" placeholder="论文编号" />
        </label>
        <label class="field">
          <span>归档状态</span>
          <input v-model="archiveForm.paper_status" placeholder="已归档/待归档" />
        </label>
        <button class="primary" @click="updateStatus">更新状态</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>论文结果列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>题目</th>
            <th>版本</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.paper_id">
            <td>{{ item.paper_id }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.paper_version }}</td>
            <td>{{ item.paper_status }}</td>
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
const archiveForm = ref({
  paper_id: null,
  paper_status: ''
});

const load = async () => {
  const { data } = await api.get('/papers');
  items.value = data.data || [];
};

const updateStatus = async () => {
  if (!archiveForm.value.paper_id) {
    return;
  }
  await api.put(`/papers/${archiveForm.value.paper_id}`, {
    paper_status: archiveForm.value.paper_status
  });
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
