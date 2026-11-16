import request from './request.js'

export const listMessages = () => request.get('/message/list')
export const unreadCount = () => request.get('/message/unread')
export const readMessage = id => request.put('/message/read/' + id)
export const readAllMessages = () => request.put('/message/read-all')
