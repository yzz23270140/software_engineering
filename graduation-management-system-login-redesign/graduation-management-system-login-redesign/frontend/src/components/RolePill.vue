<template>
  <div class="role-pill">{{ displayText }}</div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  roleLabel: {
    type: String,
    required: true
  }
});

const displayText = computed(() => {
  try {
    const stored = JSON.parse(localStorage.getItem('gms_user'));
    const id = stored?.username || '';
    const name = stored?.name || '';
    if (id || name) {
      const suffix = [id, name].filter(Boolean).join(' / ');
      return `当前身份：${props.roleLabel}（${suffix}）`;
    }
  } catch (err) {
    // Ignore invalid storage and fall back to role only.
  }
  return `当前身份：${props.roleLabel}`;
});
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
