import request from './request.js'

export const listActivities = params => request.get('/activity/list', { params })
export const getActivity = id => request.get('/activity/detail/' + id)
export const createActivity = data => request.post('/activity/create', data)
export const updateActivity = data => request.put('/activity/update', data)
export const deleteActivity = id => request.delete('/activity/delete/' + id)
export const myActivities = () => request.get('/activity/mine')
