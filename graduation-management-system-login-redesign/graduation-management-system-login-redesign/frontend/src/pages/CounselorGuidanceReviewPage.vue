<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>学生记录查看</h2>
        <p>查看所负责学生的选题、开题、中期与论文记录。</p>
      </div>
      <RolePill role-label="辅导员" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>筛选条件</h3>
        <button class="ghost" @click="refreshAll">刷新</button>
      </div>
      <div class="form-grid">
        <label class="field field-wide">
          <span>学生</span>
          <select v-model="selectedStudent">
            <option disabled value="">请选择学生</option>
            <option v-for="item in filteredStudents" :key="item.stu_id" :value="item.stu_id">
              {{ item.stu_name }}（{{ item.stu_id }}）
            </option>
          </select>
        </label>
        <label class="field">
          <span>教师</span>
          <select v-model="filters.teacherId">
            <option value="">全部</option>
            <option v-for="item in teachers" :key="item.tea_id" :value="item.tea_id">
              {{ item.tea_name }}（{{ item.tea_id }}）
            </option>
          </select>
        </label>
        <label class="field">
          <span>选题状态</span>
          <select v-model="filters.topicStatus">
            <option value="">全部</option>
            <option value="未审核">未审核</option>
            <option value="审核通过">审核通过</option>
            <option value="审核不通过">审核不通过</option>
          </select>
        </label>
        <label class="field">
          <span>开题状态</span>
          <select v-model="filters.thesisStatus">
            <option value="">全部</option>
            <option value="已提交">已提交</option>
            <option value="评审中">评审中</option>
            <option value="已通过">已通过</option>
            <option value="未通过">未通过</option>
          </select>
        </label>
        <label class="field">
          <span>中期状态</span>
          <select v-model="filters.midtermStatus">
            <option value="">全部</option>
            <option value="已提交">已提交</option>
            <option value="评审中">评审中</option>
            <option value="已通过">已通过</option>
            <option value="未通过">未通过</option>
          </select>
        </label>
        <label class="field">
          <span>指导确认</span>
          <select v-model="filters.guidanceStatus">
            <option value="">全部</option>
            <option value="已确认">已确认</option>
            <option value="未确认">未确认</option>
          </select>
        </label>
        <label class="field">
          <span>论文状态</span>
          <select v-model="filters.paperStatus">
            <option value="">全部</option>
            <option value="已提交">已提交</option>
            <option value="评审中">评审中</option>
            <option value="已通过">已通过</option>
            <option value="未通过">未通过</option>
          </select>
        </label>
        <label class="field">
          <span>答辩结果</span>
          <select v-model="filters.defenseResult">
            <option value="">全部</option>
            <option value="优秀">优秀</option>
            <option value="良好">良好</option>
            <option value="中等">中等</option>
            <option value="及格">及格</option>
            <option value="不及格">不及格</option>
          </select>
        </label>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>选题申请</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>申请ID</th>
            <th>课题</th>
            <th>教师</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in topicApplications" :key="item.application_id">
            <td>{{ item.application_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.teacher_name }}</td>
            <td>{{ item.tsa_status }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>开题报告</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>开题ID</th>
            <th>题目</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in thesisProposals" :key="item.tp_id">
            <td>{{ item.tp_id }}</td>
            <td>{{ item.tp_title }}</td>
            <td>{{ item.tp_status }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>中期汇报</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>中期ID</th>
            <th>提交时间</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in midtermReports" :key="item.mtr_id">
            <td>{{ item.mtr_id }}</td>
            <td>{{ item.mtr_submission_time }}</td>
            <td>{{ item.mtr_status }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>中期指导记录</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>记录ID</th>
            <th>指导时间</th>
            <th>方式</th>
            <th>确认状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in guidanceRecords" :key="item.gr_id">
            <td>{{ item.gr_id }}</td>
            <td>{{ item.gr_time }}</td>
            <td>{{ item.gr_method }}</td>
            <td>{{ item.gr_confirm }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>论文提交</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>论文ID</th>
            <th>版本</th>
            <th>题目</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in papers" :key="item.paper_id">
            <td>{{ item.paper_id }}</td>
            <td>{{ item.paper_version }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.paper_status }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>答辩记录</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>答辩ID</th>
            <th>终稿</th>
            <th>总分</th>
            <th>结果</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in defenseRecords" :key="item.dr_id">
            <td>{{ item.dr_id }}</td>
            <td>{{ item.paper_title }}</td>
            <td>{{ item.dr_total_score }}</td>
            <td>{{ item.dr_result }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const students = ref([]);
const teachers = ref([]);
const selectedStudent = ref('');
const allApplications = ref([]);
const allProposals = ref([]);
const allMidterms = ref([]);
const allGuidances = ref([]);
const allPapers = ref([]);
const allDefenses = ref([]);
const allTopics = ref([]);
const allTeachers = ref([]);
const filters = ref({
  teacherId: '',
  topicStatus: '',
  thesisStatus: '',
  midtermStatus: '',
  guidanceStatus: '',
  paperStatus: '',
  defenseResult: ''
});
const topicApplications = ref([]);
const thesisProposals = ref([]);
const midtermReports = ref([]);
const guidanceRecords = ref([]);
const papers = ref([]);
const defenseRecords = ref([]);

const formatTime = (value) => {
  if (!value) return '';
  return String(value).replace('T', ' ').replace('Z', '');
};

const filteredStudents = computed(() => {
  if (!students.value.length) return [];
  const appByStudent = new Map();
  allApplications.value.forEach((row) => {
    const list = appByStudent.get(row.student_Stu_id) || [];
    list.push(row);
    appByStudent.set(row.student_Stu_id, list);
  });
  const proposalByApp = new Map();
  allProposals.value.forEach((row) => {
    const list = proposalByApp.get(row.topic_selection_application_id) || [];
    list.push(row);
    proposalByApp.set(row.topic_selection_application_id, list);
  });
  const midtermByProposal = new Map();
  allMidterms.value.forEach((row) => {
    const list = midtermByProposal.get(row.thesis_proposal_Tp_id) || [];
    list.push(row);
    midtermByProposal.set(row.thesis_proposal_Tp_id, list);
  });
  const guidanceByProposal = new Map();
  allGuidances.value.forEach((row) => {
    const list = guidanceByProposal.get(row.thesis_proposal_Tp_id) || [];
    list.push(row);
    guidanceByProposal.set(row.thesis_proposal_Tp_id, list);
  });
  const paperByProposal = new Map();
  allPapers.value.forEach((row) => {
    const list = paperByProposal.get(row.thesis_proposal_Tp_id) || [];
    list.push(row);
    paperByProposal.set(row.thesis_proposal_Tp_id, list);
  });
  const defenseByPaper = new Map();
  allDefenses.value.forEach((row) => {
    const list = defenseByPaper.get(row.paper_id) || [];
    list.push(row);
    defenseByPaper.set(row.paper_id, list);
  });
  const topicMap = new Map();
  allTopics.value.forEach((row) => topicMap.set(row.topic_id, row));

  const matchesFilter = (items, predicate) => items.some(predicate);

  return students.value.filter((student) => {
    const applications = appByStudent.get(student.stu_id) || [];
    const applicationIds = applications.map((row) => row.application_id);
    const proposals = applicationIds.flatMap((id) => proposalByApp.get(id) || []);
    const proposalIds = proposals.map((row) => row.tp_id);
    const midterms = proposalIds.flatMap((id) => midtermByProposal.get(id) || []);
    const guidances = proposalIds.flatMap((id) => guidanceByProposal.get(id) || []);
    const papers = proposalIds.flatMap((id) => paperByProposal.get(id) || []);
    const paperIds = papers.map((row) => row.paper_id);
    const defenses = paperIds.flatMap((id) => defenseByPaper.get(id) || []);

    if (filters.value.teacherId) {
      const hasTeacher = matchesFilter(applications, (row) => {
        const topic = topicMap.get(row.topic_Topic_id);
        return topic?.teacher_Tea_id === filters.value.teacherId;
      });
      if (!hasTeacher) return false;
    }
    if (filters.value.topicStatus) {
      if (!matchesFilter(applications, (row) => row.tsa_status === filters.value.topicStatus)) return false;
    }
    if (filters.value.thesisStatus) {
      if (!matchesFilter(proposals, (row) => row.tp_status === filters.value.thesisStatus)) return false;
    }
    if (filters.value.midtermStatus) {
      if (!matchesFilter(midterms, (row) => row.mtr_status === filters.value.midtermStatus)) return false;
    }
    if (filters.value.guidanceStatus) {
      if (!matchesFilter(guidances, (row) => row.gr_confirm === filters.value.guidanceStatus)) return false;
    }
    if (filters.value.paperStatus) {
      if (!matchesFilter(papers, (row) => row.paper_status === filters.value.paperStatus)) return false;
    }
    if (filters.value.defenseResult) {
      if (!matchesFilter(defenses, (row) => row.dr_result === filters.value.defenseResult)) return false;
    }
    return true;
  });
});


const loadStudents = async () => {
  let counselorId = '';
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    counselorId = stored?.username || '';
  } catch (err) {
    counselorId = '';
  }
  const studentRes = await api.get('/students');
  const data = studentRes.data;
  students.value = (data.data || []).filter((row) => row.instructor_Ins_id === counselorId);
};

const loadAllData = async () => {
  const [applicationRes, proposalRes, midtermRes, guidanceRes, paperRes, defenseRes, topicRes, teacherRes] =
    await Promise.all([
      api.get('/topic-applications'),
      api.get('/thesis-proposals'),
      api.get('/midterm-reports'),
      api.get('/guidance-records'),
      api.get('/papers'),
      api.get('/defense-records'),
      api.get('/topics'),
      api.get('/teachers')
    ]);

  allApplications.value = applicationRes.data.data || [];
  allProposals.value = proposalRes.data.data || [];
  allMidterms.value = midtermRes.data.data || [];
  allGuidances.value = guidanceRes.data.data || [];
  allPapers.value = paperRes.data.data || [];
  allDefenses.value = defenseRes.data.data || [];
  allTopics.value = topicRes.data.data || [];
  allTeachers.value = teacherRes.data.data || [];
  teachers.value = allTeachers.value;
};

const loadRecords = async () => {
  if (!selectedStudent.value) {
    topicApplications.value = [];
    thesisProposals.value = [];
    midtermReports.value = [];
    guidanceRecords.value = [];
    papers.value = [];
    defenseRecords.value = [];
    return;
  }
  const applications = allApplications.value.filter((row) => row.student_Stu_id === selectedStudent.value);
  const proposals = allProposals.value;
  const midterms = allMidterms.value;
  const guidances = allGuidances.value;
  const paperRows = allPapers.value;
  const defenses = allDefenses.value;
  const topics = allTopics.value;
  const teachers = allTeachers.value;

  const topicMap = new Map();
  topics.forEach((row) => topicMap.set(row.topic_id, row));
  const teacherMap = new Map();
  teachers.forEach((row) => teacherMap.set(row.tea_id, row.tea_name));

  const mappedApplications = applications.map((row) => {
    const topic = topicMap.get(row.topic_Topic_id);
    const teacherName = topic ? (teacherMap.get(topic.teacher_Tea_id) || '') : '';
    return {
      ...row,
      teacher_id: topic?.teacher_Tea_id || '',
      topic_name: topic?.topic_name || '',
      teacher_name: teacherName ? `${teacherName}（${topic?.teacher_Tea_id || ''}）` : ''
    };
  });
  topicApplications.value = mappedApplications;

  const applicationIds = new Set(applications.map((row) => row.application_id));
  thesisProposals.value = proposals.filter((row) => applicationIds.has(row.topic_selection_application_id));

  const thesisIds = new Set(thesisProposals.value.map((row) => row.tp_id));
  midtermReports.value = midterms
    .filter((row) => thesisIds.has(row.thesis_proposal_Tp_id))
    .map((row) => ({
      ...row,
      mtr_submission_time: formatTime(row.mtr_submission_time)
    }));
  guidanceRecords.value = guidances
    .filter((row) => thesisIds.has(row.thesis_proposal_Tp_id))
    .map((row) => ({
      ...row,
      gr_time: formatTime(row.gr_time)
    }));
  papers.value = paperRows.filter((row) => thesisIds.has(row.thesis_proposal_Tp_id));

  const paperMap = new Map();
  papers.value.forEach((row) => paperMap.set(row.paper_id, row));
  defenseRecords.value = defenses
    .filter((row) => paperMap.has(row.paper_id))
    .map((row) => ({
      ...row,
      paper_title: paperMap.get(row.paper_id)?.paper_title || ''
    }));
};

const refreshAll = async () => {
  await loadStudents();
  await loadAllData();
  if (!filteredStudents.value.find((row) => row.stu_id === selectedStudent.value)) {
    selectedStudent.value = filteredStudents.value[0]?.stu_id || '';
  }
  await loadRecords();
};

onMounted(async () => {
  await refreshAll();
});

watch(selectedStudent, () => {
  loadRecords();
});

watch(filteredStudents, (list) => {
  if (!list.find((row) => row.stu_id === selectedStudent.value)) {
    selectedStudent.value = list[0]?.stu_id || '';
  }
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
select {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
  background: #ffffff;
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
