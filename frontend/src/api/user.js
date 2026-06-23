import request from '../utils/request'

export function getUserPage(params) {
  return request.get('/user/page', { params })
}

export function addUser(data) {
  return request.post('/user', data)
}

export function updateUser(data) {
  return request.put('/user', data)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

export function resetPassword(id, newPassword) {
  return request.put(`/user/${id}/password`, null, { params: { newPassword } })
}

export function changePassword(oldPassword, newPassword) {
  return request.put('/user/change-password', { oldPassword, newPassword })
}
