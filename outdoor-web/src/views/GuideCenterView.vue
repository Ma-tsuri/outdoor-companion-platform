<template>
  <div class="page" v-loading="loading">
    <h1 class="page-title">领队中心</h1>
    <p class="page-sub">发布路线、管理预约，把热爱变成专业的户外服务。</p>

    <template v-if="!guide || guide.status === 2">
      <el-alert v-if="guide && guide.status === 2" type="warning" :closable="false" show-icon
        title="上次申请未通过，请完善资料后重新提交。" style="margin-bottom:20px;" />
      <div class="grid-2">
        <section class="panel">
          <h2>成为认证领队</h2>
          <p class="muted">提交实名信息与户外履历，通过管理员审核后，即可发布活动并接收预约订单。</p>
          <div class="info-grid" style="margin-top:18px;">
            <div class="info-item"><span>实名认证</span><strong>保障双方安全</strong></div>
            <div class="info-item"><span>资质审核</span><strong>专业履历展示</strong></div>
            <div class="info-item"><span>订单管理</span><strong>实时接收通知</strong></div>
            <div class="info-item"><span>收益透明</span><strong>自主定价</strong></div>
          </div>
        </section>
        <section class="panel">
          <h2>{{ guide ? '重新提交申请' : '提交领队申请' }}</h2>
          <el-form label-position="top">
            <el-form-item label="真实姓名"><el-input v-model="guideForm.realName" /></el-form-item>
            <el-form-item label="身份证号"><el-input v-model="guideForm.idCard" /></el-form-item>
            <el-form-item label="所在城市"><el-input v-model="guideForm.city" placeholder="例如：杭州" /></el-form-item>
            <el-form-item label="服务价格（元/人）"><el-input-number v-model="guideForm.price" :min="0" style="width:100%;" /></el-form-item>
            <el-form-item label="擅长项目"><el-input v-model="guideForm.tags" placeholder="徒步,露营,登山" /></el-form-item>
            <el-form-item label="个人介绍"><el-input v-model="guideForm.intro" type="textarea" :rows="3" /></el-form-item>
            <el-button type="primary" size="large" style="width:100%;" :loading="submitting" @click="submitApply">提交审核</el-button>
          </el-form>
        </section>
      </div>
    </template>

    <template v-else-if="guide.status === 0">
      <section class="panel" style="max-width:680px;margin:40px auto;text-align:center;">
        <div style="font-size:48px;">⏳</div>
        <h2>申请正在审核中</h2>
        <p class="muted">管理员会尽快查看你的资料。审核通过后即可发布活动，结果会通过站内消息通知你。</p>
        <div class="info-grid" style="text-align:left;margin-top:20px;">
          <div class="info-item"><span>姓名</span><strong>{{ guide.realName }}</strong></div>
          <div class="info-item"><span>城市</span><strong>{{ guide.city }}</strong></div>
          <div class="info-item"><span>擅长项目</span><strong>{{ guide.tags || '—' }}</strong></div>
          <div class="info-item"><span>价格</span><strong>¥{{ formatMoney(guide.price) }}/人</strong></div>
        </div>
        <el-button style="margin-top:20px;" @click="loadGuide">刷新审核状态</el-button>
      </section>
    </template>

    <template v-else>
      <div class="stat-grid" style="grid-template-columns:repeat(3,minmax(0,1fr));">
        <div class="stat-card"><span>我的路线</span><strong>{{ activities.length }}</strong><em>条已发布活动</em></div>
        <div class="stat-card"><span>待服务订单</span><strong>{{ paidOrders }}</strong><em>已支付待完成</em></div>
        <div class="stat-card"><span>累计订单</span><strong>{{ orders.length }}</strong><em>全部预约记录</em></div>
      </div>
    </template>

    <template v-if="guide && guide.status === 1">
      <section class="panel" style="margin-top:20px;">
        <div class="table-toolbar">
          <h2 style="margin:0;">我的活动</h2>
          <span class="spacer" />
          <el-button type="primary" @click="openCreate">发布新活动</el-button>
        </div>
        <el-table :data="activities" style="width:100%;">
          <el-table-column prop="title" label="活动" min-width="180" />
          <el-table-column prop="type" label="类型" width="90" />
          <el-table-column label="开始时间" width="170">
            <template #default="{ row }">{{ formatDate(row.startTime, true) }}</template>
          </el-table-column>
          <el-table-column label="价格" width="100">
            <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
          </el-table-column>
          <el-table-column label="名额" width="100">
            <template #default="{ row }">{{ row.currentNum }}/{{ row.maxNum }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <span class="status-pill" :class="row.status === 1 ? '' : 'danger'">{{ row.status === 1 ? '上架' : '下架' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="removeActivity(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!activities.length" class="empty-box" style="margin-top:16px;">
          <div class="empty-icon">🗺️</div>还没有发布活动，点击右上角开始第一条路线。
        </div>
      </section>

      <section class="panel">
        <h2>预约订单</h2>
        <el-table :data="orders" style="width:100%;">
          <el-table-column prop="id" label="订单号" width="90" />
          <el-table-column prop="activityTitle" label="活动" min-width="170" />
          <el-table-column prop="userName" label="预约人" width="110" />
          <el-table-column label="出发日期" width="120">
            <template #default="{ row }">{{ formatDate(row.bookDate) }}</template>
          </el-table-column>
          <el-table-column prop="num" label="人数" width="80" />
          <el-table-column label="金额" width="100">
            <template #default="{ row }">¥{{ formatMoney(row.totalPrice) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <span class="status-pill" :class="statusMeta(row.status).cls">{{ statusMeta(row.status).text }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 1" link type="primary" @click="confirmComplete(row)">确认完成</el-button>
              <span v-else class="muted" style="font-size:12px;">—</span>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!orders.length" class="empty-box" style="margin-top:16px;">
          <div class="empty-icon">📭</div>暂时还没有预约订单。
        </div>
      </section>

      <section class="panel" style="max-width:760px;">
        <h2>领队资料</h2>
        <el-form label-position="top">
          <div class="grid-2">
            <el-form-item label="真实姓名"><el-input v-model="guideForm.realName" /></el-form-item>
            <el-form-item label="所在城市"><el-input v-model="guideForm.city" /></el-form-item>
          </div>
          <div class="grid-2">
            <el-form-item label="服务价格（元/人）"><el-input-number v-model="guideForm.price" :min="0" style="width:100%;" /></el-form-item>
            <el-form-item label="擅长项目"><el-input v-model="guideForm.tags" /></el-form-item>
          </div>
          <el-form-item label="个人介绍"><el-input v-model="guideForm.intro" type="textarea" :rows="3" /></el-form-item>
          <el-button type="primary" :loading="submitting" @click="saveProfile">保存资料</el-button>
        </el-form>
      </section>
    </template>

    <el-dialog v-model="dialogVisible" :title="activityForm.id ? '编辑活动' : '发布活动'" width="720px">
      <el-form label-position="top">
        <el-form-item label="活动标题"><el-input v-model="activityForm.title" maxlength="60" /></el-form-item>
        <div class="grid-2">
          <el-form-item label="活动类型">
            <el-select v-model="activityForm.type" style="width:100%;">
              <el-option v-for="item in activityTypes" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="难度（1-5）">
            <el-select v-model="activityForm.difficulty" style="width:100%;">
              <el-option v-for="n in 5" :key="n" :label="n + ' · ' + difficultyText(n)" :value="n" />
            </el-select>
          </el-form-item>
        </div>
        <div class="grid-2">
          <el-form-item label="目的地"><el-input v-model="activityForm.destination" /></el-form-item>
          <el-form-item label="集合地点"><el-input v-model="activityForm.gatherPoint" /></el-form-item>
        </div>
        <div class="grid-2">
          <el-form-item label="开始时间">
            <el-date-picker v-model="activityForm.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%;" />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker v-model="activityForm.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%;" />
          </el-form-item>
        </div>
        <div class="grid-2">
          <el-form-item label="价格（元/人）"><el-input-number v-model="activityForm.price" :min="0" style="width:100%;" /></el-form-item>
          <el-form-item label="人数上限"><el-input-number v-model="activityForm.maxNum" :min="1" style="width:100%;" /></el-form-item>
        </div>
        <el-form-item label="封面图片 URL"><el-input v-model="activityForm.cover" placeholder="https://..." /></el-form-item>
        <el-form-item label="路线介绍">
          <el-input v-model="activityForm.content" type="textarea" :rows="5" placeholder="路线亮点、费用说明、装备建议等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="saveActivity">保存活动</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { applyGuide, myGuide, updateGuide } from '../api/guide.js'
