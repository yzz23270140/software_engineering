<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>答辩与记录</h2>
        <p>查看终稿答辩评分与评委意见。</p>
      </div>
      <RolePill role-label="学生" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>答辩评分记录</h3>
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
            <th>评委意见</th>
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
            <td class="comment">{{ item.dr_comment || '-' }}</td>
            <td>
              <button class="ghost" @click="openPreview(item)">预览</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="preview.visible" class="panel">
      <div class="panel-head">
        <h3>终稿预览</h3>
        <button class="ghost" @click="closePreview">关闭</button>
      </div>
      <div v-if="preview.error" class="preview-error">{{ preview.error }}</div>
      <div v-else class="preview-body" v-html="preview.html"></div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>答辩记录详情</h3>
      </div>
      <div class="record-grid">
        <div v-for="item in items" :key="`record-${item.dr_id}`" class="record-card">
          <div class="record-title">
            {{ item.topic_name }}（{{ item.paper_title }}）
          </div>
          <p><strong>答辩时间：</strong>{{ item.dr_time || '-' }}</p>
          <p><strong>地点：</strong>{{ item.dr_location || '-' }}</p>
          <p><strong>答辩记录：</strong>{{ item.dr_record || '-' }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const preview = ref({
  visible: false,
  html: '',
  error: ''
});

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const load = async () => {
  let studentId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    studentId = stored?.username || '';
  } catch (err) {
    studentId = '';
  }
  const [defenseRes, paperRes, proposalRes, applicationRes, topicRes, teacherRes, studentRes] = await Promise.all([
    api.get('/defense-records'),
    api.get('/papers'),
    api.get('/thesis-proposals'),
    api.get('/topic-applications', { params: { studentId } }),
    api.get('/topics'),
    api.get('/teachers'),
    api.get('/students')
  ]);

  const defenses = defenseRes.data.data || [];
  const papers = (paperRes.data.data || []).filter((row) => row.paper_version === '终稿');
  const proposals = proposalRes.data.data || [];
  const apps = applicationRes.data.data || [];
  const topics = topicRes.data.data || [];
  const teachers = teacherRes.data.data || [];
  const students = studentRes.data.data || [];

  const appMap = new Map();
  apps.forEach((row) => appMap.set(row.application_id, row));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));
  const proposalMap = new Map();
  proposals.forEach((row) => proposalMap.set(row.tp_id, row));
  const teacherMap = new Map();
  teachers.forEach((row) => teacherMap.set(row.tea_id, row.tea_name));
  const studentMap = new Map();
  students.forEach((row) => studentMap.set(row.stu_id, row.stu_name));

  const studentPaperIds = new Set(
    papers.filter((row) => {
      const proposal = proposalMap.get(row.thesis_proposal_Tp_id);
      const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
      return Boolean(app);
    }).map((row) => row.paper_id)
  );

  items.value = defenses
    .filter((row) => studentPaperIds.has(row.paper_id))
    .map((row) => {
      const paper = papers.find((p) => p.paper_id === row.paper_id);
      const proposal = paper ? proposalMap.get(paper.thesis_proposal_Tp_id) : null;
      const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
      const topic = app ? topicMap.get(app.topic_Topic_id) : null;
      const teacherName = topic ? (teacherMap.get(topic.teacher_Tea_id) || '') : '';
      const studentName = app ? (studentMap.get(app.student_Stu_id) || '') : '';
      return {
        ...row,
        topic_name: topic?.topic_name || '',
        paper_title: paper?.paper_title || '',
        paper_text: paper?.paper_text || '',
        teacher_name: teacherName ? `${teacherName}（${topic?.teacher_Tea_id || ''}）` : '',
        student_name: studentName ? `${studentName}（${app?.student_Stu_id || ''}）` : '',
        dr_time: formatTime(row.dr_time)
      };
    });
};

const openPreview = async (item) => {
  preview.value.visible = true;
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
  font-size: 24px;
}
.page-header p {
  margin: 0;
  font-size: 13px;
  opacity: 0.9;
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
.comment {
  max-width: 260px;
}
.preview-body {
  padding: 8px 0;
  line-height: 1.6;
}
.preview-error {
  color: #b91c1c;
  font-size: 13px;
}
.record-grid {
  margin-top: 12px;
  display: grid;
  gap: 12px;
}
.record-card {
  border: 1px dashed #cbd5e1;
  border-radius: 6px;
  padding: 12px;
  font-size: 13px;
  background: #f8fafc;
}
.record-title {
  font-weight: 600;
  margin-bottom: 6px;
}
.ghost {
  background: #eff6fb;
  border: 1px solid #cfe1ee;
  color: #0f3a55;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}
</style>
