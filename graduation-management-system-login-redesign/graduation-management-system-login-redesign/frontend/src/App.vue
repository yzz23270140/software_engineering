<template>
  <div class="app">
    <div v-if="showSidebar" class="layout">
      <aside class="side-panel">
        <div class="side-title">系统模块</div>
        <nav class="side-actions">
          <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" class="side-action">
            {{ item.label }}
          </RouterLink>
        </nav>
      </aside>
      <main class="main">
        <RouterView />
      </main>
    </div>
    <RouterView v-else />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { RouterLink, RouterView, useRoute } from 'vue-router';
import api from './api';

const route = useRoute();
const userRole = ref('student');
const judgeGroup = ref('');

const showSidebar = computed(() => route.path !== '/login');

const navByRole = {
  student: [
    { to: '/topics', label: '课题选择' },
    { to: '/thesis-proposals', label: '开题报告提交' },
    { to: '/midterm-reports', label: '课题中期汇报' },
    { to: '/guidance-confirm', label: '中期指导确认' },
    { to: '/papers', label: '毕设论文上传' },
    { to: '/defense-records', label: '答辩与记录' },
    { to: '/notices', label: '通知公告' }
  ],
  teacher: [
    { to: '/teacher/topic-declare', label: '课题申报' },
    { to: '/teacher/selection-status', label: '选题申请审批' },
    { to: '/teacher/proposal-review', label: '开题报告查询' },
    { to: '/teacher/midterm-guidance', label: '中期指导' },
    { to: '/teacher/final-review', label: '论文初审' }
  ],
  office: [
    { to: '/office/notice-publish', label: '通知发布' },
    { to: '/office/create-student', label: '学生账号创建' },
    { to: '/office/create-teacher', label: '教师账号创建' },
    { to: '/office/create-judge', label: '评委账号创建' },
    { to: '/office/create-instructor', label: '辅导员账号创建' }
  ],
  judge: [
    { to: '/judge/topic-review', label: '课题审核' },
    { to: '/judge/proposal-review', label: '开题报告审核' },
    { to: '/judge/paper-second-review', label: '论文二审' },
    { to: '/judge/midterm-check', label: '中期检查' },
    { to: '/judge/score', label: '成绩评定' }
  ],
  counselor: [
    { to: '/counselor/guidance-review', label: '学生记录查看' },
    { to: '/counselor/student-filter', label: '学生筛选名单' }
  ]
};

const judgeNavByGroup = {
  '审题组': [{ to: '/judge/topic-review', label: '课题审核' }],
  '开题评审组': [{ to: '/judge/proposal-review', label: '开题报告审核' }],
  '中期检查小组': [{ to: '/judge/midterm-check', label: '中期检查' }],
  '论文二审组': [{ to: '/judge/paper-second-review', label: '论文二审' }],
  '毕设评委组': [{ to: '/judge/score', label: '成绩评定' }]
};

const navItems = computed(() => {
  if (userRole.value === 'judge' && judgeGroup.value) {
    return judgeNavByGroup[judgeGroup.value] || navByRole.judge;
  }
  return navByRole[userRole.value] || navByRole.student;
});

const loadJudgeGroup = async () => {
  if (userRole.value !== 'judge') {
    judgeGroup.value = '';
    return;
  }
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    const judgeId = stored?.username;
    if (!judgeId) {
      judgeGroup.value = '';
      return;
    }
    const { data } = await api.get('/judges');
    const found = (data.data || []).find((row) => row.jud_id === judgeId);
    judgeGroup.value = found?.jud_group || '';
  } catch (err) {
    judgeGroup.value = '';
  }
};

watch(
  () => route.fullPath,
  () => {
    try {
      const stored = JSON.parse(localStorage.getItem('gms_user'));
      userRole.value = stored?.role || 'student';
    } catch (err) {
      userRole.value = 'student';
    }
    loadJudgeGroup();
  },
  { immediate: true }
);
</script>

<style scoped>
.app {
  font-family: "Arial", sans-serif;
  min-height: 100vh;
  background: #f5f9fc;
}
.layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: 100vh;
}
.side-panel {
  background: #81d3f85d;
  padding: 28px 20px;
}
.side-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
}
.side-actions {
  display: grid;
  gap: 12px;
}
.side-action {
  background: #1e98d7;
  color: #ffffff;
  text-align: center;
  padding: 10px 12px;
  border-radius: 6px;
  font-size: 14px;
  letter-spacing: 0.2px;
  text-decoration: none;
}
.side-action.router-link-active {
  background: #016fa0;
}
.main {
  padding: 24px;
}
@media (max-width: 900px) {
  .layout {
    grid-template-columns: 1fr;
  }
  .side-panel {
    display: none;
  }
}
</style>
