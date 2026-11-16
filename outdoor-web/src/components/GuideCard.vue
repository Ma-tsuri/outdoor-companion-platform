<template>
  <article class="guide-card">
    <div class="guide-avatar" :style="avatarStyle">{{ initial }}</div>
    <div class="guide-info">
      <h3>{{ guide.realName || guide.userName || '认证领队' }}</h3>
      <div class="guide-sub">📍 {{ guide.city || '全国' }} · 服务 {{ guide.activityCount || 0 }} 条路线</div>
      <p>{{ guide.intro || '热爱山野，也愿意把安全的户外体验分享给你。' }}</p>
      <div class="tag-row">
        <span v-for="tag in tags" :key="tag" class="tag">{{ tag }}</span>
      </div>
      <div class="activity-foot" style="margin-top:12px;">
        <div class="price">¥{{ formatMoney(guide.price) }}<small>/人起</small></div>
        <el-button size="small" type="primary" plain @click="$router.push('/activities?guideId=' + guide.id)">查看路线</el-button>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { formatMoney } from '../utils/format'

const props = defineProps({ guide: { type: Object, required: true } })
const initial = computed(() => (props.guide.realName || props.guide.userName || '领').slice(0, 1))
const tags = computed(() => (props.guide.tags || '').split(',').filter(Boolean).slice(0, 4))
const avatarStyle = computed(() => props.guide.avatar ? { backgroundImage: `url(${props.guide.avatar})` } : {})
</script>
