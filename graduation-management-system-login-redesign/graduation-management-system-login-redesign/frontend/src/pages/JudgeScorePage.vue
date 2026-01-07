<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>答辩评分</h2>
        <p>对终稿答辩进行评分并记录过程。</p>
      </div>
      <RolePill role-label="评审组" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>评分录入</h3>
        <button class="ghost" @click="refreshForm">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field field-wide">
          <span>终稿论文</span>
          <select v-model.number="form.paper_id">
            <option disabled value="">请选择终稿</option>
            <option v-for="item in paperOptions" :key="item.paper_id" :value="item.paper_id">
              {{ item.label }}
            </option>
          </select>
        </label>
        <label class="field">
          <span>答辩时间</span>
          <input v-model="form.dr_time" type="datetime-local" />
        </label>
        <label class="field">
          <span>答辩地点</span>
          <input v-model="form.dr_location" placeholder="答辩地点" />
        </label>
        <label class="field">
          <span>汇报逻辑</span>
          <input v-model.number="form.dr_presentation_score" type="number" min="0" max="100" />
        </label>
        <label class="field">
          <span>成果质量</span>
          <input v-model.number="form.dr_paper_score" type="number" min="0" max="100" />
        </label>
        <label class="field">
          <span>问答表现</span>
          <input v-model.number="form.dr_response_score" type="number" min="0" max="100" />
        </label>
        <label class="field">
          <span>总分</span>
          <div class="readonly">{{ totalScore }}</div>
        </label>
        <label class="field">
          <span>答辩结果</span>
          <select v-model="form.dr_result">
            <option disabled value="">请选择结果</option>
            <option value="优秀">优秀</option>
            <option value="良好">良好</option>
            <option value="中等">中等</option>
            <option value="及格">及格</option>
            <option value="不及格">不及格</option>
          </select>
        </label>
        <label class="field field-wide">
          <span>答辩记录</span>
          <textarea v-model="form.dr_record" placeholder="答辩过程记录"></textarea>
        </label>
        <label class="field field-wide">
          <span>评委意见</span>
          <textarea v-model="form.dr_comment" placeholder="评委意见"></textarea>
        </label>
        <button class="primary" @click="submitScore">保存评分</button>
        <button class="ghost" @click="previewSelected">预览当前论文</button>
      </div>
    </div>

    <div v-if="preview.visible && preview.section === 'form'" class="panel">
      <div class="panel-head">
        <h3>终稿预览</h3>
        <button class="ghost" @click="closePreview">关闭</button>
      </div>
      <div v-if="preview.error" class="preview-error">{{ preview.error }}</div>
      <div v-else class="preview-body" v-html="preview.html"></div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>评分记录</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>答辩ID</th>
            <th>课题</th>
            <th>教师</th>
            <th>学生</th>
            <th>终稿</th>
            <th>汇报逻辑</th>
            <th>成果质量</th>
            <th>问答表现</th>
            <th>总分</th>
            <th>结果</th>
            <th>预览</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.dr_id">
            <td>{{ item.dr_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.teacher_name }}</td>
            <td>{{ item.student_name }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.dr_presentation_score }}</td>
            <td>{{ item.dr_paper_score }}</td>
            <td>{{ item.dr_response_score }}</td>
            <td>{{ item.dr_total_score }}</td>
            <td>{{ item.dr_result }}</td>
            <td>
              <button class="ghost" @click="openPreview('list', item)">预览</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="preview.visible && preview.section === 'list'" class="panel">
      <div class="panel-head">
        <h3>终稿预览</h3>
        <button class="ghost" @click="closePreview">关闭</button>
      </div>
      <div v-if="preview.error" class="preview-error">{{ preview.error }}</div>
      <div v-else class="preview-body" v-html="preview.html"></div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const paperOptions = ref([]);
const defenseMap = ref(new Map());
const paperMap = ref(new Map());
const form = ref({
  paper_id: '',
  dr_time: '',
  dr_location: '',
  dr_presentation_score: null,
  dr_paper_score: null,
  dr_response_score: null,
  dr_record: '',
  dr_comment: '',
  dr_result: ''
});
const preview = ref({
  visible: false,
  section: '',
  html: '',
  error: ''
});

