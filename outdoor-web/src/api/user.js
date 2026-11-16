import request from './request.js'

export const register = data => request.post('/user/register', data)
export const login = data => request.post('/user/login', data)
export const getInfo = () => request.get('/user/info')
export const updateProfile = data => request.put('/user/profile', data)
export const changePassword = data => request.put('/user/password', data)
