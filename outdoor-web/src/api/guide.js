import request from './request.js'

export const listGuides = () => request.get('/guide/list')
export const getGuide = id => request.get('/guide/detail/' + id)
export const myGuide = () => request.get('/guide/mine')
export const applyGuide = data => request.post('/guide/apply', data)
export const updateGuide = data => request.put('/guide/update', data)
