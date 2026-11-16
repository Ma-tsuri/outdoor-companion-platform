import request from './request.js'

export const listReviews = params => request.get('/review/list', { params })
export const createReview = data => request.post('/review/create', data)
