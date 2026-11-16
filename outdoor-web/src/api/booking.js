import request from './request.js'

export const createBooking = data => request.post('/booking/create', data)
export const listMyBookings = () => request.get('/booking/list')
export const listGuideBookings = () => request.get('/booking/guide')
export const payBooking = id => request.put('/booking/pay/' + id)
export const cancelBooking = id => request.put('/booking/cancel/' + id)
export const completeBooking = id => request.put('/booking/complete/' + id)
