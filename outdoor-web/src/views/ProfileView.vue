<template>
  <div class="page">
    <h1 class="page-title">个人资料</h1>
    <p class="page-sub">完善联系方式，方便领队与平台联系你。</p>
    <div class="grid-2">
      <section class="panel">
        <div style="display:flex;align-items:center;gap:16px;margin-bottom:22px;">
          <div class="guide-avatar" style="width:72px;height:72px;" :style="avatarStyle">{{ initial }}</div>
          <div>
            <h2 style="margin:0 0 6px;">{{ authState.user?.username }}</h2>
            <span class="status-pill">{{ roleText }}</span>
          </div>
        </div>
        <el-form label-position="top">
          <el-form-item label="手机号"><el-input v-model="profileForm.phone" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="profileForm.email" /></el-form-item>
          <el-form-item label="头像 URL"><el-input v-model="profileForm.avatar" placeholder="https://..." /></el-form-item>
          <el-button type="primary" :loading="saving" @click="saveProfile">保存资料</el-button>
        </el-form>
      </section>
      <section class="panel">
        <h2>修改密码</h2>
        <el-form label-position="top">
          <el-form-item label="原密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
          <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="至少 6 位" /></el-form-item>
          <el-form-item label="确认新密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password /></el-form-item>
          <el-button :loading="savingPassword" @click="savePassword">更新密码</el-button>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { changePassword, updateProfile } from '../api/user.js'
import { authState, updateUser } from '../stores/auth'

const saving = ref(false)
const savingPassword = ref(false)
const profileForm = reactive({
  phone: authState.user?.phone || '',
  email: authState.user?.email || '',
  avatar: authState.user?.avatar || ''
})
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const initial = computed(() => (authState.user?.username || '山').slice(0, 1).toUpperCase())
const avatarStyle = computed(() => authState.user?.avatar ? { backgroundImage: `url(${authState.user.avatar})` } : {})
const roleText = computed(() => authState.user?.role === 2 ? '平台管理员' : authState.user?.role === 1 ? '认证领队' : '户外爱好者')

async function saveProfile() {
  saving.value = true
  try {
    const res = await updateProfile(profileForm)
    updateUser(res.data)
    ElMessage.success('资料已保存')
  } catch (e) {
  } finally {
    saving.value = false
  }
}

async function savePassword() {
  if (!passwordForm.oldPassword || passwordForm.newPassword.length < 6) {
    ElMessage.warning('请填写原密码，新密码至少 6 位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  savingPassword.value = true
  try {
    await changePassword({ oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
    ElMessage.success('密码已更新')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (e) {
  } finally {
    savingPassword.value = false
  }
}
</script>
