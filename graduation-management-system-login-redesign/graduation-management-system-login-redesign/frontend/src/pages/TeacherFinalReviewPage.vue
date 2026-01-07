<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>论文初审</h2>
        <p>针对初稿给出教师评价与结论。</p>
      </div>
      <RolePill role-label="教师" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>初审意见</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field field-wide">
          <span>论文（初稿）</span>
          <select v-model.number="form.paper_id">
            <option disabled value="">请选择论文</option>
            <option v-for="item in paperOptions" :key="item.paper_id" :value="item.paper_id">
              {{ item.label }}
            </option>
          </select>
        </label>
        <label class="field field-wide">
          <span>内容完整性</span>
          <textarea v-model="form.content_note" placeholder="内容完整性评价"></textarea>
        </label>
        <label class="field field-wide">
          <span>格式规范性</span>
          <textarea v-model="form.format_note" placeholder="格式规范性评价"></textarea>
        </label>
        <label class="field field-wide">
          <span>与开题一致性</span>
          <textarea v-model="form.consistency_note" placeholder="与开题一致性评价"></textarea>
        </label>
        <label class="field field-wide">
          <span>综合意见</span>
          <textarea v-model="form.opinion" placeholder="评审意见"></textarea>
        </label>
        <label class="field">
          <span>审核结论</span>
          <select v-model="form.status">
            <option disabled value="">请选择结论</option>
            <option value="已通过">通过</option>
            <option value="未通过">不通过</option>
          </select>
        </label>
        <button class="primary" @click="submitReview">提交初审</button>
        <button class="ghost" @click="previewSelected">预览选中论文</button>
      </div>
    </div>

    <div v-if="preview.visible && preview.section === 'form'" class="panel">
      <div class="panel-head">
        <h3>初审预览</h3>
        <button class="ghost" @click="closePreview">关闭</button>
      </div>
      <div class="preview-meta">
        <div><strong>教师反馈：</strong>{{ preview.teacherScores || '-' }}</div>
        <div><strong>评审意见：</strong>{{ preview.teacherOpinion || '-' }}</div>
      </div>
      <div v-if="preview.error" class="preview-error">{{ preview.error }}</div>
      <div v-else class="preview-body" v-html="preview.html"></div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>初稿列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>学生</th>
            <th>题目</th>
            <th>提交时间</th>
            <th>状态</th>
            <th>文件名</th>
            <th>下载</th>
            <th>预览</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.paper_id">
            <td>{{ item.paper_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.student_name }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.paper_submission_time }}</td>
            <td>{{ formatStatus(item.paper_status) }}</td>
            <td>
              {{ item.file_url || '-' }}
            </td>
            <td>
              <a v-if="item.paper_text" :href="item.paper_text" :download="item.file_url || 'paper.docx'">下载</a>
              <span v-else>-</span>
            </td>
            <td>
              <button class="ghost" @click="openPreview('list', item)">预览</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="preview.visible && preview.section === 'list'" class="panel">
      <div class="panel-head">
        <h3>初稿预览</h3>
        <button class="ghost" @click="closePreview">关闭</button>
      </div>
      <div class="preview-meta">
        <div><strong>教师反馈：</strong>{{ preview.teacherScores || '-' }}</div>
        <div><strong>评审意见：</strong>{{ preview.teacherOpinion || '-' }}</div>
      </div>
      <div v-if="preview.error" class="preview-error">{{ preview.error }}</div>
      <div v-else class="preview-body" v-html="preview.html"></div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const paperOptions = ref([]);
const form = ref({
  paper_id: '',
  content_note: '',
  format_note: '',
  consistency_note: '',
  opinion: '',
  status: ''
});
const preview = ref({
  visible: false,
  section: '',
  html: '',
  error: '',
  teacherScores: '',
  teacherOpinion: ''
});

