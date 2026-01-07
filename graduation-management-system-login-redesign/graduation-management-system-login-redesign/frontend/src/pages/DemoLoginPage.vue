<template>
  <section class="demo-page">
    <div class="card">
      <h2>演示登录</h2>
      <p v-if="status">{{ status }}</p>
      <p v-else>正在登录...</p>
      <button class="ghost" @click="goLogin">返回登录页</button>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';

const route = useRoute();
const router = useRouter();
const status = ref('');

const roleRouteMap = {
  student: '/topics',
  teacher: '/teacher/topic-declare',
  office: '/office/notice-publish',
  counselor: '/counselor/guidance-review'
};

const goLogin = () => {
  router.push('/login');
};

const resolveJudgeRoute = async (username) => {
  try {
    const { data } = await api.get('/judges');
    const found = (data.data || []).find((row) => row.jud_id === username);
    const group = found?.jud_group || '';
    const map = {
      '审题组': '/judge/topic-review',
      '开题评审组': '/judge/proposal-review',
      '中期检查小组': '/judge/midterm-check',
      '论文二审组': '/judge/paper-second-review',
      '毕设评委组': '/judge/score'
    };
    return map[group] || '/judge/topic-review';
  } catch (err) {
    return '/judge/topic-review';
  }
};

const autoLogin = async () => {
  const role = String(route.query.role || '').trim();
  const username = String(route.query.id || '').trim();
  if (!role || !username) {
    status.value = '缺少 role 或 id 参数。';
    return;
  }
  try {
    const { data } = await api.post('/auth/login', { role, username });
    if (!data.success) {
      status.value = data.message || '登录失败';
      return;
    }
    localStorage.setItem('gms_user', JSON.stringify(data.data));
    if (role === 'judge') {
      const target = await resolveJudgeRoute(username);
      router.replace(target);
      return;
    }
    router.replace(roleRouteMap[role] || '/login');
  } catch (err) {
    status.value = '无法连接服务器，请确认后端已启动。';
  }
};

onMounted(autoLogin);
</script>

<style scoped>
.demo-page {
  min-height: 60vh;
  display: grid;
  place-items: center;
}
.card {
  background: #ffffff;
  border: 1px solid #c9d6e1;
  border-radius: 8px;
  padding: 24px;
  width: min(360px, 100%);
  box-shadow: 0 12px 24px rgba(15, 55, 78, 0.12);
  text-align: center;
}
.card h2 {
  margin: 0 0 8px;
}
.card p {
  margin: 0 0 16px;
  color: #475569;
  font-size: 13px;
}
.ghost {
  background: #eff6fb;
  border: 1px solid #cfe1ee;
  color: #0f3a55;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}
</style>
