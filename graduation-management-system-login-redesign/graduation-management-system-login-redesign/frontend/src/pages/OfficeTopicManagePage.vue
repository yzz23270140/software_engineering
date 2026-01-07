<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>课题管理</h2>
        <p>审核并维护课题状态与基本信息。</p>
      </div>
      <div class="role-pill">当前身份：教科办</div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>状态更新</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>课题 ID</span>
          <input v-model.number="updateForm.topic_id" placeholder="课题编号" />
        </label>
        <label class="field">
          <span>课题状态</span>
          <input v-model="updateForm.topic_status" placeholder="已发布/驳回" />
        </label>
        <button class="primary" @click="updateStatus">更新状态</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>课题列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题名称</th>
            <th>类型</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.topic_id">
            <td>{{ item.topic_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.topic_type }}</td>
            <td>{{ item.topic_status }}</td>
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
const updateForm = ref({
  topic_id: null,
  topic_status: ''
});

const load = async () => {
  const { data } = await api.get('/topics');
  items.value = data.data || [];
};

const updateStatus = async () => {
  if (!updateForm.value.topic_id) {
    return;
  }
  await api.put(`/topics/${updateForm.value.topic_id}`, {
    topic_status: updateForm.value.topic_status
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
