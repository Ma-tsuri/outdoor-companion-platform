<template>
  <div class="page">
    <h1 class="page-title">认证领队</h1>
    <p class="page-sub">实名认证、资质审核、真实评价，和靠谱的人一起出发。</p>
    <div v-loading="loading" class="grid-3" style="min-height:220px;">
      <GuideCard v-for="guide in guides" :key="guide.id" :guide="guide" />
    </div>
    <div v-if="!loading && !guides.length" class="empty-box">
      <div class="empty-icon">🧗</div>领队正在入驻中，敬请期待。
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import GuideCard from '../components/GuideCard.vue'
import { listGuides } from '../api/guide.js'

const guides = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await listGuides()
    guides.value = res.data || []
  } catch (e) {
    guides.value = []
  } finally {
    loading.value = false
  }
})
</script>
