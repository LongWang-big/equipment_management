import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const isCollapse = ref(false)

  function toggleSidebar() {
    isCollapse.value = !isCollapse.value
  }

  return { isCollapse, toggleSidebar }
})
