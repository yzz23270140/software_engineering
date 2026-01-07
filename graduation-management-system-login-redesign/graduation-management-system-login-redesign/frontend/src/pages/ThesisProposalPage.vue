<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>开题报告提交</h2>
        <p>从审核通过的选题申请中选择并提交开题报告。</p>
      </div>
      <RolePill role-label="学生" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>开题报告提交</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>选题申请</span>
          <select v-model.number="form.topic_selection_application_id">
            <option disabled value="">请选择已通过的申请</option>
            <option
              v-for="item in approvedApplications"
              :key="item.application_id"
              :value="item.application_id"
            >
              {{ item.topic_Topic_id }}（{{ item.application_id }}）
            </option>
          </select>
        </label>
        <label class="field">
          <span>报告标题</span>
          <input v-model="form.tp_title" placeholder="开题报告标题" />
        </label>
        <label class="field field-wide">
          <span>报告内容</span>
          <textarea v-model="form.tp_text" placeholder="研究目标、方案、计划等"></textarea>
        </label>
        <div class="field">
          <span>状态</span>
          <div class="readonly">已提交</div>
        </div>
        <button class="primary" @click="createItem">提交开题报告</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>本人已提交开题报告</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>选题申请</th>
            <th>标题</th>
            <th>内容</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.tp_id">
            <td>{{ item.tp_id }}</td>
            <td>{{ item.topic_selection_application_id }}</td>
            <td>{{ item.tp_title }}</td>
            <td>{{ item.tp_text }}</td>
            <td>{{ item.tp_status }}</td>
            <td>
              <button class="ghost danger" @click="removeItem(item.tp_id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const applications = ref([]);
const form = ref({
  topic_selection_application_id: null,
  tp_title: '',
  tp_text: '',
  tp_status: '已提交'
});

const load = async () => {
  const { data } = await api.get('/thesis-proposals');
  const appIds = new Set(applications.value.map((item) => item.application_id));
  items.value = (data.data || []).filter((row) => appIds.has(row.topic_selection_application_id));
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

const createItem = async () => {
  const { data } = await api.post('/thesis-proposals', {
    ...form.value,
    tp_status: '已提交'
  });
  if (!data.success) {
    alert(data.message || '开题报告提交失败');
    return;
  }
  await load();
};

const removeItem = async (id) => {
  if (!confirm('确认删除该开题报告吗？')) {
    return;
  }
  const { data } = await api.delete(`/thesis-proposals/${id}`);
  if (!data.success) {
    alert(data.message || '删除失败');
    return;
  }
  await load();
};

const approvedApplications = computed(() =>
  applications.value.filter((item) => item.tsa_status === '审核通过')
);

onMounted(async () => {
  await loadApplications();
  if (approvedApplications.value.length > 0) {
    form.value.topic_selection_application_id = approvedApplications.value[0].application_id;
  }
  await load();
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
.field-wide {
  grid-column: 1 / -1;
}
input, textarea {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
  resize: vertical;
  min-height: 36px;
}
.readonly {
  padding: 8px 10px;
  border: 1px dashed #cbd5e1;
  border-radius: 4px;
  background: #f8fafc;
  font-size: 13px;
  color: #475569;
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
textarea {
  min-height: 90px;
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
.danger {
  background: #fee2e2;
  border: 1px solid #fecaca;
  color: #991b1b;
}
</style>
