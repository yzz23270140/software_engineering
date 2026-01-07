<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>评委组账号创建</h2>
        <p>录入评委基础信息。</p>
      </div>
      <div class="role-pill">当前身份：教科办</div>
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>新增评委</h3>
        <button class="ghost" @click="resetForm">清空未创建</button>
      </div>
      <div v-if="error" class="error">{{ error }}</div>
      <div v-if="success" class="success">{{ success }}</div>
      <div class="form-grid">
        <label class="field">
          <span>评委 ID</span>
          <input v-model="form.jud_id" placeholder="例如 J000001" />
        </label>
        <label class="field">
          <span>姓名</span>
          <input v-model="form.jud_name" placeholder="评委姓名" />
        </label>
        <label class="field">
          <span>评审组</span>
          <select v-model="form.jud_group">
            <option disabled value="">请选择评审组</option>
            <option value="审题组">审题组</option>
            <option value="开题评审组">开题评审组</option>
            <option value="中期检查小组">中期检查小组</option>
            <option value="论文二审组">论文二审组</option>
            <option value="毕设评委组">毕设评委组</option>
          </select>
        </label>
        <button class="primary" @click="createItem">创建评委</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>评委列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>ID</span>
          <input v-model="listFilters.id" placeholder="评委ID" />
        </label>
        <label class="field">
          <span>姓名</span>
          <input v-model="listFilters.name" placeholder="姓名" />
        </label>
        <label class="field">
          <span>评审组</span>
          <select v-model="listFilters.group">
            <option value="">全部</option>
            <option value="审题组">审题组</option>
            <option value="开题评审组">开题评审组</option>
            <option value="中期检查小组">中期检查小组</option>
            <option value="论文二审组">论文二审组</option>
            <option value="毕设评委组">毕设评委组</option>
          </select>
        </label>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>评审组</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.jud_id">
            <td>{{ item.jud_id }}</td>
            <td>{{ item.jud_name }}</td>
            <td>{{ item.jud_group }}</td>
            <td>
              <button class="ghost" @click="removeItem(item.jud_id)">删除</button>
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
  group: ''
});
const form = ref({
  jud_id: '',
  jud_name: '',
  jud_group: ''
});

const filteredItems = computed(() => items.value.filter((row) => {
  if (listFilters.value.id && row.jud_id !== listFilters.value.id) return false;
  if (listFilters.value.name && !row.jud_name.includes(listFilters.value.name)) return false;
  if (listFilters.value.group && row.jud_group !== listFilters.value.group) return false;
  return true;
}));

const load = async () => {
  const { data } = await api.get('/judges');
  items.value = data.data || [];
};

const createItem = async () => {
  error.value = '';
  success.value = '';
  const judId = String(form.value.jud_id || '').trim();
  if (!judId) {
    error.value = '评委ID不能为空';
    return;
  }
  if (!/^J[1-5]\d{5}$/.test(judId)) {
    error.value = '评委ID格式错误，应为J+组别编号+5位数字';
    return;
  }
  if (!form.value.jud_group) {
    error.value = '评审组不能为空';
    return;
  }
  const groupCodeMap = {
    '审题组': '1',
    '开题评审组': '2',
    '中期检查小组': '3',
    '论文二审组': '4',
    '毕设评委组': '5'
  };
  if (groupCodeMap[form.value.jud_group] !== judId.substring(1, 2)) {
    error.value = '评委ID组别编号与评审组不匹配';
    return;
  }
  try {
    const { data } = await api.post('/judges', form.value);
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
    jud_id: '',
    jud_name: '',
    jud_group: ''
  };
  error.value = '';
  success.value = '';
};

const removeItem = async (id) => {
  if (!confirm('确认删除该评委吗？')) {
    return;
  }
  error.value = '';
  success.value = '';
  try {
    const { data } = await api.delete(`/judges/${id}`);
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
