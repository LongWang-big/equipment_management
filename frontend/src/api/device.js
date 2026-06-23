import request from '../utils/request'

export function getDevicePage(params) {
  return request.get('/device/page', { params })
}

export function getDeviceById(id) {
  return request.get(`/device/${id}`)
}

export function addDevice(data) {
  return request.post('/device', data)
}

export function updateDevice(data) {
  return request.put('/device', data)
}

export function deleteDevice(id) {
  return request.delete(`/device/${id}`)
}

/** 获取全部设备列表（下拉选择用） */
export function getAllDevices() {
  return request.get('/device/page', { params: { pageNum: 1, pageSize: 9999 } })
}
