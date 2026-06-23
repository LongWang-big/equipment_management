import request from '../utils/request'

export function getCategoryPage(params) {
  return request.get('/category/page', { params })
}

export function addCategory(data) {
  return request.post('/category', data)
}

export function updateCategory(data) {
  return request.put('/category', data)
}

export function deleteCategory(id) {
  return request.delete(`/category/${id}`)
}

/** 获取全部分类（下拉选择用） */
export function getAllCategories() {
  return request.get('/category/page', { params: { pageNum: 1, pageSize: 9999 } })
}
