<template>
  <div class="page-container">
    <div class="search-bar">
      <el-select v-model="filterType" clearable placeholder="全部类型" style="width: 150px" @change="handleSearch">
        <el-option label="入库" value="IN" />
        <el-option label="出库" value="OUT" />
        <el-option label="退库" value="RETURN" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
    </div>

    <el-table :data="tableData" stripe border v-loading="loading">
      <el-table-column prop="deviceName" label="设备名称" min-width="150" />
      <el-table-column prop="operationType" label="操作类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.operationType === 'IN'" type="success">入库</el-tag>
          <el-tag v-else-if="row.operationType === 'OUT'" type="warning">出库</el-tag>
          <el-tag v-else type="info">退库</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="receiver" label="领用人" width="100" />
      <el-table-column prop="sourcePlace" label="来源" width="140" />
      <el-table-column prop="targetPlace" label="去处" width="140" />
      <el-table-column prop="operatorName" label="经手人" width="100" />
      <el-table-column prop="operationTime" label="操作时间" width="170" />
    </el-table>

    <el-pagination
      style="margin-top: 16px; justify-content: flex-end"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStockRecordPage } from '../../api/stockRecord'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterType = ref('')

onMounted(() => { loadData() })

function handleSearch() { pageNum.value = 1; loadData() }

async function loadData() {
  loading.value = true
  try {
    const res = await getStockRecordPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      operationType: filterType.value || undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}
</script>