const load = async () => {
  let teacherId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    teacherId = stored?.username || '';
  } catch (err) {
    teacherId = '';
  }
  const [topicRes, applicationRes, proposalRes, paperRes, studentRes, opinionRes] = await Promise.all([
    api.get('/topics', { params: { teacherId } }),
    api.get('/topic-applications'),
    api.get('/thesis-proposals'),
    api.get('/papers'),
    api.get('/students'),
    api.get('/first-opinions')
  ]);
  const topics = topicRes.data.data || [];
  const topicIds = new Set(topics.map((item) => item.topic_id));
  const applications = (applicationRes.data.data || []).filter((row) => topicIds.has(row.topic_Topic_id));
  const proposals = (proposalRes.data.data || []).filter((row) =>
    applications.some((app) => app.application_id === row.topic_selection_application_id)
  );
  const papers = (paperRes.data.data || []).filter((row) =>
    row.paper_version === '初稿' && proposals.some((p) => p.tp_id === row.thesis_proposal_Tp_id)
  );
  const opinions = opinionRes.data.data || [];
  const students = studentRes.data.data || [];
  const studentMap = new Map();
  students.forEach((row) => studentMap.set(row.stu_id, row.stu_name));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));

  items.value = papers.map((row) => {
    const proposal = proposals.find((p) => p.tp_id === row.thesis_proposal_Tp_id);
    const app = proposal ? applications.find((a) => a.application_id === proposal.topic_selection_application_id) : null;
    const topic = app ? topicMap.get(app.topic_Topic_id) : null;
    const studentName = app ? (studentMap.get(app.student_Stu_id) || '') : '';
    const opinion = opinions.find((item) => item.paper_id === row.paper_id);
    const details = parseDetails(opinion?.fio_details);
    return {
      ...row,
      topic_name: topic?.topic_name || '',
      student_name: studentName ? `${studentName}（${app?.student_Stu_id || ''}）` : '',
      paper_submission_time: formatTime(row.paper_submission_time),
      teacher_opinion: opinion?.fio_opinion || '',
      teacher_scores: formatTeacherScores(details)
    };
  });

  paperOptions.value = items.value.map((row) => ({
    paper_id: row.paper_id,
    label: `${row.paper_id} - ${row.topic_name} - ${row.student_name}`
  }));
  if (!form.value.paper_id && paperOptions.value.length) {
    form.value.paper_id = paperOptions.value[0].paper_id;
  }
};

const submitReview = async () => {
  if (!form.value.paper_id || !form.value.status) {
    alert('请选择论文并填写审核结论。');
    return;
  }
  const details = {
    content: form.value.content_note,
    format: form.value.format_note,
    consistency: form.value.consistency_note
  };

  const { data } = await api.post('/first-opinions', {
    paper_id: form.value.paper_id,
    fio_opinion: form.value.opinion,
    fio_score: 0,
    fio_details: JSON.stringify(details),
    fio_status: '提交'
  });
  if (!data.success) {
    alert(data.message || '初审提交失败');
    return;
  }
  const paperUpdate = await api.put(`/papers/${form.value.paper_id}`, {
    paper_status: form.value.status
  });
  if (!paperUpdate.data.success) {
    alert(paperUpdate.data.message || '更新状态失败');
    return;
  }
  form.value.opinion = '';
  form.value.content_note = '';
  form.value.format_note = '';
  form.value.consistency_note = '';
  await load();
};

const previewSelected = () => {
  const selected = items.value.find((item) => item.paper_id === form.value.paper_id);
  if (!selected) {
    alert('请先选择论文。');
    return;
  }
  openPreview('form', selected);
};

const openPreview = async (section, item) => {
  preview.value.visible = true;
  preview.value.section = section;
  preview.value.html = '';
  preview.value.error = '';
  preview.value.teacherScores = item.teacher_scores || '';
  preview.value.teacherOpinion = item.teacher_opinion || '';
  if (!item.paper_text) {
    preview.value.error = '暂无可预览内容。';
    return;
  }
  const fileName = item.file_url || '';
  if (fileName.toLowerCase().endsWith('.doc')) {
    preview.value.error = '暂不支持 .doc 预览，请下载后查看。';
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

const parseDetails = (value) => {
  if (!value) return null;
  try {
    return JSON.parse(value);
  } catch (err) {
    return null;
  }
};

const formatTeacherScores = (details) => {
  if (!details) return '';
  const parts = [];
  if (details.content) parts.push(`内容完整性：${details.content}`);
  if (details.format) parts.push(`格式规范性：${details.format}`);
  if (details.consistency) parts.push(`与开题一致性：${details.consistency}`);
  return parts.join(' / ');
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
.preview-body {
  padding: 8px 0;
  line-height: 1.6;
}
.preview-error {
  color: #b91c1c;
  font-size: 13px;
}
.preview-meta {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: #0f3a55;
  padding: 8px 0;
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