import { createActivity, deleteActivity, myActivities, updateActivity } from '../api/activity.js'
import { completeBooking, listGuideBookings } from '../api/booking.js'
import { activityTypes, bookingStatusMap, difficultyText, formatDate, formatMoney } from '../utils/format'

const guide = ref(null)
const activities = ref([])
const orders = ref([])
const loading = ref(true)
const submitting = ref(false)
const dialogVisible = ref(false)
const guideForm = reactive({ realName: '', idCard: '', city: '', price: 200, tags: '', intro: '' })

function emptyActivity() {
  return {
    id: null, title: '', type: '徒步', difficulty: 1, destination: '', gatherPoint: '',
    startTime: '', endTime: '', price: 200, maxNum: 10, cover: '', content: ''
  }
}

const activityForm = reactive(emptyActivity())
const paidOrders = computed(() => orders.value.filter(item => item.status === 1).length)

function statusMeta(status) {
  return bookingStatusMap[status] || { text: '未知', cls: '' }
}

async function loadGuide() {
  loading.value = true
  try {
    const res = await myGuide()
    guide.value = res.data
    if (guide.value) {
      Object.assign(guideForm, {
        realName: guide.value.realName || '',
        idCard: guide.value.idCard || '',
        city: guide.value.city || '',
        price: Number(guide.value.price || 0),
        tags: guide.value.tags || '',
        intro: guide.value.intro || ''
      })
      if (guide.value.status === 1) await loadRelated()
    }
  } catch (e) {
    guide.value = null
  } finally {
    loading.value = false
  }
}

