<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>论文评审</h2>
        <p>针对一审稿进行评分与评估。</p>
      </div>
      <RolePill role-label="评审组" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>评审意见</h3>
        <button class="ghost" @click="refreshForm">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>教师筛选</span>
          <select v-model="filters.teacherId">
            <option value="">全部</option>
            <option v-for="item in teachers" :key="item.tea_id" :value="item.tea_id">
              {{ item.tea_name }}（{{ item.tea_id }}）
            </option>
          </select>
        </label>
        <label class="field field-wide">
          <span>论文（一审稿）</span>
          <select v-model.number="form.paper_id">
            <option disabled value="">请选择论文</option>
            <option v-for="item in paperOptions" :key="item.paper_id" :value="item.paper_id">
              {{ item.label }}
            </option>
          </select>
        </label>
        <label class="field">
          <span>评审人 ID</span>
          <div class="readonly">{{ form.reviewer_id || '未登录' }}</div>
        </label>
        <label class="field">
          <span>评分</span>
          <input v-model.number="form.score" type="number" min="0" max="100" />
        </label>
        <label class="field">
          <span>结论</span>
          <select v-model="form.status">
            <option disabled value="">请选择结论</option>
            <option value="已通过">通过</option>
            <option value="未通过">不通过</option>
          </select>
        </label>
        <label class="field field-wide">
          <span>评审意见</span>
          <textarea v-model="form.opinion" placeholder="评审意见"></textarea>
        </label>
        <button class="primary" @click="submitReview">提交评审</button>
        <button class="ghost" @click="previewSelected">预览当前论文</button>
      </div>
    </div>

    <div v-if="preview.visible && preview.section === 'form'" class="panel">
      <div class="panel-head">
        <h3>一审稿预览</h3>
        <button class="ghost" @click="closePreview">关闭</button>
      </div>
      <div class="preview-meta">
        <div><strong>我的评分：</strong>{{ preview.myScore || '-' }}</div>
        <div><strong>我的意见：</strong>{{ preview.myOpinion || '-' }}</div>
        <div><strong>我的结论：</strong>{{ preview.myResult || '-' }}</div>
      </div>
      <div v-if="preview.error" class="preview-error">{{ preview.error }}</div>
      <div v-else class="preview-body" v-html="preview.html"></div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>一审稿列表</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>教师</th>
            <th>学生</th>
            <th>题目</th>
            <th>提交时间</th>
            <th>状态</th>
            <th>评审结论</th>
            <th>文件</th>
            <th>预览</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.paper_id">
            <td>{{ item.paper_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.teacher_name || '-' }}</td>
            <td>{{ item.student_name }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.paper_submission_time }}</td>
            <td>{{ item.review_status }}</td>
            <td>{{ item.judge_result || '-' }}</td>
            <td>
              <a v-if="item.file_url" :href="item.file_url" target="_blank" rel="noreferrer">查看</a>
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
        <h3>一审稿预览</h3>
        <button class="ghost" @click="closePreview">关闭</button>
      </div>
      <div class="preview-meta">
        <div><strong>我的评分：</strong>{{ preview.myScore || '-' }}</div>
        <div><strong>我的意见：</strong>{{ preview.myOpinion || '-' }}</div>
        <div><strong>我的结论：</strong>{{ preview.myResult || '-' }}</div>
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
const teachers = ref([]);
const opinions = ref([]);
const filters = ref({
  teacherId: ''
});
const form = ref({
  paper_id: '',
  reviewer_id: '',
  opinion: '',
  score: null,
  status: ''
});
const preview = ref({
  visible: false,
  section: '',
  html: '',
  error: '',
  myScore: '',
  myOpinion: '',
  myResult: ''
});

const setReviewerId = () => {
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    form.value.reviewer_id = stored?.username || '';
  } catch (err) {
    form.value.reviewer_id = '';
  }
};

