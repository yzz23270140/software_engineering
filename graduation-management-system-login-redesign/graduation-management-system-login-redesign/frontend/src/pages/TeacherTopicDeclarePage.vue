<template>
  <section class="page">
    <header class="page-header">
      <div>
        <h2>课题申报</h2>
        <p>提交课题信息并发布给学生选择。</p>
      </div>
      <RolePill role-label="教师" />
    </header>

    <div class="panel">
      <div class="panel-head">
        <h3>课题信息填写</h3>
        <button class="ghost" @click="load">刷新列表</button>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>课题名称</span>
          <input v-model="form.topic_name" placeholder="填写课题名称" />
        </label>
        <label class="field">
          <span>课题描述</span>
          <input v-model="form.topic_description" placeholder="简要描述" />
        </label>
        <label class="field">
          <span>课题类型</span>
          <select v-model="form.topic_type">
            <option disabled value="">请选择课题类型</option>
            <option value="教师自拟">教师自拟</option>
            <option value="科研项目">科研项目</option>
          </select>
        </label>
        <label class="field">
          <span>所属专业</span>
          <select v-model="form.topic_major" multiple>
            <option value="计算机科学">计算机科学</option>
            <option value="软件工程">软件工程</option>
            <option value="人工智能">人工智能</option>
            <option value="计算机科学与技术">计算机科学与技术</option>
            <option value="EDA">EDA</option>
          </select>
        </label>
        <label class="field">
          <span>学年</span>
          <select v-model="form.academic_year">
            <option disabled value="">请选择学年</option>
            <option value="2024-2025">2024-2025</option>
            <option value="2025-2026">2025-2026</option>
          </select>
        </label>
        <button class="primary" @click="createTopic">提交申报</button>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>已申报课题</h3>
        <div class="actions">
          <button class="ghost" @click="toggleMine">
            {{ onlyMine ? '显示全部课题' : '仅显示本教师' }}
          </button>
          <button class="ghost" @click="load">刷新</button>
        </div>
      </div>
      <div class="form-grid">
        <label class="field">
          <span>课题类型</span>
          <select v-model="filters.type">
            <option value="">全部</option>
            <option value="教师自拟">教师自拟</option>
            <option value="科研项目">科研项目</option>
          </select>
        </label>
        <label class="field">
          <span>专业</span>
          <select v-model="filters.major">
            <option value="">全部</option>
            <option v-for="item in majorOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>
        <label class="field">
          <span>学年</span>
          <select v-model="filters.academicYear">
            <option value="">全部</option>
            <option v-for="item in yearOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>
        <label class="field">
          <span>教师</span>
          <select v-model="filters.teacherName">
            <option value="">全部</option>
            <option v-for="item in teacherOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课题名称</th>
            <th>描述</th>
            <th>类型</th>
            <th>专业</th>
            <th>学年</th>
            <th>教师</th>
            <th>状态</th>
            <th>发布时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in displayItems" :key="item.topic_id">
            <td>{{ item.topic_id }}</td>
            <td>{{ item.topic_name }}</td>
            <td>{{ item.topic_description }}</td>
            <td>{{ item.topic_type }}</td>
            <td>{{ formatMajor(item.topic_major) }}</td>
            <td>{{ item.academic_year }}</td>
            <td>{{ teacherNameMap[item.teacher_Tea_id] || '' }}</td>
            <td>{{ item.topic_status }}</td>
            <td>{{ item.created_at }}</td>
            <td>
              <button
                v-if="item.teacher_Tea_id === teacherId"
                class="ghost danger"
                @click="removeItem(item.topic_id)"
              >
                删除
              </button>
              <span v-else>-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import RolePill from '../components/RolePill.vue';
import api from '../api';

const items = ref([]);
const displayItems = ref([]);
const teacherNameMap = ref({});
const teacherId = ref('');
const onlyMine = ref(false);
const filters = ref({
  type: '',
  major: '',
  academicYear: '',
  teacherName: ''
});
const form = ref({
  topic_name: '',
  topic_description: '',
  topic_type: '',
  topic_major: [],
  academic_year: ''
});

