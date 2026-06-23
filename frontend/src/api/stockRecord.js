import request from '../utils/request'

export function getStockRecordPage(params) {
  return request.get('/stock-record/page', { params })
}
