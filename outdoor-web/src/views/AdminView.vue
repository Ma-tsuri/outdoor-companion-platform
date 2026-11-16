<template>
  <div class="page" v-loading="loading">
    <h1 class="page-title">管理后台</h1>
    <p class="page-sub">审核领队、管理用户与活动，掌握平台运营数据。</p>

    <div class="stat-grid" v-if="stats">
      <div class="stat-card"><span>注册用户</span><strong>{{ stats.userCount }}</strong><em>全部账号</em></div>
      <div class="stat-card"><span>认证领队</span><strong>{{ stats.guideCount }}</strong><em>已通过审核</em></div>
      <div class="stat-card"><span>待审领队</span><strong>{{ stats.pendingGuideCount }}</strong><em>需要处理</em></div>
      <div class="stat-card"><span>上架活动</span><strong>{{ stats.activityCount }}</strong><em>可预约路线</em></div>
      <div class="stat-card"><span>订单总数</span><strong>{{ stats.bookingCount }}</strong><em>全部预约</em></div>
      <div class="stat-card"><span>待支付</span><strong>{{ stats.pendingBookingCount }}</strong><em>未完成支付</em></div>
      <div class="stat-card"><span>已完成</span><strong>{{ stats.completedBookingCount }}</strong><em>完成行程</em></div>
      <div class="stat-card"><span>累计交易额</span><strong>¥{{ formatMoney(stats.totalRevenue) }}</strong><em>已支付及完成</em></div>
    </div>

    <section class="panel analytics-panel" v-if="stats">
      <div class="section-head" style="margin-bottom:20px;">
        <div>
          <h2 style="margin:0;">运营分析</h2>
          <p class="page-sub" style="margin:4px 0 0;">订单状态与活动供给结构</p>
        </div>
      </div>
      <div class="analytics-grid">
        <div class="chart-block">
          <h3>订单状态分布</h3>
          <div v-if="bookingChart.length" class="bar-list">
            <div v-for="(item, index) in bookingChart" :key="item.name" class="bar-row">
              <div class="bar-label"><span>{{ item.name }}</span><strong>{{ item.value }}</strong></div>
              <div class="bar-track"><i :class="'tone-' + index" :style="{ width: barWidth(item.value, bookingChart) }" /></div>
            </div>
          </div>
          <p v-else class="muted">暂无订单数据</p>
        </div>
        <div class="chart-block">
          <h3>活动类型分布</h3>
          <div v-if="activityChart.length" class="bar-list">
            <div v-for="(item, index) in activityChart" :key="item.name" class="bar-row">
              <div class="bar-label"><span>{{ item.name }}</span><strong>{{ item.value }} 条</strong></div>
              <div class="bar-track"><i :class="'tone-' + (index + 4)" :style="{ width: barWidth(item.value, activityChart) }" /></div>
            </div>
          </div>
          <p v-else class="muted">暂无活动数据</p>
        </div>
      </div>
    </section>

    <el-tabs v-model="active" style="margin-top:26px;">
      <el-tab-pane label="概览" name="overview">
        <div class="grid-2">
          <section class="panel">
            <h2>待审核领队</h2>
            <el-table :data="guides.filter(item => item.status === 0).slice(0, 5)" size="small">
              <el-table-column prop="realName" label="姓名" />
              <el-table-column prop="city" label="城市" width="90" />
              <el-table-column label="操作" width="80">
                <template #default="{ row }">
                  <el-button link type="primary" @click="active = 'guides'">处理</el-button>
                </template>
              </el-table-column>
            </el-table>
            <p v-if="!guides.some(item => item.status === 0)" class="muted" style="margin:14px 0 0;">暂无待审核申请。</p>
          </section>
          <section class="panel">
            <h2>最近订单</h2>
            <el-table :data="bookings.slice(0, 5)" size="small">
              <el-table-column prop="activityTitle" label="活动" min-width="140" />
              <el-table-column prop="userName" label="用户" width="90" />
              <el-table-column label="金额" width="90">
                <template #default="{ row }">¥{{ formatMoney(row.totalPrice) }}</template>
              </el-table-column>
              <el-table-column label="状态" width="90">
                <template #default="{ row }">
                  <span class="status-pill" :class="statusMeta(row.status).cls">{{ statusMeta(row.status).text }}</span>
                </template>
              </el-table-column>
            </el-table>
          </section>
        </div>
      </el-tab-pane>

      <el-tab-pane label="领队审核" name="guides">
        <div class="filter-bar">
          <el-radio-group v-model="guideStatus" @change="loadGuides">
            <el-radio-button :value="0">待审核</el-radio-button>
            <el-radio-button :value="1">已通过</el-radio-button>
            <el-radio-button :value="2">已拒绝</el-radio-button>
          </el-radio-group>
        </div>
        <el-table :data="filteredGuides" style="width:100%;">
          <el-table-column prop="realName" label="姓名" width="110" />
          <el-table-column prop="userName" label="账号" width="120" />
          <el-table-column prop="city" label="城市" width="100" />
          <el-table-column prop="tags" label="擅长项目" min-width="140" />
          <el-table-column prop="intro" label="介绍" min-width="220" show-overflow-tooltip />
          <el-table-column label="价格" width="100">
            <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <span class="status-pill" :class="guideStatusMeta(row.status).cls">{{ guideStatusMeta(row.status).text }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <template v-if="row.status === 0">
                <el-button link type="success" @click="audit(row, 1)">通过</el-button>
                <el-button link type="danger" @click="audit(row, 2)">拒绝</el-button>
              </template>
              <span v-else class="muted" style="font-size:12px;">已处理</span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="用户管理" name="users">
        <div class="filter-bar">
          <el-input v-model="userKeyword" class="search" placeholder="搜索用户名或手机号" clearable @keyup.enter="searchUsers" />
          <el-button type="primary" @click="searchUsers">搜索</el-button>
        </div>
        <el-table :data="users" style="width:100%;">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="phone" label="手机号" width="140" />
          <el-table-column label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 2 ? 'danger' : row.role === 1 ? 'success' : 'info'" effect="plain">
                {{ row.role === 2 ? '管理员' : row.role === 1 ? '领队' : '用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-switch :model-value="row.status === 1" @change="value => toggleUser(row, value)" />
            </template>
          </el-table-column>
          <el-table-column label="注册时间" width="170">
            <template #default="{ row }">{{ formatDate(row.createTime, true) }}</template>
          </el-table-column>
        </el-table>
        <div class="pager">
          <el-pagination background layout="prev, pager, next" :total="userTotal" :page-size="10"
            v-model:current-page="userPage" @current-change="loadUsers" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="活动管理" name="activities">
        <div class="filter-bar">
          <el-radio-group v-model="activityStatus" @change="loadActivities">
            <el-radio-button :value="1">上架中</el-radio-button>
            <el-radio-button :value="0">已下架</el-radio-button>
            <el-radio-button value="">全部</el-radio-button>
          </el-radio-group>
        </div>
        <el-table :data="activities" style="width:100%;">
          <el-table-column prop="title" label="活动" min-width="180" />
          <el-table-column prop="guideName" label="领队" width="110" />
          <el-table-column prop="type" label="类型" width="80" />
          <el-table-column label="开始时间" width="170">
            <template #default="{ row }">{{ formatDate(row.startTime, true) }}</template>
          </el-table-column>
          <el-table-column label="价格" width="90">
            <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
          </el-table-column>
          <el-table-column label="名额" width="90">
            <template #default="{ row }">{{ row.currentNum }}/{{ row.maxNum }}</template>
          </el-table-column>
          <el-table-column label="上架" width="90">
            <template #default="{ row }">
              <el-switch :model-value="row.status === 1" @change="value => toggleActivity(row, value)" />
            </template>
          </el-table-column>
        </el-table>
        <div class="pager">
          <el-pagination background layout="prev, pager, next" :total="activityTotal" :page-size="10"
            v-model:current-page="activityPage" @current-change="loadActivities" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="订单管理" name="bookings">
        <div class="filter-bar">
          <el-radio-group v-model="bookingStatus" @change="loadBookings">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button :value="0">待支付</el-radio-button>
            <el-radio-button :value="1">已支付</el-radio-button>
            <el-radio-button :value="3">已完成</el-radio-button>
            <el-radio-button :value="2">已取消</el-radio-button>
          </el-radio-group>
        </div>
        <el-table :data="bookings" style="width:100%;">
          <el-table-column prop="id" label="订单号" width="90" />
          <el-table-column prop="activityTitle" label="活动" min-width="180" />
          <el-table-column prop="userName" label="用户" width="100" />
          <el-table-column prop="guideName" label="领队" width="100" />
          <el-table-column label="出发日期" width="120">
            <template #default="{ row }">{{ formatDate(row.bookDate) }}</template>
          </el-table-column>
          <el-table-column prop="num" label="人数" width="70" />
          <el-table-column label="金额" width="100">
            <template #default="{ row }">¥{{ formatMoney(row.totalPrice) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <span class="status-pill" :class="statusMeta(row.status).cls">{{ statusMeta(row.status).text }}</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="pager">
          <el-pagination background layout="prev, pager, next" :total="bookingTotal" :page-size="10"
            v-model:current-page="bookingPage" @current-change="loadBookings" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  auditGuide, getStats, listAllActivities, listAllBookings, listAllGuides,
  listUsers, setActivityStatus, setUserStatus
} from '../api/admin.js'
import { bookingStatusMap, formatDate, formatMoney, guideStatusMap } from '../utils/format'

const loading = ref(false)
const active = ref('overview')
const stats = ref(null)
const bookingChart = computed(() => stats.value?.bookingStatusDistribution || [])
const activityChart = computed(() => stats.value?.activityTypeDistribution || [])
const guides = ref([])
const guideStatus = ref(0)
const filteredGuides = computed(() => guides.value.filter(item => item.status === guideStatus.value))

const users = ref([])
const userTotal = ref(0)
const userPage = ref(1)
const userKeyword = ref('')

const activities = ref([])
const activityTotal = ref(0)
const activityPage = ref(1)
const activityStatus = ref(1)

const bookings = ref([])
const bookingTotal = ref(0)
const bookingPage = ref(1)
const bookingStatus = ref('')

function statusMeta(status) {
  return bookingStatusMap[status] || { text: '未知', cls: '' }
}

function guideStatusMeta(status) {
  return guideStatusMap[status] || { text: '未知', cls: '' }
}

function barWidth(value, items) {
  const max = Math.max(1, ...items.map(item => Number(item.value || 0)))
  const percent = Number(value || 0) / max * 100
  return percent === 0 ? '0%' : Math.max(8, percent) + '%'
}

async function loadStats() {
  try {
    const res = await getStats()
    stats.value = res.data
  } catch (e) {}
}

async function loadGuides() {
  try {
    const res = await listAllGuides()
    guides.value = res.data || []
  } catch (e) {
    guides.value = []
  }
}

async function loadUsers() {
  try {
    const res = await listUsers({ page: userPage.value, size: 10, keyword: userKeyword.value || undefined })
    users.value = res.data?.records || []
    userTotal.value = Number(res.data?.total || 0)
  } catch (e) {
    users.value = []
  }
}

async function loadActivities() {
  try {
    const res = await listAllActivities({
      page: activityPage.value,
      size: 10,
      status: activityStatus.value === '' ? undefined : activityStatus.value
    })
    activities.value = res.data?.records || []
    activityTotal.value = Number(res.data?.total || 0)
  } catch (e) {
    activities.value = []
  }
}

async function loadBookings() {
  try {
    const res = await listAllBookings({
      page: bookingPage.value,
      size: 10,
      status: bookingStatus.value === '' ? undefined : bookingStatus.value
    })
    bookings.value = res.data?.records || []
    bookingTotal.value = Number(res.data?.total || 0)
  } catch (e) {
    bookings.value = []
  }
}

function searchUsers() {
  userPage.value = 1
  loadUsers()
}

async function audit(row, status) {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定${action}“${row.realName || row.userName}”的领队申请吗？`, '审核确认', {
      confirmButtonText: action, cancelButtonText: '取消', type: status === 1 ? 'success' : 'warning'
    })
    await auditGuide(row.id, status)
    ElMessage.success(`已${action}`)
    await Promise.all([loadGuides(), loadStats()])
  } catch (e) {}
}

async function toggleUser(row, value) {
  try {
    await setUserStatus(row.id, value ? 1 : 0)
    row.status = value ? 1 : 0
    ElMessage.success(value ? '账号已启用' : '账号已禁用')
  } catch (e) {
    loadUsers()
  }
}

async function toggleActivity(row, value) {
  try {
    await setActivityStatus(row.id, value ? 1 : 0)
    row.status = value ? 1 : 0
    ElMessage.success(value ? '活动已上架' : '活动已下架')
    loadStats()
  } catch (e) {
    loadActivities()
  }
}

onMounted(async () => {
  loading.value = true
  await Promise.allSettled([loadStats(), loadGuides(), loadUsers(), loadActivities(), loadBookings()])
  loading.value = false
})
</script>