const majorOptions = computed(() => {
  const set = new Set();
  items.value.forEach((item) => {
    const majors = parseMajor(item.topic_major);
    majors.forEach((m) => set.add(m));
  });
  return Array.from(set);
});

const yearOptions = computed(() => {
  const set = new Set();
  items.value.forEach((item) => {
    if (item.academic_year) set.add(item.academic_year);
  });
  return Array.from(set);
});

const teacherOptions = computed(() => {
  const set = new Set();
  Object.values(teacherNameMap.value || {}).forEach((name) => {
    if (name) set.add(name);
  });
  return Array.from(set);
});

const load = async () => {
  const [topicRes, teacherRes] = await Promise.all([
    api.get('/topics'),
    api.get('/teachers')
  ]);
  items.value = topicRes.data.data || [];
  const map = {};
  (teacherRes.data.data || []).forEach((item) => {
    map[item.tea_id] = item.tea_name;
  });
  teacherNameMap.value = map;
  applyFilters();
};

const createTopic = async () => {
  const currentTeacherId = teacherId.value;
  if (!form.value.topic_name || !form.value.topic_description || !form.value.topic_type) {
    alert('请完善课题名称、描述与类型。');
    return;
  }
  if (!form.value.topic_major || form.value.topic_major.length === 0) {
    alert('请选择所属专业。');
    return;
  }
  if (!form.value.academic_year) {
    alert('请填写学年。');
    return;
  }
  try {
    const { data } = await api.post('/topics', {
      ...form.value,
      teacher_Tea_id: currentTeacherId,
      topic_major: JSON.stringify(form.value.topic_major || []),
      topic_status: '审核中'
    });
    if (!data.success) {
      alert(data.message || '课题申报失败');
      return;
    }
    await load();
  } catch (err) {
    alert('课题申报失败，请检查教师ID是否有效。');
  }
};

const removeItem = async (id) => {
  if (!confirm('确认删除该课题吗？')) {
    return;
  }
  await api.delete(`/topics/${id}`);
  await load();
};

const formatMajor = (value) => {
  return parseMajor(value).join('、');
};

const parseMajor = (value) => {
  if (!value) return [];
  try {
    const parsed = JSON.parse(value);
    if (Array.isArray(parsed)) {
      return parsed;
    }
  } catch (err) {
    // ignore
  }
  return String(value).split(/[、,]/).map((item) => item.trim()).filter(Boolean);
};

const applyFilters = () => {
  displayItems.value = items.value.filter((item) => {
    if (onlyMine.value && teacherId.value && item.teacher_Tea_id !== teacherId.value) {
      return false;
    }
    if (filters.value.type && item.topic_type !== filters.value.type) {
      return false;
    }
    if (filters.value.major && !parseMajor(item.topic_major).includes(filters.value.major)) {
      return false;
    }
    if (filters.value.academicYear && item.academic_year !== filters.value.academicYear) {
      return false;
    }
    if (filters.value.teacherName) {
      const teacherName = teacherNameMap.value[item.teacher_Tea_id] || '';
      if (teacherName !== filters.value.teacherName) return false;
    }
    return true;
  });
};

const toggleMine = () => {
  onlyMine.value = !onlyMine.value;
};

const loadTeacherId = () => {
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    teacherId.value = stored?.username || '';
  } catch (err) {
    teacherId.value = '';
  }
};

watch([filters, onlyMine], () => {
  applyFilters();
}, { deep: true });

onMounted(() => {
  loadTeacherId();
  load();
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
.actions {
  display: flex;
  gap: 8px;
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
input {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
}
select {
  padding: 8px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 13px;
  background: #ffffff;
  min-height: 36px;
}
.danger {
  background: #fee2e2;
  border: 1px solid #fecaca;
  color: #991b1b;
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
  text-align: left;
}
.table th {
  background: #f1f7fb;
  color: #0f3a55;
}
</style>
