<template>
  <div class="auth-page">
    <div class="auth-card">
      <aside class="auth-side">
        <div>
          <span class="eyebrow">WELCOME BACK</span>
          <h2>山野在等你，<br />下一次出发。</h2>
          <p>登录后查看预约、接收领队消息，也可以申请成为认证领队。</p>
        </div>
        <p>“山不来，我们就向山走去。”</p>
      </aside>
      <div class="auth-form">
        <h3>登录山野同行</h3>
        <el-form label-position="top" @submit.prevent="onLogin">
          <el-form-item label="用户名">
            <el-input v-model="form.username" size="large" placeholder="请输入用户名" @keyup.enter="onLogin" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" size="large" type="password" show-password
              placeholder="请输入密码" @keyup.enter="onLogin" />
          </el-form-item>
          <el-button type="primary" size="large" style="width:100%;" :loading="loading" @click="onLogin">登录</el-button>
        </el-form>
        <div class="quick-account">
          <span class="muted" style="font-size:12px;">演示账号：</span>
          <el-tag v-for="account in accounts" :key="account.username" effect="plain" @click="fill(account)">
            {{ account.label }}
          </el-tag>
        </div>
        <p class="muted" style="margin-top:22px;font-size:13px;">
          还没有账号？<router-link to="/register" style="color:var(--forest);font-weight:700;">立即注册</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user.js'
import { setAuth } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const form = reactive({ username: 'user_xia', password: '123456' })
const accounts = [
  { label: '普通用户', username: 'user_xia' },
  { label: '认证领队', username: 'guide_lin' },
  { label: '管理员', username: 'admin' }
]

function fill(account) {
  form.username = account.username
  form.password = '123456'
}

async function onLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form)
    setAuth(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/')
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>
