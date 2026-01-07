<template>
  <section class="login-page">
    <header class="top-bar">
      <h1>毕业设计辅助管理系统</h1>
    </header>
    <main class="content">
      <div class="login-card">
        <div class="card-head">
          <h2>用户登录</h2>
          <p>请选择身份并输入对应 ID</p>
        </div>
        <div class="form">
          <label>
            身份选择
            <select v-model="form.role">
              <option value="student">学生</option>
              <option value="teacher">教师</option>
              <option value="office">教科办</option>
              <option value="judge">评审组</option>
              <option value="counselor">辅导员</option>
            </select>
          </label>
          <label>
            用户 ID
            <input v-model="form.username" placeholder="请输入对应身份的 ID" />
          </label>
          <label>
            密码
            <input v-model="form.password" type="password" placeholder="统一为 123456" />
          </label>
          <button @click="handleLogin">登录</button>
          <p class="hint">默认密码：123456</p>
        </div>
        <div v-if="error" class="error">{{ error }}</div>
        <div v-if="result" class="result">
          已登录：{{ result.username }}（{{ roleLabel(result.role) }}）
        </div>
      </div>
    </main>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api';

const router = useRouter();
const form = ref({
  username: '',
  role: 'student',
  password: ''
});

const result = ref(null);
const error = ref('');

const handleLogin = async () => {
  error.value = '';
  result.value = null;
  if (!form.value.username.trim()) {
    error.value = '请输入用户 ID。';
    return;
  }
  if (form.value.password !== '123456') {
    error.value = '密码错误，请输入 123456。';
    return;
  }
  try {
    const { data } = await api.post('/auth/login', {
      username: form.value.username.trim(),
      role: form.value.role
    });
    if (!data.success) {
      error.value = data.message || '登录失败';
      return;
    }
    result.value = data.data;
    localStorage.setItem('gms_user', JSON.stringify(result.value));
  } catch (err) {
    error.value = '无法连接服务器，请确认后端已启动。';
    return;
  }
  if (result.value.role === 'student') {
    router.push('/topics');
  } else if (result.value.role === 'teacher') {
    router.push('/teacher/topic-declare');
  } else if (result.value.role === 'office') {
    router.push('/office/notice-publish');
  } else if (result.value.role === 'judge') {
    let judgeGroup = '';
    try {
      const { data: judgeRes } = await api.get('/judges');
      const found = (judgeRes.data || []).find((row) => row.jud_id === result.value.username);
      judgeGroup = found?.jud_group || '';
    } catch (err) {
      judgeGroup = '';
    }
    const judgeRouteMap = {
      '审题组': '/judge/topic-review',
      '开题评审组': '/judge/proposal-review',
      '中期检查小组': '/judge/midterm-check',
      '论文二审组': '/judge/paper-second-review',
      '毕设评委组': '/judge/score'
    };
    router.push(judgeRouteMap[judgeGroup] || '/judge/topic-review');
  } else if (result.value.role === 'counselor') {
    router.push('/counselor/guidance-review');
  }
};

const roleLabel = (role) => {
  switch (role) {
    case 'student':
      return '学生';
    case 'teacher':
      return '教师';
    case 'office':
      return '教科办';
    case 'judge':
      return '评审组';
    case 'counselor':
      return '辅导员';
    default:
      return role;
  }
};
</script>

<style scoped>
.login-page {
  --gms-blue: #016fa0;
  --gms-blue-light: #81d3f85d;
  --gms-blue-accent: #1e98d7;
  --gms-text: #1f2937;
  --gms-border: #b9c7d3;
  font-family: "Arial", sans-serif;
  color: var(--gms-text);
  background: linear-gradient(180deg, #e7f4fb 0%, #f7fbfe 70%, #ffffff 100%);
  min-height: 720px;
  border: 1px solid #d9e6ef;
  border-radius: 6px;
  overflow: hidden;
}
.top-bar {
  background: var(--gms-blue);
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.top-bar h1 {
  color: #ffffff;
  font-size: 32px;
  letter-spacing: 1px;
  margin: 0;
}
.content {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 32px 64px;
}
.login-card {
  width: min(420px, 100%);
  background: #ffffff;
  border: 1px solid var(--gms-border);
  border-radius: 8px;
  padding: 28px 28px 22px;
  box-shadow: 0 12px 28px rgba(2, 44, 68, 0.12);
}
.card-head h2 {
  margin: 0 0 6px;
  font-size: 24px;
}
.card-head p {
  margin: 0 0 16px;
  color: #4b5563;
  font-size: 13px;
}
.form {
  display: grid;
  gap: 12px;
}
label {
  display: grid;
  gap: 6px;
  font-size: 13px;
}
input, select {
  padding: 6px 8px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
}
button {
  padding: 8px 12px;
  border: none;
  background: var(--gms-blue-accent);
  color: #ffffff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  letter-spacing: 0.4px;
}
.hint {
  margin: 0;
  color: #6b7280;
  font-size: 12px;
}
.error {
  color: #dc2626;
}
.result {
  margin-top: 12px;
  color: #111827;
}
@media (max-width: 900px) {
  .top-bar {
    height: 120px;
  }
}
</style>
