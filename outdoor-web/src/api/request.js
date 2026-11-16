import axios from 'axios'
import { ElMessage } from 'element-plus'
import { logout } from '../stores/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

request.interceptors.response.use(
  res => {
    const data = res.data
    if (data && data.code !== 200) {
      ElMessage.error(data.msg || '请求失败')
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
    return data
  },
  err => {
    if (err.response?.status === 401) {
      logout()
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`
      }
      return Promise.reject(err)
    }
    const msg = err.response?.data?.msg || '网络请求失败'
    ElMessage.error(msg)
    return Promise.reject(err)
  }
)

export default request
