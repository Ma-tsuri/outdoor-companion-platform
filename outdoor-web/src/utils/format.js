const pad = value => String(value).padStart(2, '0')

export function formatDate(value, withTime = false) {
  if (!value) return '待定'
  const date = new Date(String(value).replace(' ', 'T'))
  if (Number.isNaN(date.getTime())) return String(value)
  const day = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
  if (!withTime) return day
  return `${day} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}

export const activityTypes = ['徒步', '骑行', '登山', '露营', '攀岩', '溯溪', '其他']

export function difficultyText(value) {
  return ['休闲', '轻松', '进阶', '挑战', '硬核'][Math.min(4, Math.max(0, (value || 1) - 1))]
}

export const bookingStatusMap = {
  0: { text: '待支付', cls: 'warn' },
  1: { text: '已支付', cls: 'info' },
  2: { text: '已取消', cls: 'danger' },
  3: { text: '已完成', cls: '' }
}

export const guideStatusMap = {
  0: { text: '待审核', cls: 'warn' },
  1: { text: '已通过', cls: '' },
  2: { text: '已拒绝', cls: 'danger' }
}
