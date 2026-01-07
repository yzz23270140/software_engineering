<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>毕设论文上传</h2>
        <p>提交初稿与一审稿，并查看教师与评审组反馈。</p>
      </div>
      <RolePill role-label="学生" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>初稿提交</h3>
        <button class="ghost" @click="refreshDraft">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>开题报告</span>
          <select v-model.number="draftForm.thesis_proposal_Tp_id">
            <option disabled value="">请选择开题报告</option>
            <option v-for="item in draftOptions" :key="item.tp_id" :value="item.tp_id">
              {{ item.label }}
            </option>
          </select>
        </label>
        <label class="field">
          <span>论文题目</span>
          <input v-model="draftForm.paper_title" placeholder="论文标题" />
        </label>
        <label class="field field-wide">
          <span>摘要</span>
          <textarea v-model="draftForm.paper_abstract" placeholder="摘要与关键结论"></textarea>
        </label>
        <div class="field field-wide">
          <span>Word 文件上传</span>
          <div
            class="drop-zone"
            @dragover.prevent
            @drop.prevent="handleDrop('draft', $event)"
          >
            <p>拖拽 .doc/.docx 到这里，或</p>
            <button class="ghost" type="button" @click="triggerFile('draft')">选择文件</button>
            <input
              ref="draftFileInput"
              type="file"
              accept=".doc,.docx"
              class="hidden-input"
              @change="handleFileChange('draft', $event)"
            />
            <p class="file-name">{{ draftFileName || '未选择文件' }}</p>
          </div>
        </div>
        <div class="field">
          <span>状态</span>
          <div class="readonly">已提交</div>
        </div>
        <button class="primary" @click="submitDraft">提交初稿</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>初稿记录</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>题目</th>
            <th>提交时间</th>
            <th>状态</th>
            <th>教师反馈</th>
            <th>文件名</th>
            <th>下载</th>
            <th>预览</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in draftItems" :key="item.paper_id">
            <td>{{ item.paper_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.paper_submission_time }}</td>
            <td>{{ formatStatus(item.paper_status) }}</td>
            <td>
              <div class="feedback">
                <div>{{ item.teacher_scores }}</div>
                <div>{{ item.teacher_opinion || '-' }}</div>
              </div>
            </td>
            <td>{{ item.file_url || '-' }}</td>
            <td>
              <a v-if="item.paper_text" :href="item.paper_text" :download="item.file_url || 'paper.docx'">
                下载
              </a>
              <span v-else>-</span>
            </td>
            <td>
              <button class="ghost" @click="openPreview('draft', item)">预览</button>
            </td>
            <td>
              <button class="ghost danger" @click="removeItem(item.paper_id)">撤回</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="preview.visible && preview.section === 'draft'" class="panel">
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

    <div class="panel">
      <div class="panel-head">
        <h3>一审稿提交</h3>
        <button class="ghost" @click="refreshReview">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>开题报告</span>
          <select v-model.number="reviewForm.thesis_proposal_Tp_id">
            <option disabled value="">请选择开题报告</option>
            <option v-for="item in reviewOptions" :key="item.tp_id" :value="item.tp_id">
              {{ item.label }}
            </option>
          </select>
        </label>
        <label class="field">
          <span>论文题目</span>
          <input v-model="reviewForm.paper_title" placeholder="论文标题" />
        </label>
        <label class="field field-wide">
          <span>摘要</span>
          <textarea v-model="reviewForm.paper_abstract" placeholder="摘要与关键结论"></textarea>
        </label>
        <div class="field field-wide">
          <span>Word 文件上传</span>
          <div
            class="drop-zone"
            @dragover.prevent
            @drop.prevent="handleDrop('review', $event)"
          >
            <p>拖拽 .doc/.docx 到这里，或</p>
            <button class="ghost" type="button" @click="triggerFile('review')">选择文件</button>
            <input
              ref="reviewFileInput"
              type="file"
              accept=".doc,.docx"
              class="hidden-input"
              @change="handleFileChange('review', $event)"
            />
            <p class="file-name">{{ reviewFileName || '未选择文件' }}</p>
          </div>
        </div>
        <div class="field">
          <span>状态</span>
          <div class="readonly">已提交</div>
        </div>
        <button class="primary" @click="submitReview">提交一审稿</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>一审稿记录</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>题目</th>
            <th>提交时间</th>
            <th>状态</th>
            <th>评审均分</th>
            <th>评审结论</th>
            <th>文件名</th>
            <th>下载</th>
            <th>预览</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in reviewItems" :key="item.paper_id">
            <td>{{ item.paper_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.paper_submission_time }}</td>
            <td>{{ item.review_status }}</td>
            <td>{{ item.judge_avg_score || '-' }}</td>
            <td>{{ item.judge_result || '-' }}</td>
            <td>{{ item.file_url || '-' }}</td>
            <td>
              <a v-if="item.paper_text" :href="item.paper_text" :download="item.file_url || 'paper.docx'">
                下载
              </a>
              <span v-else>-</span>
            </td>
            <td>
              <button class="ghost" @click="openPreview('review', item)">预览</button>
            </td>
            <td>
              <button class="ghost danger" @click="removeItem(item.paper_id)">撤回</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="preview.visible && preview.section === 'review'" class="panel">
      <div class="panel-head">
        <h3>一审稿预览</h3>
        <button class="ghost" @click="closePreview">关闭</button>
      </div>
      <div class="preview-meta">
        <div><strong>评审均分：</strong>{{ preview.judgeAvg || '-' }}</div>
        <div><strong>评审结论：</strong>{{ preview.judgeResult || '-' }}</div>
      </div>
      <div class="preview-opinions" v-if="preview.judgeAll.length">
        <div class="preview-opinion-title">评委意见列表</div>
        <div v-for="(item, index) in preview.judgeAll" :key="`${item.reviewer_id}-${index}`" class="opinion-card">
          <div><strong>评委：</strong>{{ item.reviewer_id || '-' }}</div>
          <div><strong>评分：</strong>{{ item.score || '-' }}</div>
          <div><strong>结论：</strong>{{ item.result || '-' }}</div>
          <div><strong>意见：</strong>{{ item.opinion || '-' }}</div>
        </div>
      </div>
      <div v-if="preview.error" class="preview-error">{{ preview.error }}</div>
      <div v-else class="preview-body" v-html="preview.html"></div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>终稿提交</h3>
        <p class="hint">终稿评分请在“答辩与记录”查看。</p>
        <button class="ghost" @click="refreshFinal">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>开题报告</span>
          <select v-model.number="finalForm.thesis_proposal_Tp_id">
            <option disabled value="">请选择开题报告</option>
            <option v-for="item in finalOptions" :key="item.tp_id" :value="item.tp_id">
              {{ item.label }}
            </option>
          </select>
        </label>
        <label class="field">
          <span>论文题目</span>
          <input v-model="finalForm.paper_title" placeholder="论文标题" />
        </label>
        <label class="field field-wide">
          <span>摘要</span>
          <textarea v-model="finalForm.paper_abstract" placeholder="摘要与关键结论"></textarea>
        </label>
        <div class="field field-wide">
          <span>Word 文件上传</span>
          <div
            class="drop-zone"
            @dragover.prevent
            @drop.prevent="handleDrop('final', $event)"
          >
            <p>拖拽 .doc/.docx 到这里，或</p>
            <button class="ghost" type="button" @click="triggerFile('final')">选择文件</button>
            <input
              ref="finalFileInput"
              type="file"
              accept=".doc,.docx"
              class="hidden-input"
              @change="handleFileChange('final', $event)"
            />
            <p class="file-name">{{ finalFileName || '未选择文件' }}</p>
          </div>
        </div>
        <div class="field">
          <span>状态</span>
          <div class="readonly">已提交</div>
        </div>
        <button class="primary" @click="submitFinal">提交终稿</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>终稿记录</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题</th>
            <th>题目</th>
            <th>提交时间</th>
            <th>状态</th>
            <th>文件名</th>
            <th>下载</th>
            <th>预览</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in finalItems" :key="item.paper_id">
            <td>{{ item.paper_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.paper_submission_time }}</td>
            <td>{{ formatStatus(item.paper_status) }}</td>
            <td>{{ item.file_url || '-' }}</td>
            <td>
              <a v-if="item.paper_text" :href="item.paper_text" :download="item.file_url || 'paper.docx'">
                下载
              </a>
              <span v-else>-</span>
            </td>
            <td>
              <button class="ghost" @click="openPreview('final', item)">预览</button>
            </td>
            <td>
              <button class="ghost danger" @click="removeItem(item.paper_id)">撤回</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="preview.visible && preview.section === 'final'" class="panel">
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
import { ref, onMounted } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const draftItems = ref([]);
const reviewItems = ref([]);
const finalItems = ref([]);
const draftOptions = ref([]);
const reviewOptions = ref([]);
const finalOptions = ref([]);
const draftForm = ref({
  thesis_proposal_Tp_id: '',
  paper_title: '',
  paper_abstract: '',
  file_url: '',
  paper_text: ''
});
const reviewForm = ref({
  thesis_proposal_Tp_id: '',
  paper_title: '',
  paper_abstract: '',
  file_url: '',
  paper_text: ''
});
const finalForm = ref({
  thesis_proposal_Tp_id: '',
  paper_title: '',
  paper_abstract: '',
  file_url: '',
  paper_text: ''
});
const draftFileName = ref('');
const reviewFileName = ref('');
const finalFileName = ref('');
const draftFileInput = ref(null);
const reviewFileInput = ref(null);
const finalFileInput = ref(null);
const preview = ref({
  visible: false,
  section: '',
  html: '',
  error: '',
  teacherScores: '',
  teacherOpinion: '',
  judgeScore: '',
  judgeOpinion: '',
  judgeAvg: '',
  judgeResult: '',
  judgeAll: []
});

