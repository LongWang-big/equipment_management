<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'">
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <div class="logo">
          <img src="../assets/logo.svg" alt="" style="width: 32px; height: 32px" />
          <span v-show="!isCollapse">设备管理系统</span>
        </div>

        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/device">
          <el-icon><Monitor /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Files /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/inventory">
          <el-icon><Box /></el-icon>
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/stock-record">
          <el-icon><List /></el-icon>
          <span>出入库记录</span>
        </el-menu-item>
        <el-menu-item index="/purchase">
          <el-icon><ShoppingCart /></el-icon>
          <span>采购管理</span>
        </el-menu-item>
        <el-menu-item index="/scrap">
          <el-icon><Warning /></el-icon>
          <span>报损管理</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容 -->
    <el-container>
      <el-header class="layout-header">
        <div class="left">
          <el-icon
            style="cursor: pointer; margin-right: 12px"
            @click="appStore.toggleSidebar"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span>{{ route.meta.title }}</span>
        </div>
        <div class="right">
          <span>欢迎，{{ authStore.realName }}</span>
          <el-tag size="small" type="info">{{ authStore.role }}</el-tag>
          <el-dropdown @command="handleCommand">
            <el-icon style="cursor: pointer; color: #fff"><Setting /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="changePwdVisible" title="修改密码" width="400px" destroy-on-close>
      <el-form ref="changePwdFormRef" :model="changePwdForm" :rules="changePwdRules" label-width="90px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="changePwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="changePwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="changePwdLoading" @click="handleChangePwdSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { useAppStore } from '../stores/app'
import { changePassword } from '../api/user'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()
const isCollapse = computed(() => appStore.isCollapse)

// 修改密码
const changePwdVisible = ref(false)
const changePwdLoading = ref(false)
const changePwdFormRef = ref()
const changePwdForm = reactive({ oldPassword: '', newPassword: '' })
const changePwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    authStore.logout()
    router.push('/login')
  } else if (cmd === 'changePassword') {
    changePwdForm.oldPassword = ''
    changePwdForm.newPassword = ''
    changePwdVisible.value = true
  }
}

async function handleChangePwdSubmit() {
  const valid = await changePwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  changePwdLoading.value = true
  try {
    await changePassword(changePwdForm.oldPassword, changePwdForm.newPassword)
    ElMessage.success('密码修改成功')
    changePwdVisible.value = false
  } finally {
    changePwdLoading.value = false
  }
}
</script>

<style scoped>
.el-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.el-aside .el-menu {
  border-right: none;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
}
.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
}
</style>
