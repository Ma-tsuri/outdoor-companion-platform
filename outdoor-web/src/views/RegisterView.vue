<template>
  <div class="auth-page">
    <div class="auth-card">
      <aside class="auth-side">
        <div>
          <span class="eyebrow">JOIN THE TRAIL</span>
          <h2>从一次出发，<br />认识新的自己。</h2>
          <p>注册后即可预约活动。想成为领队？完善资料并通过审核后，就能发布自己的路线。</p>
        </div>
        <p>安全、专业、有温度的户外社区。</p>
      </aside>
      <div class="auth-form">
        <h3>创建账号</h3>
        <el-form label-position="top">
          <el-form-item label="用户名">
            <el-input v-model="form.username" size="large" placeholder="2-32 个字符" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" size="large" type="password" show-password placeholder="至少 6 位" />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="form.confirmPassword" size="large" type="password" show-password placeholder="再次输入密码" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" size="large" placeholder="用于接收订单通知" />
          </el-form-item>
          <el-form-item label="身份">
            <el-radio-group v-model="form.role">
              <el-radio :value="0">户外爱好者</el-radio>
              <el-radio :value="1">申请成为领队</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-button type="primary" size="large" style="width:100%;" :loading="loading" @click="onRegister">注册并登录</el-button>
        </el-form>
        <p class="muted" style="margin-top:20px;font-size:13px;">
          已有账号？<router-link to="/login" style="color:var(--forest);font-weight:700;">去登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/user.js'
import { setAuth } from '../stores/auth'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '', phone: '', role: 0 })

async function onRegister() {
  if (!form.username || form.username.length < 2) {
    ElMessage.warning('用户名至少 2 个字符')
    return
  }
  if (!form.password || form.password.length < 6) {
    ElMessage.warning('密码至少 6 位')
    return
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  loading.value = true
  try {
    const res = await register({ username: form.username, password: form.password, phone: form.phone, role: form.role })
    setAuth(res.data.token, res.data.user)
    ElMessage.success('注册成功')
    router.replace(form.role === 1 ? '/guide-center' : '/')
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>
