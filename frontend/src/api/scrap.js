import request from '../utils/request'

export function getScrapPage(params) {
  return request.get('/scrap/page', { params })
}

export function addScrap(data) {
  return request.post('/scrap', data)
}

export function approveScrap(data) {
  return request.put('/scrap/approve', data)
}

export function deleteScrap(id) {
  return request.delete(`/scrap/${id}`)
}
