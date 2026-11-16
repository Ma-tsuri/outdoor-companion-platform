<template>
  <article class="activity-card" @click="$router.push('/activity/' + activity.id)">
    <div class="activity-cover" :style="coverStyle">
      <span class="cover-type">{{ activity.type || '户外' }}</span>
    </div>
    <div class="activity-body">
      <h3>{{ activity.title }}</h3>
      <div class="activity-meta">
        <span>📍 {{ activity.destination || '待定' }}</span>
        <span>🥾 {{ difficultyText(activity.difficulty) }}</span>
        <span>📅 {{ formatDate(activity.startTime) }}</span>
      </div>
      <div class="activity-foot">
        <div class="guide-line">领队 · {{ activity.guideName || '山野同行' }}</div>
        <div class="price">¥{{ formatMoney(activity.price) }}<small>/人</small></div>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { difficultyText, formatDate, formatMoney } from '../utils/format'

const props = defineProps({ activity: { type: Object, required: true } })
const coverStyle = computed(() => props.activity.cover
  ? { backgroundImage: `url(${props.activity.cover})` }
  : {})
</script>
