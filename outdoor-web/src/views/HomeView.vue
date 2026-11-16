<template>
  <div class="page">
    <section class="hero">
      <div class="hero-content">
        <span class="eyebrow">OUTDOOR · COMPANION</span>
        <h1>去山野，<br />遇见同频的人。</h1>
        <p>认证领队带你徒步、骑行、攀岩、露营。不必做攻略，也不必一个人出发，把周末交给山风与远方。</p>
        <div class="hero-search">
          <el-input v-model="keyword" size="large" placeholder="搜索目的地或活动，例如：莫干山" @keyup.enter="search" />
          <el-button type="primary" size="large" @click="search">找路线</el-button>
        </div>
        <div class="hero-stats">
          <div><strong>{{ activities.length ? '6+' : '—' }}</strong><span>精选路线</span></div>
          <div><strong>{{ guides.length ? '3+' : '—' }}</strong><span>认证领队</span></div>
          <div><strong>100%</strong><span>户外保险</span></div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="section-head">
        <div>
          <h2>本周出发</h2>
          <p>从城市边缘开始，走进山川湖海。</p>
        </div>
        <router-link to="/activities" class="more">查看全部 →</router-link>
      </div>
      <div v-if="activities.length" class="grid-3">
        <ActivityCard v-for="item in activities" :key="item.id" :activity="item" />
      </div>
      <div v-else class="empty-box"><div class="empty-icon">🏕️</div>正在准备新的山野路线…</div>
    </section>

    <section class="section">
      <div class="section-head">
        <div>
          <h2>和谁一起出发</h2>
          <p>每一位领队都经过实名认证与资质审核。</p>
        </div>
        <router-link to="/guides" class="more">认识领队 →</router-link>
      </div>
      <div class="grid-3">
        <GuideCard v-for="guide in guides" :key="guide.id" :guide="guide" />
      </div>
    </section>

    <section class="section">
      <div class="section-head">
        <div><h2>三步，走进山野</h2><p>轻量、透明、有保障的预约体验。</p></div>
      </div>
      <div class="grid-3">
        <div class="panel"><h2>01 · 选路线</h2><p class="muted">按类型、难度和目的地筛选，查看真实评价与领队履历。</p></div>
        <div class="panel"><h2>02 · 约领队</h2><p class="muted">选择日期与人数，在线确认订单，领队会第一时间收到通知。</p></div>
        <div class="panel"><h2>03 · 出发</h2><p class="muted">完成行程后确认订单并评价，让每一次出发都有回应。</p></div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ActivityCard from '../components/ActivityCard.vue'
import GuideCard from '../components/GuideCard.vue'
import { listActivities } from '../api/activity.js'
import { listGuides } from '../api/guide.js'

const router = useRouter()
const keyword = ref('')
const activities = ref([])
const guides = ref([])

function search() {
  router.push({ path: '/activities', query: keyword.value ? { keyword: keyword.value } : {} })
}

onMounted(async () => {
  try {
    const [activityRes, guideRes] = await Promise.all([
      listActivities({ page: 1, size: 6 }),
      listGuides()
    ])
    activities.value = activityRes.data?.records || []
    guides.value = (guideRes.data || []).slice(0, 3)
  } catch (e) {
    activities.value = []
    guides.value = []
  }
})
</script>
