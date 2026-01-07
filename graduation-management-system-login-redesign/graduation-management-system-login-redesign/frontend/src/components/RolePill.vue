<template>
  <div class="role-pill">{{ displayText }}</div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import api from '../api';

const props = defineProps({
  roleLabel: {
    type: String,
    required: true
  }
});

const extraInfo = ref('');

const loadExtraInfo = async () => {
  let stored;
  try {
    stored = JSON.parse(localStorage.getItem('gms_user'));
  } catch (err) {
    stored = null;
  }
  const role = stored?.role || '';
  const id = stored?.username || '';
  if (!role || !id) return;
  try {
    if (role === 'student') {
      const { data } = await api.get('/students');
      const found = (data.data || []).find((row) => row.stu_id === id);
      if (found) {
        extraInfo.value = [found.stu_college, found.stu_major].filter(Boolean).join(' / ');
      }
    } else if (role === 'teacher') {
      const { data } = await api.get('/teachers');
      const found = (data.data || []).find((row) => row.tea_id === id);
      if (found) {
        extraInfo.value = found.tea_institute || '';
      }
    } else if (role === 'judge') {
      const { data } = await api.get('/judges');
      const found = (data.data || []).find((row) => row.jud_id === id);
      if (found) {
        extraInfo.value = found.jud_group || '';
      }
    } else if (role === 'counselor') {
      const { data } = await api.get('/instructors');
      const found = (data.data || []).find((row) => row.ins_id === id);
      if (found) {
        extraInfo.value = found.ins_college || '';
      }
    }
  } catch (err) {
    extraInfo.value = '';
  }
};

const displayText = computed(() => {
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    const id = stored?.username || '';
    const name = stored?.name || '';
    if (id || name) {
      const suffix = [id, name].filter(Boolean).join(' / ');
      if (extraInfo.value) {
        return `当前身份：${props.roleLabel}（${suffix}）· ${extraInfo.value}`;
      }
      return `当前身份：${props.roleLabel}（${suffix}）`;
    }
  } catch (err) {
    // Ignore invalid storage and fall back to role only.
  }
  return `当前身份：${props.roleLabel}`;
});

onMounted(loadExtraInfo);
</script>

<style scoped>
.role-pill {
  background: #1e98d7;
  color: #ffffff;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
}
</style>
