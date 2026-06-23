<template>
  <div class="page-container">
    <!-- 选项卡 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="库存列表" name="list" />
      <el-tab-pane label="库存预警" name="warning" />
    </el-tabs>

    <!-- 操作按钮 -->
    <div class="search-bar">
      <el-button type="success" @click="openDialog('in')">
        <el-icon><Download /></el-icon> 设备入库
      </el-button>
      <el-button type="warning" @click="openDialog('out')">
        <el-icon><Upload /></el-icon> 设备出库
      </el-button>
      <el-button type="info" @click="openDialog('return')">
        <el-icon><RefreshLeft /></el-icon> 设备退库
      </el-button>
      <el-button type="primary" @click="openDialog('check')">
        <el-icon><DocumentChecked /></el-icon> 设备盘点
      </el-button>
    </div>

    <!-- 库存表格 -->
    <el-table :data="tableData" stripe border v-loading="loading">
      <el-table-column prop="deviceName" label="设备名称" min-width="150" />
      <el-table-column prop="quantity" label="库存数量" width="110" />
      <el-table-column prop="status" label="设备状态" width="110">
        <template #default="{ row }">
          <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="warningValue" label="预警值" width="90" />
      <el-table-column prop="location" label="存放位置" width="150" />
      <el-table-column label="库存状态" width="110">
        <template #default="{ row }">
          <el-tag v-if="row.quantity <= row.warningValue" type="danger">低于预警</el-tag>
          <el-tag v-else type="success">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="170" />
    </el-table>

    <!-- 分页 -->
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

    <!-- 入库弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="选择设备" prop="deviceId" v-if="activeDialog !== 'check'">
          <el-select v-model="form.deviceId" filterable placeholder="请选择设备" style="width: 100%">
            <el-option
              v-for="item in deviceOptions"
              :key="item.id"
              :label="item.deviceName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择设备" prop="deviceId" v-else>
          <el-select v-model="form.deviceId" filterable placeholder="请选择设备" style="width: 100%">
            <el-option
              v-for="item in deviceOptions"
              :key="item.id"
              :label="item.deviceName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="经手人" prop="operatorName" v-if="activeDialog === 'in'">
          <el-input v-model="form.operatorName" placeholder="请输入经手人" />
        </el-form-item>
        <el-form-item label="存放位置" prop="targetPlace" v-if="activeDialog === 'in'">
          <el-input v-model="form.targetPlace" placeholder="请输入存放位置" />
        </el-form-item>
        <el-form-item label="领用人" prop="receiver" v-if="activeDialog === 'out'">
          <el-input v-model="form.receiver" placeholder="请输入领用人" />
        </el-form-item>
        <el-form-item label="经手人" prop="operatorName" v-if="activeDialog === 'out'">
          <el-input v-model="form.operatorName" placeholder="请输入经手人" />
        </el-form-item>
        <el-form-item label="来源位置" prop="sourcePlace" v-if="activeDialog === 'out'">
          <el-input v-model="form.sourcePlace" placeholder="请输入来源位置" />
        </el-form-item>
        <el-form-item label="去往位置" prop="targetPlace" v-if="activeDialog === 'out'">
          <el-input v-model="form.targetPlace" placeholder="请输入去往位置" />
        </el-form-item>
        <el-form-item label="退库人" prop="receiver" v-if="activeDialog === 'return'">
          <el-input v-model="form.receiver" placeholder="请输入退库人" />
        </el-form-item>
        <el-form-item label="经手人" prop="operatorName" v-if="activeDialog === 'return'">
          <el-input v-model="form.operatorName" placeholder="请输入经手人" />
        </el-form-item>
        <el-form-item label="来源位置" prop="sourcePlace" v-if="activeDialog === 'return'">
          <el-input v-model="form.sourcePlace" placeholder="请输入来源位置" />
        </el-form-item>
        <el-form-item label="实际库存" prop="actualQuantity" v-if="activeDialog === 'check'">
          <el-input-number v-model="form.actualQuantity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="盘点人" prop="checker" v-if="activeDialog === 'check'">
          <el-input v-model="form.checker" placeholder="请输入盘点人" />
        </el-form-item>
      </el-form>

      <!-- 盘点结果 -->
      <el-card v-if="checkResult" style="margin-top: 12px">
        <template #header>盘点结果</template>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="设备名称">{{ checkResult.deviceName }}</el-descriptions-item>
          <el-descriptions-item label="系统库存">{{ checkResult.systemQuantity }}</el-descriptions-item>
          <el-descriptions-item label="实际库存">{{ checkResult.actualQuantity }}</el-descriptions-item>
          <el-descriptions-item label="差异数量">
            <el-tag :type="checkResult.differenceQuantity === 0 ? 'success' : 'danger'">
              {{ checkResult.differenceQuantity }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getInventoryPage,
  getWarningList,
  stockIn,
  stockOut,
  stockReturn,
  inventoryCheck
} from '../../api/inventory'
import { getAllDevices } from '../../api/device'

