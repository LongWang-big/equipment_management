<template>
  <div class="page-container">
    <!-- 统计卡片 -->
    <div class="dashboard-cards">
      <div class="dashboard-card" style="background: linear-gradient(135deg, #667eea, #764ba2)">
        <el-icon class="icon"><Monitor /></el-icon>
        <div class="number">{{ stats.deviceCount }}</div>
        <div class="label">设备总数</div>
      </div>
      <div class="dashboard-card" style="background: linear-gradient(135deg, #f093fb, #f5576c)">
        <el-icon class="icon"><Box /></el-icon>
        <div class="number">{{ stats.totalStock }}</div>
        <div class="label">库存记录</div>
      </div>
      <div class="dashboard-card" style="background: linear-gradient(135deg, #4facfe, #00f2fe)">
        <el-icon class="icon"><WarningFilled /></el-icon>
        <div class="number">{{ stats.warningCount }}</div>
        <div class="label">库存预警</div>
      </div>
      <div class="dashboard-card" style="background: linear-gradient(135deg, #43e97b, #38f9d7)">
        <el-icon class="icon"><ShoppingCart /></el-icon>
        <div class="number">{{ stats.orderCount }}</div>
        <div class="label">采购订单</div>
      </div>
    </div>

    <!-- 采购月报表格 -->
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>本年度采购统计</span>
          <el-select v-model="selectedYear" style="width: 120px" @change="loadReport">
            <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
          </el-select>
        </div>
      </template>
      <el-table :data="reportData" stripe border style="width: 100%">
        <el-table-column prop="yearMonth" label="月份" width="120" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="purchaseQuantity" label="采购数量" width="120" />
        <el-table-column prop="totalAmount" label="采购金额(元)" width="150">
          <template #default="{ row }">
            {{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getDevicePage } from '../../api/device'
import { getInventoryPage, getWarningList } from '../../api/inventory'
import { getOrderPage, getMonthlyReport } from '../../api/purchase'

const currentYear = new Date().getFullYear()
const selectedYear = ref(String(currentYear))
const yearOptions = Array.from({ length: 5 }, (_, i) => String(currentYear - i))

const stats = reactive({
  deviceCount: 0,
  totalStock: 0,
  warningCount: 0,
  orderCount: 0
})

const reportData = ref([])

onMounted(async () => {
  await Promise.all([loadStats(), loadReport()])
})

async function loadStats() {
  try {
    const [deviceRes, stockRes, warningRes, orderRes] = await Promise.all([
      getDevicePage({ pageNum: 1, pageSize: 1 }),
      getInventoryPage({ pageNum: 1, pageSize: 1 }),
      getWarningList({ pageNum: 1, pageSize: 1 }),
      getOrderPage({ pageNum: 1, pageSize: 1 })
    ])
    stats.deviceCount = deviceRes.data?.total || 0
    stats.totalStock = stockRes.data?.total || 0
    stats.warningCount = warningRes.data?.total || 0
    stats.orderCount = orderRes.data?.total || 0
  } catch (e) {
    // 部分接口可能无权限
  }
}

async function loadReport() {
  try {
    const res = await getMonthlyReport(selectedYear.value)
    reportData.value = res.data || []
  } catch (e) {
    reportData.value = []
  }
}
</script>
