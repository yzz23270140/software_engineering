<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>学生筛选名单</h2>
        <p>按状态筛选当前辅导员所负责的学生。</p>
      </div>
      <RolePill role-label="辅导员" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>筛选条件</h3>
        <button class="ghost" @click="refreshAll">刷新</button>
      </div>
      <div class="form-grid">
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
        <h3>筛选学生名单</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>学生ID</th>
            <th>姓名</th>
            <th>学院</th>
            <th>专业</th>
            <th>年级</th>
            <th>指导教师</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredStudents" :key="item.stu_id">
            <td>{{ item.stu_id }}</td>
            <td>{{ item.stu_name }}</td>
            <td>{{ item.stu_college }}</td>
            <td>{{ item.stu_major }}</td>
            <td>{{ item.enrollment_year }}</td>
            <td>{{ formatTeacher(item.teacher_Tea_id) }}</td>
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

const students = ref([]);
const teachers = ref([]);
const allApplications = ref([]);
const allProposals = ref([]);
const allMidterms = ref([]);
const allGuidances = ref([]);
const allPapers = ref([]);
const allDefenses = ref([]);
const allTopics = ref([]);

const filters = ref({
  teacherId: '',
  topicStatus: '',
  thesisStatus: '',
  midtermStatus: '',
  guidanceStatus: '',
  paperStatus: '',
  defenseResult: ''
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
  teachers.value = teacherRes.data.data || [];
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

const formatTeacher = (teacherId) => {
  if (!teacherId) return '无';
  const found = teachers.value.find((row) => row.tea_id === teacherId);
  return found ? `${found.tea_name}（${found.tea_id}）` : teacherId;
};

const refreshAll = async () => {
  await loadStudents();
  await loadAllData();
};

onMounted(refreshAll);
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
  vertical-align: top;
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