const totalScore = computed(() => {
  const scores = [form.value.dr_presentation_score, form.value.dr_paper_score, form.value.dr_response_score]
    .filter((v) => typeof v === 'number');
  if (!scores.length) return '-';
  const total = scores.reduce((sum, v) => sum + v, 0) / scores.length;
  return total.toFixed(2);
});

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const load = async () => {
  const [defenseRes, paperRes, proposalRes, applicationRes, topicRes, studentRes, teacherRes] = await Promise.all([
    api.get('/defense-records'),
    api.get('/papers'),
    api.get('/thesis-proposals'),
    api.get('/topic-applications'),
    api.get('/topics'),
    api.get('/students'),
    api.get('/teachers')
  ]);
  const defenses = defenseRes.data.data || [];
  const papers = (paperRes.data.data || []).filter((row) => row.paper_version === '终稿');
  const proposals = proposalRes.data.data || [];
  const apps = applicationRes.data.data || [];
  const topics = topicRes.data.data || [];
  const students = studentRes.data.data || [];
  const teachers = teacherRes.data.data || [];

  const appMap = new Map();
  apps.forEach((row) => appMap.set(row.application_id, row));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));
  const studentMap = new Map();
  students.forEach((row) => studentMap.set(row.stu_id, row.stu_name));
  const teacherMap = new Map();
  teachers.forEach((row) => teacherMap.set(row.tea_id, row.tea_name));
  const proposalMap = new Map();
  proposals.forEach((row) => proposalMap.set(row.tp_id, row));

  defenseMap.value = new Map();
  defenses.forEach((row) => defenseMap.value.set(row.paper_id, row));

  const paperRows = papers.map((row) => {
    const proposal = proposalMap.get(row.thesis_proposal_Tp_id);
    const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
    const topic = app ? topicMap.get(app.topic_Topic_id) : null;
    const studentName = app ? (studentMap.get(app.student_Stu_id) || '') : '';
    return {
      ...row,
      topic_name: topic?.topic_name || '',
      student_name: studentName ? `${studentName}（${app?.student_Stu_id || ''}）` : '',
      paper_text: row.paper_text || ''
    };
  });
  paperMap.value = new Map(paperRows.map((row) => [row.paper_id, row]));

  paperOptions.value = paperRows.map((row) => ({
    paper_id: row.paper_id,
    label: `${row.paper_id} - ${row.topic_name} - ${row.student_name}`
  }));
  if (!form.value.paper_id && paperOptions.value.length) {
    form.value.paper_id = paperOptions.value[0].paper_id;
  }

  items.value = defenses.map((row) => {
    const paper = papers.find((p) => p.paper_id === row.paper_id);
    const proposal = paper ? proposalMap.get(paper.thesis_proposal_Tp_id) : null;
    const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
    const topic = app ? topicMap.get(app.topic_Topic_id) : null;
    const teacherName = topic ? (teacherMap.get(topic.teacher_Tea_id) || '') : '';
    return {
      ...row,
      topic_name: topic?.topic_name || '',
      paper_title: paper?.paper_title || '',
      dr_time: formatTime(row.dr_time),
      teacher_name: teacherName ? `${teacherName}（${topic?.teacher_Tea_id || ''}）` : '',
      student_name: app ? `${studentMap.get(app.student_Stu_id) || ''}（${app?.student_Stu_id || ''}）` : '',
      paper_text: paper?.paper_text || ''
    };
  });
};

const submitScore = async () => {
  if (!form.value.paper_id) {
    alert('请选择终稿。');
    return;
  }
  const scoreValue = totalScore.value === '-' ? null : Number(totalScore.value);
  const payload = {
    paper_id: form.value.paper_id,
    dr_time: form.value.dr_time ? form.value.dr_time : null,
    dr_location: form.value.dr_location,
    dr_presentation_score: form.value.dr_presentation_score,
    dr_paper_score: form.value.dr_paper_score,
    dr_response_score: form.value.dr_response_score,
    dr_total_score: scoreValue,
    dr_record: form.value.dr_record,
    dr_comment: form.value.dr_comment,
    dr_result: form.value.dr_result
  };
  const existing = defenseMap.value.get(form.value.paper_id);
  let response;
  if (existing) {
    response = await api.put(`/defense-records/${existing.dr_id}`, payload);
  } else {
    response = await api.post('/defense-records', payload);
  }
  if (!response.data.success) {
    alert(response.data.message || '保存失败');
    return;
  }
  await load();
};

const refreshForm = async () => {
  form.value.dr_time = '';
  form.value.dr_location = '';
  form.value.dr_presentation_score = null;
  form.value.dr_paper_score = null;
  form.value.dr_response_score = null;
  form.value.dr_record = '';
  form.value.dr_comment = '';
  form.value.dr_result = '';
  await load();
};

const previewSelected = () => {
  const selected = paperMap.value.get(form.value.paper_id);
  if (!selected) {
    alert('请先选择终稿论文。');
    return;
  }
  openPreview('form', selected);
};

const openPreview = async (section, item) => {
  preview.value.visible = true;
  preview.value.section = section;
  preview.value.html = '';
  preview.value.error = '';
  if (!item.paper_text) {
    preview.value.error = '暂无可预览内容。';
    return;
  }
  try {
    const mammoth = await import('mammoth');
    const arrayBuffer = dataUrlToArrayBuffer(item.paper_text);
    const result = await mammoth.convertToHtml({ arrayBuffer });
    preview.value.html = result.value || '<p>未能解析出内容。</p>';
  } catch (err) {
    preview.value.error = '预览失败，请确认已安装 mammoth。';
  }
};

const closePreview = () => {
  preview.value.visible = false;
  preview.value.section = '';
};

const dataUrlToArrayBuffer = (dataUrl) => {
  const base64 = String(dataUrl).split(',')[1] || '';
  const binary = atob(base64);
  const len = binary.length;
  const bytes = new Uint8Array(len);
  for (let i = 0; i < len; i += 1) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
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
.preview-body {
  padding: 8px 0;
  line-height: 1.6;
}
.preview-error {
  color: #b91c1c;
  font-size: 13px;
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