const load = async () => {
  let studentId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    studentId = stored?.username || '';
  } catch (err) {
    studentId = '';
  }
  const [paperRes, applicationRes, proposalRes, topicRes, midtermRes, firstRes, secondRes] = await Promise.all([
    api.get('/papers'),
    api.get('/topic-applications', { params: { studentId } }),
    api.get('/thesis-proposals'),
    api.get('/topics'),
    api.get('/midterm-reports'),
    api.get('/first-opinions'),
    api.get('/second-opinions')
  ]);
  const apps = applicationRes.data.data || [];
  const proposals = proposalRes.data.data || [];
  const topics = topicRes.data.data || [];
  const midterms = midtermRes.data.data || [];
  const firstOpinions = firstRes.data.data || [];
  const secondOpinions = secondRes.data.data || [];

  const appMap = new Map();
  apps.forEach((row) => appMap.set(row.application_id, row));
  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));

  const proposalRows = proposals.filter((row) => appMap.has(row.topic_selection_application_id));
  const passedThesis = proposalRows.filter((row) => row.tp_status === '已通过');
  const passedMidterm = new Set(
    midterms.filter((row) => row.mtr_status === '已通过').map((row) => row.thesis_proposal_Tp_id)
  );
  const eligibleThesis = passedThesis.filter((row) => passedMidterm.has(row.tp_id));

  const allPapers = (paperRes.data.data || [])
    .filter((row) => proposalRows.some((p) => p.tp_id === row.thesis_proposal_Tp_id))
    .map((row) => {
      const proposal = proposalRows.find((p) => p.tp_id === row.thesis_proposal_Tp_id);
      const app = proposal ? appMap.get(proposal.topic_selection_application_id) : null;
      const topic = app ? topicMap.get(app.topic_Topic_id) : null;
      return {
        ...row,
        topic_name: topic?.topic_name || '',
        paper_submission_time: formatTime(row.paper_submission_time)
      };
    });

  draftItems.value = allPapers
    .filter((row) => row.paper_version === '初稿')
    .map((row) => {
      const opinion = firstOpinions.find((item) => item.paper_id === row.paper_id);
      const details = parseDetails(opinion?.fio_details);
      return {
        ...row,
        teacher_opinion: opinion?.fio_opinion || '',
        teacher_scores: formatTeacherScores(details)
      };
    });

  reviewItems.value = allPapers
    .filter((row) => row.paper_version === '一审稿')
    .map((row) => {
      const paperOpinions = secondOpinions.filter((item) => item.paper_id === row.paper_id);
      const opinion = paperOpinions[0];
      const summary = summarizeSecondOpinions(paperOpinions);
      const reviewStatus = paperOpinions.length
        ? `已有${paperOpinions.length}位评委打分`
        : '已提交';
      return {
        ...row,
        review_status: reviewStatus,
        judge_avg_score: summary.avg,
        judge_result: summary.result,
        judge_all: paperOpinions.map((item) => ({
          reviewer_id: item.reviewer_id,
          score: item.sio_score,
          opinion: item.sio_opinion,
          result: parseResult(item.sio_details, item.sio_status)
        }))
      };
    });

  finalItems.value = allPapers
    .filter((row) => row.paper_version === '终稿')
    .map((row) => ({
      ...row
    }));

  const draftPassed = new Set(
    allPapers.filter((row) => row.paper_version === '初稿' && row.paper_status === '已通过')
      .map((row) => row.thesis_proposal_Tp_id)
  );
  const reviewPassed = new Set(
    allPapers.filter((row) => row.paper_version === '一审稿' && row.paper_status === '已通过')
      .map((row) => row.thesis_proposal_Tp_id)
  );

  draftOptions.value = eligibleThesis.map((row) => {
    const app = appMap.get(row.topic_selection_application_id);
    const topic = app ? topicMap.get(app.topic_Topic_id) : null;
    return {
      tp_id: row.tp_id,
      label: `${row.tp_id} - ${topic?.topic_name || ''}`
    };
  });
  reviewOptions.value = eligibleThesis
    .filter((row) => draftPassed.has(row.tp_id))
    .map((row) => {
      const app = appMap.get(row.topic_selection_application_id);
      const topic = app ? topicMap.get(app.topic_Topic_id) : null;
      return {
        tp_id: row.tp_id,
        label: `${row.tp_id} - ${topic?.topic_name || ''}`
      };
    });
  finalOptions.value = eligibleThesis
    .filter((row) => reviewPassed.has(row.tp_id))
    .map((row) => {
      const app = appMap.get(row.topic_selection_application_id);
      const topic = app ? topicMap.get(app.topic_Topic_id) : null;
      return {
        tp_id: row.tp_id,
        label: `${row.tp_id} - ${topic?.topic_name || ''}`
      };
    });

  if (!draftForm.value.thesis_proposal_Tp_id && draftOptions.value.length) {
    draftForm.value.thesis_proposal_Tp_id = draftOptions.value[0].tp_id;
  }
  if (!reviewForm.value.thesis_proposal_Tp_id && reviewOptions.value.length) {
    reviewForm.value.thesis_proposal_Tp_id = reviewOptions.value[0].tp_id;
  }
  if (!finalForm.value.thesis_proposal_Tp_id && finalOptions.value.length) {
    finalForm.value.thesis_proposal_Tp_id = finalOptions.value[0].tp_id;
  }
};

