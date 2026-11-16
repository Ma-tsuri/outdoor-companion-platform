<template>
  <div class="page">
    <h1 class="page-title">户外活动</h1>
    <p class="page-sub">按兴趣和目的地，找到下一次出发。</p>

    <div class="filter-bar">
      <el-input v-model="keyword" class="search" placeholder="搜索活动或目的地" clearable @keyup.enter="search" />
      <el-select v-model="type" placeholder="活动类型" clearable style="width:140px;">
        <el-option v-for="item in activityTypes" :key="item" :label="item" :value="item" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
      <el-button @click="reset">重置</el-button>
      <el-tag v-if="guideId" closable @close="clearGuide">只看该领队路线</el-tag>
      <span class="spacer" />
      <span class="muted">共 {{ total }} 条路线</span>
    </div>

    <div v-loading="loading" class="grid-3" style="min-height:220px;">
      <ActivityCard v-for="item in list" :key="item.id" :activity="item" />
    </div>
    <div v-if="!loading && !list.length" class="empty-box">
      <div class="empty-icon">🧭</div>没有找到符合条件的路线，换个关键词试试。
    </div>
    <div v-if="total > size" class="pager">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="size"
        v-model:current-page="page" @current-change="fetchList" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ActivityCard from '../components/ActivityCard.vue'
import { listActivities } from '../api/activity.js'
import { activityTypes } from '../utils/format'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const type = ref('')
const guideId = ref('')
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 9
const loading = ref(false)

async function fetchList() {
  loading.value = true
  try {
    const res = await listActivities({
      page: page.value,
      size,
      keyword: keyword.value || undefined,
      type: type.value || undefined,
      guideId: guideId.value || undefined
    })
    list.value = res.data?.records || []
    total.value = Number(res.data?.total || 0)
  } catch (e) {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  router.replace({ path: '/activities', query: {
    ...(keyword.value ? { keyword: keyword.value } : {}),
    ...(type.value ? { type: type.value } : {}),
    ...(guideId.value ? { guideId: guideId.value } : {})
  } })
  fetchList()
}

function reset() {
  keyword.value = ''
  type.value = ''
  guideId.value = ''
  search()
}

function clearGuide() {
  guideId.value = ''
  search()
}

watch(() => [route.query.keyword, route.query.type, route.query.guideId], () => {
  keyword.value = route.query.keyword || ''
  type.value = route.query.type || ''
  guideId.value = route.query.guideId || ''
  page.value = 1
  fetchList()
}, { immediate: true })
</script>
