import request from '../utils/request'

export function getInventoryPage(params) {
  return request.get('/inventory/page', { params })
}

export function getWarningList(params) {
  return request.get('/inventory/warning', { params })
}

export function stockIn(data) {
  return request.post('/inventory/in', data)
}

export function stockOut(data) {
  return request.post('/inventory/out', data)
}

export function stockReturn(data) {
  return request.post('/inventory/return', data)
}

export function inventoryCheck(data) {
  return request.post('/inventory/check', data)
}