const load = async () => {
  const [paperRes, proposalRes, applicationRes, topicRes, studentRes, teacherRes, opinionRes] = await Promise.all([
    api.get('/papers'),
    api.get('/thesis-proposals'),
    api.get('/topic-applications'),
    api.get('/topics'),
    api.get('/students'),
    api.get('/teachers'),
    api.get('/second-opinions')
  ]);
  const papers = (paperRes.data.data || []).filter((row) => row.paper_version === '一审稿');
  const proposals = proposalRes.data.data || [];
  const applications = applicationRes.data.data || [];
  const topics = topicRes.data.data || [];
  const students = studentRes.data.data || [];
  const teacherRows = teacherRes.data.data || [];
  const opinionRows = opinionRes.data.data || [];
  const appMap = new Map();
  applications.forEach((row) => appMap.set(row.application_id, row));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));
  const studentMap = new Map();
  students.forEach((row) => studentMap.set(row.stu_id, row.stu_name));
  const teacherMap = new Map();
  teacherRows.forEach((row) => teacherMap.set(row.tea_id, row.tea_name));

  items.value = papers.map((row) => {
    const proposal = proposals.find((p) => p.tp_id === row.thesis_proposal_Tp_id);
    const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
    const topic = app ? topicMap.get(app.topic_Topic_id) : null;
    const studentName = app ? (studentMap.get(app.student_Stu_id) || '') : '';
    const teacherName = topic ? (teacherMap.get(topic.teacher_Tea_id) || '') : '';
    const paperOpinions = opinionRows.filter((item) => item.paper_id === row.paper_id);
    const summary = summarizeSecondOpinions(paperOpinions);
    const myOpinion = paperOpinions.find((item) => item.reviewer_id === form.value.reviewer_id);
    const reviewStatus = myOpinion ? '我已打分' : '我未打分';
    return {
      ...row,
      topic_name: topic?.topic_name || '',
      student_name: studentName ? `${studentName}（${app?.student_Stu_id || ''}）` : '',
      paper_submission_time: formatTime(row.paper_submission_time),
      teacher_id: topic?.teacher_Tea_id || '',
      teacher_name: teacherName,
      judge_result: summary.result,
      review_status: reviewStatus
    };
  });

  paperOptions.value = items.value.map((row) => ({
    paper_id: row.paper_id,
    label: `${row.paper_id} - ${row.topic_name} - ${row.student_name}`
  }));
  if (!form.value.paper_id && paperOptions.value.length) {
    form.value.paper_id = paperOptions.value[0].paper_id;
  }
  teachers.value = teacherRows;
  form.value.reviewer_id = form.value.reviewer_id || '';
  preview.value.myScore = '';
  preview.value.myOpinion = '';
  preview.value.visible = false;
  preview.value.error = '';
  preview.value.html = '';
  opinions.value = opinionRows;
};

const filteredItems = computed(() => {
  if (!filters.value.teacherId) return items.value;
  return items.value.filter((item) => item.teacher_id === filters.value.teacherId);
});

const submitReview = async () => {
  if (!form.value.paper_id || !form.value.reviewer_id || !form.value.status) {
    alert('请填写论文、评审人及结论。');
    return;
  }
  if (!form.value.opinion || form.value.score == null) {
    alert('请填写评分与评审意见。');
    return;
  }
  const { data } = await api.post('/second-opinions', {
    paper_id: form.value.paper_id,
    reviewer_id: form.value.reviewer_id,
    sio_opinion: form.value.opinion,
    sio_score: form.value.score,
    sio_status: '提交',
    sio_details: JSON.stringify({ result: form.value.status })
  });
  if (!data.success) {
    alert(data.message || '评审提交失败');
    return;
  }
  form.value.opinion = '';
  await load();
};

const refreshForm = async () => {
  form.value.paper_id = '';
  form.value.opinion = '';
  form.value.score = null;
  form.value.status = '';
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
  const rows = opinions.value.filter((row) => row.paper_id === item.paper_id);
  const myOpinion = rows.find((row) => row.reviewer_id === form.value.reviewer_id);
  preview.value.myScore = myOpinion?.sio_score != null ? myOpinion.sio_score : '';
  preview.value.myOpinion = myOpinion?.sio_opinion || '';
  preview.value.myResult = parseResult(myOpinion?.sio_details, myOpinion?.sio_status);
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

const parseResult = (details, fallbackStatus) => {
  if (details) {
    try {
      const parsed = JSON.parse(details);
      if (parsed?.result) return parsed.result;
    } catch (err) {
      // ignore
    }
  }
  return fallbackStatus || '';
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

const summarizeSecondOpinions = (rows) => {
  if (!rows || !rows.length) {
    return { result: '' };
  }
  const hasReject = rows.some((row) => isReject(parseResult(row.sio_details, row.sio_status)));
  const result = hasReject ? '不通过' : '通过';
  return { result };
};

const isReject = (value) => {
  if (!value) return false;
  return value.includes('未通过') || value.includes('不通过');
};

setReviewerId();
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
