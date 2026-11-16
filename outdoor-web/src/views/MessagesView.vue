<template>
  <div class="page">
    <div class="section-head">
      <div>
        <h1 class="page-title">消息中心</h1>
        <p class="page-sub">订单动态、审核结果与平台通知都在这里。</p>
      </div>
      <el-button :disabled="!unreadTotal" @click="readAll">全部标为已读</el-button>
    </div>

    <div v-loading="loading" style="min-height:200px;">
      <article v-for="item in messages" :key="item.id" class="panel" style="padding:18px;margin-bottom:14px;cursor:pointer;"
        :style="item.isRead ? '' : 'border-color:#b9d2ba;background:#fbfdf9;'" @click="markRead(item)">
        <div style="display:flex;align-items:center;gap:10px;margin-bottom:8px;">
          <span v-if="!item.isRead" style="width:8px;height:8px;border-radius:50%;background:#dfa13c;" />
          <strong style="font-size:14px;">{{ item.fromUserName || '系统通知' }}</strong>
          <span class="muted" style="font-size:12px;">{{ formatDate(item.createTime, true) }}</span>
        </div>
        <div style="color:#42534a;line-height:1.8;">{{ item.content }}</div>
      </article>
    </div>
    <div v-if="!loading && !messages.length" class="empty-box">
      <div class="empty-icon">🔔</div>暂时没有消息。
    </div>
  </div>
</template>

<script setup>
import { computed, inject, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listMessages, readAllMessages, readMessage } from '../api/message.js'
import { formatDate } from '../utils/format'

const messages = ref([])
const loading = ref(true)
const unreadTotal = computed(() => messages.value.filter(item => !item.isRead).length)
const refreshUnread = inject('refreshUnread', null)

async function load() {
  loading.value = true
  try {
    const res = await listMessages()
    messages.value = res.data || []
  } catch (e) {
    messages.value = []
  } finally {
    loading.value = false
  }
}

async function markRead(item) {
  if (item.isRead) return
  try {
    await readMessage(item.id)
    item.isRead = 1
    refreshUnread?.()
  } catch (e) {}
}

async function readAll() {
  try {
    await readAllMessages()
    messages.value.forEach(item => { item.isRead = 1 })
    refreshUnread?.()
    ElMessage.success('已全部标为已读')
  } catch (e) {}
}

onMounted(load)
</script>
