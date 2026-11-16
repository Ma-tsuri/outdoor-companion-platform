import { computed, reactive } from 'vue'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem('user') || 'null')
  } catch (e) {
    return null
  }
}

export const authState = reactive({
  token: localStorage.getItem('token') || '',
  user: readUser()
})

export const isLoggedIn = computed(() => Boolean(authState.token))
export const isAdmin = computed(() => authState.user?.role === 2)
export const isGuide = computed(() => authState.user?.role === 1)

export function setAuth(token, user) {
  authState.token = token || ''
  authState.user = user || null
  localStorage.setItem('token', authState.token)
  localStorage.setItem('user', JSON.stringify(authState.user))
}

export function updateUser(user) {
  authState.user = user
  localStorage.setItem('user', JSON.stringify(user))
}

export function logout() {
  authState.token = ''
  authState.user = null
  localStorage.removeItem('token')
  localStorage.removeItem('user')
}
