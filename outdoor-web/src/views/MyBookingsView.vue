<template>
  <div class="page">
    <h1 class="page-title">我的预约</h1>
    <p class="page-sub">管理你的出发计划，完成行程后别忘了留下评价。</p>

    <div class="filter-bar">
      <el-radio-group v-model="active" @change="() => {}">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="0">待支付</el-radio-button>
        <el-radio-button value="1">已支付</el-radio-button>
        <el-radio-button value="3">已完成</el-radio-button>
        <el-radio-button value="2">已取消</el-radio-button>
      </el-radio-group>
      <span class="spacer" />
      <span class="muted">共 {{ filtered.length }} 个订单</span>
    </div>

    <div v-loading="loading" style="min-height:200px;">
      <article v-for="item in filtered" :key="item.id" class="booking-card">
        <div class="booking-cover" :style="item.activityCover ? { backgroundImage: `url(${item.activityCover})` } : {}" />
        <div class="booking-main">
          <div style="display:flex;align-items:center;gap:10px;flex-wrap:wrap;">
            <span class="status-pill" :class="statusMeta(item.status).cls">{{ statusMeta(item.status).text }}</span>
            <span class="muted" style="font-size:12px;">订单号 #{{ item.id }}</span>
          </div>
          <h3>{{ item.activityTitle || '户外活动' }}</h3>
          <div class="activity-meta">
            <span>🧭 领队 {{ item.guideName || '待安排' }}</span>
            <span>📅 {{ formatDate(item.bookDate) }}</span>
            <span>👥 {{ item.num }} 人</span>
            <span>💰 ¥{{ formatMoney(item.totalPrice) }}</span>
          </div>
          <div class="booking-actions">
            <el-button v-if="item.status === 0" type="primary" size="small" @click="onPay(item)">去支付</el-button>
            <el-button v-if="item.status === 0 || item.status === 1" size="small" @click="onCancel(item)">取消预约</el-button>
            <el-button v-if="item.status === 3 && !item.reviewed" type="warning" size="small" plain @click="openReview(item)">去评价</el-button>
            <el-tag v-if="item.status === 3 && item.reviewed" type="success" effect="plain">已评价</el-tag>
            <el-button size="small" text @click="$router.push('/activity/' + item.activityId)">查看活动</el-button>
          </div>
        </div>
      </article>
    </div>
    <div v-if="!loading && !filtered.length" class="empty-box">
      <div class="empty-icon">🎒</div>还没有相关订单，去挑一条路线吧。
      <div style="margin-top:16px;"><el-button type="primary" @click="$router.push('/activities')">浏览活动</el-button></div>
    </div>

    <el-dialog v-model="reviewVisible" title="评价这次行程" width="460px">
      <el-rate v-model="reviewForm.rating" show-text />
      <el-input v-model="reviewForm.content" type="textarea" :rows="4" maxlength="300" show-word-limit
        placeholder="说说这次行程的体验，给后来的伙伴一些参考。" style="margin-top:18px;" />
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelBooking, listMyBookings, payBooking } from '../api/booking.js'
import { createReview } from '../api/review.js'
import { bookingStatusMap, formatDate, formatMoney } from '../utils/format'

const bookings = ref([])
const active = ref('all')
const loading = ref(true)
const reviewVisible = ref(false)
const reviewSubmitting = ref(false)
const currentBooking = ref(null)
const reviewForm = reactive({ rating: 5, content: '' })

const filtered = computed(() => active.value === 'all'
  ? bookings.value
  : bookings.value.filter(item => String(item.status) === active.value))

function statusMeta(status) {
  return bookingStatusMap[status] || { text: '未知', cls: '' }
}

async function load() {
  loading.value = true
  try {
    const res = await listMyBookings()
    bookings.value = res.data || []
  } catch (e) {
    bookings.value = []
  } finally {
    loading.value = false
  }
}

async function onPay(item) {
  try {
    await payBooking(item.id)
    ElMessage.success('支付成功')
    load()
  } catch (e) {}
}

async function onCancel(item) {
  try {
    await ElMessageBox.confirm('取消后名额将释放，确定取消这个预约吗？', '取消预约', {
      confirmButtonText: '确定取消', cancelButtonText: '再想想', type: 'warning'
    })
    await cancelBooking(item.id)
    ElMessage.success('已取消')
    load()
  } catch (e) {}
}

function openReview(item) {
  currentBooking.value = item
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewVisible.value = true
}

async function submitReview() {
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请写一点评价内容')
    return
  }
  reviewSubmitting.value = true
  try {
    await createReview({ bookingId: currentBooking.value.id, rating: reviewForm.rating, content: reviewForm.content })
    ElMessage.success('评价成功，感谢你的分享')
    reviewVisible.value = false
    load()
  } catch (e) {
  } finally {
    reviewSubmitting.value = false
  }
}

onMounted(load)
</script>
