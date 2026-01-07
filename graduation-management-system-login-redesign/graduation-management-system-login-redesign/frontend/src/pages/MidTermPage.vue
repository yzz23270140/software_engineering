<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>课题中期汇报</h2>
        <p>填写中期进展并提交汇报。</p>
      </div>
      <RolePill role-label="学生" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>中期汇报提交</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>开题报告</span>
          <select v-model.number="form.thesis_proposal_Tp_id">
            <option disabled value="">请选择开题报告</option>
            <option v-for="item in thesisOptions" :key="item.tp_id" :value="item.tp_id">
              {{ item.label }}
            </option>
          </select>
        </label>
        <label class="field field-wide">
          <span>进展描述</span>
          <textarea v-model="form.mtr_progress" placeholder="项目进度、问题与下一步计划"></textarea>
        </label>
        <label class="field field-wide">
          <span>存在问题</span>
          <textarea v-model="form.mtr_problems" placeholder="当前遇到的问题（可选）"></textarea>
        </label>
        <label class="field field-wide">
          <span>后续计划</span>
          <textarea v-model="form.mtr_plan" placeholder="下一阶段计划（可选）"></textarea>
        </label>
        <div class="field">
          <span>状态</span>
          <div class="readonly">已提交</div>
        </div>
        <button class="primary" @click="createItem">提交汇报</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>中期汇报记录</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>开题报告</th>
            <th>提交时间</th>
            <th>进展</th>
            <th>状态</th>
            <th>检查意见</th>
            <th>查看</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.mtr_id">
            <td>{{ item.mtr_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.thesis_proposal_Tp_id }}</td>
            <td>{{ item.mtr_submission_time }}</td>
            <td>{{ item.mtr_progress }}</td>
            <td>{{ formatStatus(item.mtr_status) }}</td>
            <td>{{ item.mtr_comment || '-' }}</td>
            <td>
              <button class="ghost" @click="goDetail(item.mtr_id)">查看</button>
            </td>
            <td>
              <button class="ghost danger" @click="removeItem(item.mtr_id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const thesisOptions = ref([]);
const form = ref({
  thesis_proposal_Tp_id: '',
  mtr_progress: '',
  mtr_problems: '',
  mtr_plan: '',
  mtr_status: '已提交'
});
const router = useRouter();

const load = async () => {
  let studentId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    studentId = stored?.username || '';
  } catch (err) {
    studentId = '';
  }
  const [midtermRes, applicationRes, proposalRes, topicRes] = await Promise.all([
    api.get('/midterm-reports'),
    api.get('/topic-applications', { params: { studentId } }),
    api.get('/thesis-proposals'),
    api.get('/topics')
  ]);
  const apps = applicationRes.data.data || [];
  const proposals = proposalRes.data.data || [];
  const topics = topicRes.data.data || [];
  const appMap = new Map();
  apps.forEach((row) => appMap.set(row.application_id, row));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));

  const proposalRows = proposals.filter((row) => appMap.has(row.topic_selection_application_id));
  thesisOptions.value = proposalRows
    .filter((row) => row.tp_status === '已通过')
    .map((row) => {
      const app = appMap.get(row.topic_selection_application_id);
      const topic = app ? topicMap.get(app.topic_Topic_id) : null;
      return {
        tp_id: row.tp_id,
        label: `${row.tp_id} - ${topic?.topic_name || ''}`
      };
    });
  if (!form.value.thesis_proposal_Tp_id && thesisOptions.value.length) {
    form.value.thesis_proposal_Tp_id = thesisOptions.value[0].tp_id;
  }

  const thesisIdSet = new Set(proposalRows.map((row) => row.tp_id));
  items.value = (midtermRes.data.data || [])
    .filter((row) => thesisIdSet.has(row.thesis_proposal_Tp_id))
    .map((row) => {
      const proposal = proposalRows.find((p) => p.tp_id === row.thesis_proposal_Tp_id);
      const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
      const topic = app ? topicMap.get(app.topic_Topic_id) : null;
      return {
        ...row,
        topic_name: topic?.topic_name || '',
        mtr_submission_time: formatTime(row.mtr_submission_time)
      };
    });
};

const createItem = async () => {
  if (!form.value.thesis_proposal_Tp_id || !form.value.mtr_progress) {
    alert('请填写开题报告与进展描述。');
    return;
  }
  const { data } = await api.post('/midterm-reports', {
    ...form.value,
    mtr_status: '已提交'
  });
  if (!data.success) {
    alert(data.message || '中期汇报提交失败');
    return;
  }
  form.value.mtr_progress = '';
  form.value.mtr_problems = '';
  form.value.mtr_plan = '';
  await load();
};

const removeItem = async (id) => {
  if (!confirm('确认删除该中期汇报吗？')) {
    return;
  }
  const { data } = await api.delete(`/midterm-reports/${id}`);
  if (!data.success) {
    alert(data.message || '删除失败');
    return;
  }
  await load();
};

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const formatStatus = (value) => {
  if (value === '已通过') return '通过';
  if (value === '未通过') return '不通过';
  return value || '';
};

const goDetail = (id) => {
  router.push(`/midterm-reports/${id}`);
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
.readonly {
  padding: 8px 10px;
  border: 1px dashed #cbd5e1;
  border-radius: 4px;
  background: #f8fafc;
  font-size: 13px;
  color: #475569;
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
.ghost.danger {
  border-color: #fecaca;
  color: #b91c1c;
  background: #fff1f2;
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
