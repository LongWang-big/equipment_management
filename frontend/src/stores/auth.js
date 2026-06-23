import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const realName = ref(localStorage.getItem('realName') || '')
  const role = ref(localStorage.getItem('role') || '')

  function setAuth(data) {
    token.value = data.token
    realName.value = data.realName
    role.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('realName', data.realName)
    localStorage.setItem('role', data.role)
  }

  function logout() {
    token.value = ''
    realName.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('realName')
    localStorage.removeItem('role')
  }

  return { token, realName, role, setAuth, logout }
})