const submitDraft = async () => {
  if (!draftForm.value.thesis_proposal_Tp_id || !draftForm.value.paper_title) {
    alert('请填写开题报告与论文题目。');
    return;
  }
  if (!draftForm.value.paper_text) {
    alert('请上传 Word 文件。');
    return;
  }
  const { data } = await api.post('/papers', {
    thesis_proposal_Tp_id: draftForm.value.thesis_proposal_Tp_id,
    paper_version: '初稿',
    paper_title: draftForm.value.paper_title,
    paper_abstract: draftForm.value.paper_abstract,
    file_url: draftForm.value.file_url,
    paper_text: draftForm.value.paper_text,
    paper_status: '已提交'
  });
  if (!data.success) {
    alert(data.message || '初稿提交失败');
    return;
  }
  draftForm.value.paper_title = '';
  draftForm.value.paper_abstract = '';
  draftForm.value.file_url = '';
  draftForm.value.paper_text = '';
  draftFileName.value = '';
  await load();
};

const refreshDraft = async () => {
  draftForm.value.paper_title = '';
  draftForm.value.paper_abstract = '';
  draftForm.value.file_url = '';
  draftForm.value.paper_text = '';
  draftFileName.value = '';
  await load();
};

const submitReview = async () => {
  if (!reviewForm.value.thesis_proposal_Tp_id || !reviewForm.value.paper_title) {
    alert('请填写开题报告与论文题目。');
    return;
  }
  if (!reviewForm.value.paper_text) {
    alert('请上传 Word 文件。');
    return;
  }
  const { data } = await api.post('/papers', {
    thesis_proposal_Tp_id: reviewForm.value.thesis_proposal_Tp_id,
    paper_version: '一审稿',
    paper_title: reviewForm.value.paper_title,
    paper_abstract: reviewForm.value.paper_abstract,
    file_url: reviewForm.value.file_url,
    paper_text: reviewForm.value.paper_text,
    paper_status: '已提交'
  });
  if (!data.success) {
    alert(data.message || '一审稿提交失败');
    return;
  }
  reviewForm.value.paper_title = '';
  reviewForm.value.paper_abstract = '';
  reviewForm.value.file_url = '';
  reviewForm.value.paper_text = '';
  reviewFileName.value = '';
  await load();
};