const activeTab = ref('list')
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const deviceOptions = ref([])

// 弹窗控制
const dialogVisible = ref(false)
const activeDialog = ref('in')
const formRef = ref()
const checkResult = ref(null)

const dialogTitle = computed(() => {
  const map = { in: '设备入库', out: '设备出库', return: '设备退库', check: '设备盘点' }
  return map[activeDialog.value]
})

const form = reactive({
  deviceId: null,
  quantity: 1,
  operatorName: '',
  receiver: '',
  sourcePlace: '',
  targetPlace: '',
  actualQuantity: 0,
  checker: ''
})

const formRules = {
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  operatorName: [{ required: true, message: '请输入经手人', trigger: 'blur' }],
  receiver: [{ required: true, message: '请输入领用人/退库人', trigger: 'blur' }],
  sourcePlace: [{ required: true, message: '请输入来源位置', trigger: 'blur' }],
  targetPlace: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  actualQuantity: [{ required: true, message: '请输入实际库存', trigger: 'blur' }],
  checker: [{ required: true, message: '请输入盘点人', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
  loadDevices()
})

async function loadDevices() {
  try {
    const res = await getAllDevices()
    deviceOptions.value = res.data?.records || []
  } catch (e) {
    // ignore
  }
}

function handleTabChange() {
  pageNum.value = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const api = activeTab.value === 'warning' ? getWarningList : getInventoryPage
    const res = await api({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function openDialog(type) {
  activeDialog.value = type
  checkResult.value = null
  resetForm()
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, {
    deviceId: null,
    quantity: 1,
    operatorName: '',
    receiver: '',
    sourcePlace: '',
    targetPlace: '',
    actualQuantity: 0,
    checker: ''
  })
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (activeDialog.value === 'in') {
      await stockIn({
        deviceId: form.deviceId,
        quantity: form.quantity,
        operatorName: form.operatorName,
        targetPlace: form.targetPlace
      })
      ElMessage.success('入库成功')
    } else if (activeDialog.value === 'out') {
      await stockOut({
        deviceId: form.deviceId,
        quantity: form.quantity,
        receiver: form.receiver,
        operatorName: form.operatorName,
        sourcePlace: form.sourcePlace,
        targetPlace: form.targetPlace
      })
      ElMessage.success('出库成功')
    } else if (activeDialog.value === 'return') {
      await stockReturn({
        deviceId: form.deviceId,
        quantity: form.quantity,
        receiver: form.receiver,
        operatorName: form.operatorName,
        sourcePlace: form.sourcePlace
      })
      ElMessage.success('退库成功')
    } else if (activeDialog.value === 'check') {
      const res = await inventoryCheck({
        deviceId: form.deviceId,
        actualQuantity: form.actualQuantity,
        checker: form.checker
      })
      checkResult.value = res.data
      ElMessage.success('盘点完成')
      return
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}
</script>
