<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>毕设结果完善和汇报</h2>
        <p>提交最终完善材料与汇报结果。</p>
      </div>
      <RolePill role-label="学生" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>结果汇报</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>汇报类型</span>
          <select v-model="reviewType">
            <option value="first">一审意见</option>
            <option value="second">二审意见</option>
          </select>
        </label>
        <label class="field">
          <span>论文 ID</span>
          <input v-model.number="form.paper_id" placeholder="论文编号" />
        </label>
        <label class="field">
          <span>审核人 ID</span>
          <input v-model="form.reviewer_id" placeholder="审核人编号" />
        </label>
        <label class="field field-wide">
          <span>完善说明</span>
          <textarea v-model="form.opinion" placeholder="修改点与补充说明"></textarea>
        </label>
        <label class="field">
          <span>评分</span>
          <input v-model.number="form.score" placeholder="评分" />
        </label>
        <div class="field">
          <span>状态</span>
          <div class="readonly">待确认</div>
        </div>
        <button class="primary" @click="createItem">提交汇报</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>汇报记录</h3>
        <button class="ghost" @click="load">刷新</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>论文</th>
            <th>审核人</th>
            <th>评分</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.paper_id }}</td>
            <td>{{ item.reviewer_id }}</td>
            <td>{{ item.score }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const reviewType = ref('first');
const items = ref([]);
const form = ref({
  paper_id: null,
  reviewer_id: '',
  opinion: '',
  score: null,
  status: '待确认'
});

const endpoints = {
  first: '/first-opinions',
  second: '/second-opinions'
};

const load = async () => {
  const { data } = await api.get(endpoints[reviewType.value]);
  items.value = (data.data || []).map((row) => ({
    id: row.fio_id || row.sio_id,
    paper_id: row.paper_id,
    reviewer_id: row.reviewer_id,
    score: row.fio_score || row.sio_score
  }));
};

const createItem = async () => {
  const payload = reviewType.value === 'first'
    ? {
        paper_id: form.value.paper_id,
        reviewer_id: form.value.reviewer_id,
        fio_opinion: form.value.opinion,
        fio_score: form.value.score,
        fio_status: '待确认'
      }
    : {
        paper_id: form.value.paper_id,
        reviewer_id: form.value.reviewer_id,
        sio_opinion: form.value.opinion,
        sio_score: form.value.score,
        sio_status: '待确认'
      };

  await api.post(endpoints[reviewType.value], payload);
  await load();
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
