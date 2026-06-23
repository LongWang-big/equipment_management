import request from '../utils/request'

export function getOrderPage(params) {
  return request.get('/purchase-order/page', { params })
}

export function getOrderById(id) {
  return request.get(`/purchase-order/${id}`)
}

export function addOrder(data) {
  return request.post('/purchase-order', data)
}

export function updateOrder(data) {
  return request.put('/purchase-order', data)
}

export function deleteOrder(id) {
  return request.delete(`/purchase-order/${id}`)
}

export function getMonthlyReport(year) {
  return request.get('/report/purchase/month', { params: { year } })
}

export function getYearlyReport(year) {
  return request.get('/report/purchase/year', { params: { year } })
}
