<template>
  <div class="app-shell">
    <header class="site-header">
      <div class="header-inner">
        <router-link to="/" class="brand">
          <span class="brand-mark">▲</span><span>山野同行</span>
        </router-link>
        <nav class="main-nav">
          <router-link to="/">首页</router-link>
          <router-link to="/activities">户外活动</router-link>
          <router-link to="/guides">认证领队</router-link>
        </nav>
        <div class="header-actions">
          <template v-if="isLoggedIn">
            <router-link to="/messages" class="icon-button" title="消息">
              🔔<span v-if="unread > 0" class="badge-dot">{{ unread > 99 ? '99+' : unread }}</span>
            </router-link>
            <el-dropdown trigger="click" @command="onCommand">
              <span class="user-chip">
                <span class="avatar">{{ initial }}</span>{{ authState.user?.username }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                  <el-dropdown-item command="bookings">我的预约</el-dropdown-item>
                  <el-dropdown-item command="guide">领队中心</el-dropdown-item>
                  <el-dropdown-item v-if="isAdmin" command="admin" divided>管理后台</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button text @click="router.push('/login')">登录</el-button>
            <el-button type="primary" @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main><router-view /></main>
    <footer class="site-footer">
      <strong>山野同行</strong> · 户外运动伴游预约平台 · 毕业设计作品
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted, provide, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authState, isLoggedIn, isAdmin, logout } from '../stores/auth'
import { unreadCount } from '../api/message'

const route = useRoute()
const router = useRouter()
const unread = ref(0)

const initial = computed(() => (authState.user?.username || '山').slice(0, 1).toUpperCase())

async function loadUnread() {
  if (!isLoggedIn.value) {
    unread.value = 0
    return
  }
  try {
    const res = await unreadCount()
    unread.value = res.data || 0
  } catch (e) {
    unread.value = 0
  }
}

function onCommand(command) {
  if (command === 'logout') {
    logout()
    unread.value = 0
    ElMessage.success('已退出登录')
    router.push('/')
    return
  }
  const map = { profile: '/profile', bookings: '/bookings', guide: '/guide-center', admin: '/admin' }
  if (map[command]) router.push(map[command])
}

onMounted(loadUnread)
watch(() => route.fullPath, loadUnread)
provide('refreshUnread', loadUnread)
</script>