async function loadRelated() {
  try {
    const [activityRes, orderRes] = await Promise.all([myActivities(), listGuideBookings()])
    activities.value = activityRes.data || []
    orders.value = orderRes.data || []
  } catch (e) {
    activities.value = []
    orders.value = []
  }
}

function openCreate() {
  Object.assign(activityForm, emptyActivity())
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(activityForm, {
    ...row,
    price: Number(row.price || 0),
    maxNum: Number(row.maxNum || 1),
    startTime: row.startTime ? String(row.startTime).slice(0, 19) : '',
    endTime: row.endTime ? String(row.endTime).slice(0, 19) : ''
  })
  dialogVisible.value = true
}

async function submitApply() {
  if (!guideForm.realName || !guideForm.idCard || !guideForm.city) {
    ElMessage.warning('请填写姓名、身份证号和城市')
    return
  }
  submitting.value = true
  try {
    await applyGuide(guideForm)
    ElMessage.success('申请已提交，请等待审核')
    await loadGuide()
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

async function saveProfile() {
  if (!guideForm.realName) {
    ElMessage.warning('请填写真实姓名')
    return
  }
  submitting.value = true
  try {
    await updateGuide(guideForm)
    ElMessage.success('资料已更新')
    await loadGuide()
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

async function saveActivity() {
  if (!activityForm.title || !activityForm.destination || !activityForm.startTime || !activityForm.endTime) {
    ElMessage.warning('请填写标题、目的地和起止时间')
    return
  }
  submitting.value = true
  try {
    const payload = { ...activityForm }
    if (payload.id) {
      await updateActivity(payload)
    } else {
      delete payload.id
      await createActivity(payload)
    }
    ElMessage.success(activityForm.id ? '活动已更新' : '活动已发布')
    dialogVisible.value = false
    await loadRelated()
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

async function removeActivity(row) {
  try {
    await ElMessageBox.confirm(`确定删除“${row.title}”吗？`, '删除活动', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    })
    await deleteActivity(row.id)
    ElMessage.success('已删除')
    await loadRelated()
  } catch (e) {}
}

async function confirmComplete(row) {
  try {
    await ElMessageBox.confirm('确认本次行程已完成？确认后用户可以进行评价。', '确认完成', {
      confirmButtonText: '确认完成', cancelButtonText: '取消', type: 'success'
    })
    await completeBooking(row.id)
    ElMessage.success('订单已完成')
    await loadRelated()
  } catch (e) {}
}

onMounted(loadGuide)
</script>
