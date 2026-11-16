import request from './request.js'

export const getStats = () => request.get('/admin/stats')
export const listUsers = params => request.get('/admin/users', { params })
export const setUserStatus = (id, status) => request.put(`/admin/user/status/${id}?status=${status}`)
export const listAllGuides = params => request.get('/admin/guides', { params })
export const auditGuide = (id, status) => request.put(`/guide/audit/${id}?status=${status}`)
export const listAllActivities = params => request.get('/admin/activities', { params })
export const setActivityStatus = (id, status) => request.put(`/admin/activity/status/${id}?status=${status}`)
export const listAllBookings = params => request.get('/admin/bookings', { params })
