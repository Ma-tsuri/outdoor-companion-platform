import { createRouter, createWebHistory } from 'vue-router'
import { authState } from '../stores/auth'
import HomeView from '../views/HomeView.vue'

const routes = [
  { path: '/', name: 'Home', component: HomeView },
  { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/RegisterView.vue') },
  { path: '/activities', name: 'ActivityList', component: () => import('../views/ActivityListView.vue') },
  { path: '/activity/:id', name: 'ActivityDetail', component: () => import('../views/ActivityDetailView.vue') },
  { path: '/guides', name: 'Guides', component: () => import('../views/GuidesView.vue') },
  {
    path: '/bookings',
    name: 'MyBookings',
    component: () => import('../views/MyBookingsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/guide-center',
    name: 'GuideCenter',
    component: () => import('../views/GuideCenterView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('../views/MessagesView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/AdminView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach(to => {
  if (to.meta.requiresAuth && !authState.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && authState.user?.role !== 2) {
    return { path: '/' }
  }
  return true
})

export default router
