import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from './pages/LoginPage.vue';
import TopicPage from './pages/TopicPage.vue';
import TopicApplicationPage from './pages/TopicApplicationPage.vue';
import ThesisProposalPage from './pages/ThesisProposalPage.vue';
import ThesisProposalDetailPage from './pages/ThesisProposalDetailPage.vue';
import MidTermPage from './pages/MidTermPage.vue';
import MidTermDetailPage from './pages/MidTermDetailPage.vue';
import PaperPage from './pages/PaperPage.vue';
import DefensePage from './pages/DefensePage.vue';
import NoticePage from './pages/NoticePage.vue';
import DemoLoginPage from './pages/DemoLoginPage.vue';
import TeacherTopicDeclarePage from './pages/TeacherTopicDeclarePage.vue';
import TeacherProposalReviewPage from './pages/TeacherProposalReviewPage.vue';
import TeacherFinalReviewPage from './pages/TeacherFinalReviewPage.vue';
import TeacherSelectionStatusPage from './pages/TeacherSelectionStatusPage.vue';
import TeacherMidtermGuidancePage from './pages/TeacherMidtermGuidancePage.vue';
import OfficeNoticePublishPage from './pages/OfficeNoticePublishPage.vue';
import OfficeResultArchivePage from './pages/OfficeResultArchivePage.vue';
import OfficeTopicManagePage from './pages/OfficeTopicManagePage.vue';
import OfficePermissionPage from './pages/OfficePermissionPage.vue';
import OfficePermissionAdvancedPage from './pages/OfficePermissionAdvancedPage.vue';
import JudgeTopicProposalReviewPage from './pages/JudgeTopicProposalReviewPage.vue';
import JudgeProposalReviewPage from './pages/JudgeProposalReviewPage.vue';
import JudgePaperSecondReviewPage from './pages/JudgePaperSecondReviewPage.vue';
import JudgeMidtermCheckPage from './pages/JudgeMidtermCheckPage.vue';
import JudgeScorePage from './pages/JudgeScorePage.vue';
import CounselorGuidanceReviewPage from './pages/CounselorGuidanceReviewPage.vue';
import CounselorStudentFilterPage from './pages/CounselorStudentFilterPage.vue';
import StudentGuidanceConfirmPage from './pages/StudentGuidanceConfirmPage.vue';
import OfficeCreateStudentPage from './pages/OfficeCreateStudentPage.vue';
import OfficeCreateTeacherPage from './pages/OfficeCreateTeacherPage.vue';
import OfficeCreateJudgePage from './pages/OfficeCreateJudgePage.vue';
import OfficeCreateInstructorPage from './pages/OfficeCreateInstructorPage.vue';

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  { path: '/demo-login', component: DemoLoginPage },
  { path: '/topics', component: TopicPage },
  { path: '/topic-applications', component: TopicApplicationPage },
  { path: '/thesis-proposals', component: ThesisProposalPage },
  { path: '/thesis-proposals/:id', component: ThesisProposalDetailPage },
  { path: '/midterm-reports', component: MidTermPage },
  { path: '/midterm-reports/:id', component: MidTermDetailPage },
  { path: '/guidance-confirm', component: StudentGuidanceConfirmPage },
  { path: '/papers', component: PaperPage },
  { path: '/defense-records', component: DefensePage },
  { path: '/notices', component: NoticePage },
  { path: '/teacher/topic-declare', component: TeacherTopicDeclarePage },
  { path: '/teacher/proposal-review', component: TeacherProposalReviewPage },
  { path: '/teacher/final-review', component: TeacherFinalReviewPage },
  { path: '/teacher/selection-status', component: TeacherSelectionStatusPage },
  { path: '/teacher/midterm-guidance', component: TeacherMidtermGuidancePage },
  { path: '/office/notice-publish', component: OfficeNoticePublishPage },
  { path: '/office/result-archive', component: OfficeResultArchivePage },
  { path: '/office/topic-manage', component: OfficeTopicManagePage },
  { path: '/office/permissions', component: OfficePermissionPage },
  { path: '/office/permissions-advanced', component: OfficePermissionAdvancedPage },
  { path: '/office/create-student', component: OfficeCreateStudentPage },
  { path: '/office/create-teacher', component: OfficeCreateTeacherPage },
  { path: '/office/create-judge', component: OfficeCreateJudgePage },
  { path: '/office/create-instructor', component: OfficeCreateInstructorPage },
  { path: '/judge/topic-review', component: JudgeTopicProposalReviewPage },
  { path: '/judge/proposal-review', component: JudgeProposalReviewPage },
  { path: '/judge/paper-second-review', component: JudgePaperSecondReviewPage },
  { path: '/judge/midterm-check', component: JudgeMidtermCheckPage },
  { path: '/judge/score', component: JudgeScorePage },
  { path: '/counselor/guidance-review', component: CounselorGuidanceReviewPage },
  { path: '/counselor/student-filter', component: CounselorStudentFilterPage }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