const refreshReview = async () => {
  reviewForm.value.paper_title = '';
  reviewForm.value.paper_abstract = '';
  reviewForm.value.file_url = '';
  reviewForm.value.paper_text = '';
  reviewFileName.value = '';
  await load();
};

const submitFinal = async () => {
  if (!finalForm.value.thesis_proposal_Tp_id || !finalForm.value.paper_title) {
    alert('请填写开题报告与论文题目。');
    return;
  }
  if (!finalForm.value.paper_text) {
    alert('请上传 Word 文件。');
    return;
  }
  const { data } = await api.post('/papers', {
    thesis_proposal_Tp_id: finalForm.value.thesis_proposal_Tp_id,
    paper_version: '终稿',
    paper_title: finalForm.value.paper_title,
    paper_abstract: finalForm.value.paper_abstract,
    file_url: finalForm.value.file_url,
    paper_text: finalForm.value.paper_text,
    paper_status: '已提交'
  });
  if (!data.success) {
    alert(data.message || '终稿提交失败');
    return;
  }
  finalForm.value.paper_title = '';
  finalForm.value.paper_abstract = '';
  finalForm.value.file_url = '';
  finalForm.value.paper_text = '';
  finalFileName.value = '';
  await load();
};

const refreshFinal = async () => {
  finalForm.value.paper_title = '';
  finalForm.value.paper_abstract = '';
  finalForm.value.file_url = '';
  finalForm.value.paper_text = '';
  finalFileName.value = '';
  await load();
};

