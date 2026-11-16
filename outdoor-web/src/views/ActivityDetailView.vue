<template>
  <div v-loading="loading" class="page">
    <template v-if="activity">
      <section class="detail-hero" :style="heroStyle">
        <div>
          <span class="eyebrow">{{ activity.type || '户外活动' }}</span>
          <h1>{{ activity.title }}</h1>
          <div class="activity-meta" style="color:rgba(255,255,255,.82);">
            <span>📍 {{ activity.destination }}</span>
            <span>🥾 {{ difficultyText(activity.difficulty) }}</span>
            <span>📅 {{ formatDate(activity.startTime, true) }}</span>
            <span>👥 {{ activity.currentNum }}/{{ activity.maxNum }} 人</span>
          </div>
        </div>
      </section>

      <div class="detail-layout">
        <div>
          <section class="panel">
            <h2>路线介绍</h2>
            <div class="muted" style="line-height:1.9;" v-html="activity.content || '领队正在完善路线介绍。'" />
          </section>

          <section class="panel">
            <h2>行程信息</h2>
            <div class="info-grid">
              <div class="info-item"><span>集合地点</span><strong>{{ activity.gatherPoint || '待通知' }}</strong></div>
              <div class="info-item"><span>目的地</span><strong>{{ activity.destination }}</strong></div>
              <div class="info-item"><span>开始时间</span><strong>{{ formatDate(activity.startTime, true) }}</strong></div>
              <div class="info-item"><span>结束时间</span><strong>{{ formatDate(activity.endTime, true) }}</strong></div>
              <div class="info-item"><span>难度等级</span><strong>{{ difficultyText(activity.difficulty) }}</strong></div>
              <div class="info-item"><span>剩余名额</span><strong>{{ remaining }} 人</strong></div>
            </div>
          </section>

          <section class="panel">
            <h2>真实评价 <span class="muted" style="font-size:13px;font-weight:400;">{{ activity.avgRating ? activity.avgRating.toFixed(1) + ' 分' : '暂无评分' }}</span></h2>
            <div v-if="reviews.length">
              <div v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-head">
                  <strong>{{ review.userName || '户外伙伴' }}</strong>
                  <span>{{ formatDate(review.createTime) }} · {{ '★'.repeat(review.rating) }}</span>
                </div>
                <div>{{ review.content }}</div>
              </div>
            </div>
            <p v-else class="muted">还没有评价，完成行程后可以留下你的体验。</p>
          </section>
        </div>

        <aside>
          <section class="panel booking-panel">
            <div class="booking-price"><strong>¥{{ formatMoney(activity.price) }}</strong><span class="muted">/ 人</span></div>
            <el-form label-position="top">
              <el-form-item label="出发日期">
                <el-date-picker v-model="bookingDate" type="date" value-format="YYYY-MM-DD"
                  style="width:100%;" :disabled-date="disablePast" />
              </el-form-item>
              <el-form-item label="预约人数">
                <el-input-number v-model="num" :min="1" :max="remaining" style="width:100%;" />
              </el-form-item>
            </el-form>
            <div class="booking-row"><span>剩余名额</span><strong>{{ remaining }} 人</strong></div>
            <div class="booking-row"><span>合计</span><strong class="price">¥{{ formatMoney(totalPrice) }}</strong></div>
            <el-button type="primary" size="large" style="width:100%;margin-top:18px;" :disabled="remaining <= 0"
              :loading="submitting" @click="onBook">{{ remaining > 0 ? '立即预约' : '名额已满' }}</el-button>
            <p class="muted" style="font-size:12px;margin:12px 0 0;text-align:center;">包含领队服务与户外保险</p>
          </section>

          <section class="panel" v-if="guideInfo">
            <h2>你的领队</h2>
            <GuideCard :guide="guideInfo" />
          </section>
        </aside>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import GuideCard from '../components/GuideCard.vue'
import { getActivity } from '../api/activity.js'
import { listReviews } from '../api/review.js'
import { createBooking, payBooking } from '../api/booking.js'
import { authState } from '../stores/auth'
import { difficultyText, formatDate, formatMoney } from '../utils/format'

const route = useRoute()
const router = useRouter()
const activity = ref(null)
const reviews = ref([])
const bookingDate = ref('')
const num = ref(1)
const loading = ref(true)
const submitting = ref(false)

const remaining = computed(() => Math.max(0, Number(activity.value?.maxNum || 0) - Number(activity.value?.currentNum || 0)))
const totalPrice = computed(() => Number(activity.value?.price || 0) * num.value)
const heroStyle = computed(() => activity.value?.cover
  ? { backgroundImage: `linear-gradient(180deg, rgba(8,28,18,.12), rgba(8,28,18,.82)), url(${activity.value.cover})` }
  : {})
const guideInfo = computed(() => activity.value ? {
  id: activity.value.guideId,
  realName: activity.value.guideName,
  city: activity.value.guideCity,
  intro: activity.value.guideIntro,
  tags: activity.value.guideTags,
  price: activity.value.guidePrice,
  avatar: activity.value.guideAvatar,
  activityCount: activity.value.guideActivityCount
} : null)

function disablePast(date) {
  return date.getTime() < new Date().setHours(0, 0, 0, 0)
}

async function load() {
  loading.value = true
  try {
    const [detailRes, reviewRes] = await Promise.all([
      getActivity(route.params.id),
      listReviews({ activityId: route.params.id })
    ])
    activity.value = detailRes.data
    reviews.value = reviewRes.data || []
    bookingDate.value = detailRes.data?.startTime ? String(detailRes.data.startTime).slice(0, 10) : ''
  } catch (e) {
    activity.value = null
  } finally {
    loading.value = false
  }
}

async function onBook() {
  if (!authState.token) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!bookingDate.value) {
    ElMessage.warning('请选择出发日期')
    return
  }
  submitting.value = true
  try {
    const res = await createBooking({
      activityId: activity.value.id,
      bookDate: bookingDate.value,
      num: num.value
    })
    const bookingId = res.data.id
    try {
      await ElMessageBox.confirm(`订单已创建，合计 ¥${formatMoney(totalPrice.value)}，是否立即支付？`, '预约成功', {
        confirmButtonText: '立即支付',
        cancelButtonText: '稍后支付',
        type: 'success'
      })
      await payBooking(bookingId)
      ElMessage.success('支付成功，领队已收到通知')
    } catch (e) {
      ElMessage.info('订单已保存，可在“我的预约”中继续支付')
    }
    router.push('/bookings')
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>
