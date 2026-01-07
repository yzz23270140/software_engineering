<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>中期指导</h2>
        <p>填写指导记录并查看学生确认情况。</p>
      </div>
      <RolePill role-label="教师" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>指导记录提交</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field field-wide">
          <span>开题报告（学生）</span>
          <select v-model.number="form.thesis_proposal_Tp_id">
            <option disabled value="">请选择开题报告</option>
            <option v-for="item in thesisOptions" :key="item.tp_id" :value="item.tp_id">
              {{ item.label }}
            </option>
          </select>
        </label>
        <label class="field">
          <span>指导时间</span>
          <input v-model="form.gr_time" type="datetime-local" />
        </label>
        <label class="field">
          <span>指导时长（分钟）</span>
          <input v-model.number="form.gr_duration" type="number" min="0" placeholder="可选" />
        </label>
        <label class="field">
          <span>指导方式</span>
          <select v-model="form.gr_method">
            <option value="面谈">面谈</option>
            <option value="线上会议">线上会议</option>
            <option value="邮件">邮件</option>
            <option value="电话">电话</option>
            <option value="其他">其他</option>
          </select>
        </label>
        <label class="field field-wide">
          <span>指导记录</span>
          <textarea v-model="form.gr_record" placeholder="指导过程与重点内容"></textarea>
        </label>
        <label class="field field-wide">
          <span>指导意见</span>
          <textarea v-model="form.gr_suggestions" placeholder="建议或改进方向（可选）"></textarea>
        </label>
        <button class="primary" @click="createItem">提交指导</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>指导记录列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>学生</th>
            <th>指导时间</th>
            <th>方式</th>
            <th>确认状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.gr_id">
            <td>{{ item.gr_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.student_name }}</td>
            <td>{{ item.gr_time }}</td>
            <td>{{ item.gr_method }}</td>
            <td>{{ item.gr_confirm }}</td>
            <td>
              <button class="ghost danger" @click="removeItem(item.gr_id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const thesisOptions = ref([]);
const form = ref({
  thesis_proposal_Tp_id: '',
  gr_time: '',
  gr_duration: null,
  gr_method: '面谈',
  gr_record: '',
  gr_suggestions: ''
});

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const load = async () => {
  let teacherId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    teacherId = stored?.username || '';
  } catch (err) {
    teacherId = '';
  }
  const [topicRes, applicationRes, proposalRes, studentRes, guidanceRes] = await Promise.all([
    api.get('/topics', { params: { teacherId } }),
    api.get('/topic-applications'),
    api.get('/thesis-proposals'),
    api.get('/students'),
    api.get('/guidance-records')
  ]);

  const topics = topicRes.data.data || [];
  const topicIds = new Set(topics.map((item) => item.topic_id));
  const applications = (applicationRes.data.data || []).filter((row) => topicIds.has(row.topic_Topic_id));
  const proposals = (proposalRes.data.data || []).filter((row) =>
    row.tp_status === '已通过' &&
    applications.some((app) => app.application_id === row.topic_selection_application_id)
  );
  const students = studentRes.data.data || [];
  const studentMap = new Map();
  students.forEach((row) => studentMap.set(row.stu_id, row.stu_name));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));

  thesisOptions.value = proposals.map((proposal) => {
    const app = applications.find((row) => row.application_id === proposal.topic_selection_application_id);
    const topic = app ? topicMap.get(app.topic_Topic_id) : null;
    const studentName = app ? (studentMap.get(app.student_Stu_id) || '') : '';
    return {
      tp_id: proposal.tp_id,
      label: `${proposal.tp_id} - ${studentName}（${app?.student_Stu_id || ''}）- ${topic?.topic_name || ''}`
    };
  });
  if (!form.value.thesis_proposal_Tp_id && thesisOptions.value.length) {
    form.value.thesis_proposal_Tp_id = thesisOptions.value[0].tp_id;
  }

  const thesisIdSet = new Set(proposals.map((row) => row.tp_id));
  items.value = (guidanceRes.data.data || [])
    .filter((row) => thesisIdSet.has(row.thesis_proposal_Tp_id))
    .map((row) => {
      const proposal = proposals.find((p) => p.tp_id === row.thesis_proposal_Tp_id);
      const app = proposal ? applications.find((a) => a.application_id === proposal.topic_selection_application_id) : null;
      const topic = app ? topicMap.get(app.topic_Topic_id) : null;
      const studentName = app ? (studentMap.get(app.student_Stu_id) || '') : '';
      return {
        ...row,
        topic_name: topic?.topic_name || '',
        student_name: studentName ? `${studentName}（${app?.student_Stu_id || ''}）` : '',
        gr_time: formatTime(row.gr_time)
      };
    });
};

const createItem = async () => {
  if (!form.value.thesis_proposal_Tp_id || !form.value.gr_record) {
    alert('请填写开题报告与指导记录内容。');
    return;
  }
  const payload = {
    thesis_proposal_Tp_id: form.value.thesis_proposal_Tp_id,
    gr_time: form.value.gr_time ? form.value.gr_time : null,
    gr_duration: form.value.gr_duration || null,
    gr_method: form.value.gr_method,
    gr_record: form.value.gr_record,
    gr_suggestions: form.value.gr_suggestions,
    gr_confirm: '未确认'
  };
  const { data } = await api.post('/guidance-records', payload);
  if (!data.success) {
    alert(data.message || '提交失败');
    return;
  }
  form.value.gr_record = '';
  form.value.gr_suggestions = '';
  await load();
};

const removeItem = async (id) => {
  if (!confirm('确认删除该指导记录吗？')) {
    return;
  }
  const { data } = await api.delete(`/guidance-records/${id}`);
  if (!data.success) {
    alert(data.message || '删除失败');
    return;
  }
  await load();
};

load();
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
}
textarea {
  min-height: 90px;
  resize: vertical;
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
