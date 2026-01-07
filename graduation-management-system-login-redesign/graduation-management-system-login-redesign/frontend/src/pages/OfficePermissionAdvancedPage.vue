<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>权限设置（高级）</h2>
        <p>为角色设置更细粒度的功能权限。</p>
      </div>
      <div class="role-pill">当前身份：教科办</div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>权限策略</h3>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>角色</span>
          <input v-model="form.role" placeholder="教师/评审组" />
        </label>
        <label class="field">
          <span>功能点</span>
          <input v-model="form.feature" placeholder="课题审核/论文终审" />
        </label>
        <label class="field">
          <span>可见范围</span>
          <input v-model="form.scope" placeholder="全院/本专业" />
        </label>
        <button class="primary" @click="addRule">保存策略</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>策略列表</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>角色</th>
            <th>功能点</th>
            <th>范围</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>{{ item.role }}</td>
            <td>{{ item.feature }}</td>
            <td>{{ item.scope }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';

const items = ref([
  { id: 1, role: '教科办', feature: '通知发布', scope: '全院' }
]);
const form = ref({
  role: '',
  feature: '',
  scope: ''
});

const addRule = () => {
  if (!form.value.role || !form.value.feature) {
    return;
  }
  items.value.push({
    id: Date.now(),
    role: form.value.role,
    feature: form.value.feature,
    scope: form.value.scope || '全部'
  });
  form.value = { role: '', feature: '', scope: '' };
};
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