const removeItem = async (id) => {
  if (!confirm('确认撤回该论文记录吗？')) {
    return;
  }
  const { data } = await api.delete(`/papers/${id}`);
  if (!data.success) {
    alert(data.message || '撤回失败');
    return;
  }
  await load();
};

const triggerFile = (type) => {
  if (type === 'draft') {
    draftFileInput.value?.click();
  } else if (type === 'review') {
    reviewFileInput.value?.click();
  } else if (type === 'final') {
    finalFileInput.value?.click();
  }
};

const handleDrop = (type, event) => {
  const file = event.dataTransfer?.files?.[0];
  if (file) {
    handleFile(type, file);
  }
};

const handleFileChange = (type, event) => {
  const file = event.target?.files?.[0];
  if (file) {
    handleFile(type, file);
  }
};

const handleFile = (type, file) => {
  const name = file.name || '';
  if (!name.endsWith('.doc') && !name.endsWith('.docx')) {
    alert('仅支持 .doc 或 .docx 文件。');
    return;
  }
  const reader = new FileReader();
  reader.onload = () => {
    const content = reader.result ? String(reader.result) : '';
    if (type === 'draft') {
      draftForm.value.paper_text = content;
      draftForm.value.file_url = name;
      draftFileName.value = name;
    } else if (type === 'review') {
      reviewForm.value.paper_text = content;
      reviewForm.value.file_url = name;
      reviewFileName.value = name;
    } else if (type === 'final') {
      finalForm.value.paper_text = content;
      finalForm.value.file_url = name;
      finalFileName.value = name;
    }
  };
  reader.readAsDataURL(file);
};

const openPreview = async (section, item) => {
  preview.value.visible = true;
  preview.value.section = section;
  preview.value.html = '';
  preview.value.error = '';
  preview.value.teacherScores = item.teacher_scores || '';
  preview.value.teacherOpinion = item.teacher_opinion || '';
  preview.value.judgeScore = item.judge_score || '';
  preview.value.judgeOpinion = item.judge_opinion || '';
  preview.value.judgeAvg = item.judge_avg_score || '';
  preview.value.judgeResult = item.judge_result || '';
  preview.value.judgeAll = item.judge_all || [];
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

const summarizeSecondOpinions = (rows) => {
  if (!rows || !rows.length) {
    return { avg: '', result: '' };
  }
  const scores = rows.map((row) => row.sio_score).filter((v) => typeof v === 'number');
  const avg = scores.length
    ? (scores.reduce((sum, v) => sum + v, 0) / scores.length).toFixed(2)
    : '';
  const hasReject = rows.some((row) => isReject(parseResult(row.sio_details, row.sio_status)));
  const result = hasReject ? '不通过' : '通过';
  return { avg, result };
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

const isReject = (value) => {
  if (!value) return false;
  return value.includes('未通过') || value.includes('不通过');
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
.drop-zone {
  border: 1px dashed #93c5fd;
  border-radius: 6px;
  padding: 12px;
  background: #eff6ff;
  display: grid;
  gap: 6px;
  justify-items: start;
}
.hidden-input {
  display: none;
}
.file-name {
  font-size: 12px;
  color: #475569;
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
.preview-opinions {
  border-top: 1px dashed #cbd5e1;
  padding-top: 10px;
  margin-top: 10px;
  display: grid;
  gap: 8px;
}
.preview-opinion-title {
  font-size: 13px;
  font-weight: 600;
  color: #0f3a55;
}
.opinion-card {
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 8px;
  background: #f8fafc;
  font-size: 12px;
  display: grid;
  gap: 4px;
}
.feedback {
  display: grid;
  gap: 4px;
  font-size: 12px;
  color: #0f3a55;
}
.hint {
  margin: 0;
  color: #64748b;
  font-size: 12px;
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
