<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 280px"
        @change="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增采购单
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" stripe border v-loading="loading">
      <el-table-column prop="deviceName" label="设备名称" min-width="150" />
      <el-table-column prop="purchaseQuantity" label="采购数量" width="110" />
      <el-table-column prop="purchasePrice" label="采购单价(元)" width="130">
        <template #default="{ row }">
          {{ row.purchasePrice?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额(元)" width="130">
        <template #default="{ row }">
          {{ row.totalAmount?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="purchaser" label="采购员" width="110" />
      <el-table-column prop="purchaseDate" label="采购日期" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-popconfirm
            title="确定删除该采购单吗？"
            @confirm="handleDelete(row.id)"
          >
            <template #reference>
              <el-button type="danger" link size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑采购单' : '新增采购单'"
      width="550px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择设备" prop="deviceId">
          <el-select v-model="form.deviceId" filterable placeholder="请选择设备" style="width: 100%">
            <el-option
              v-for="item in deviceOptions"
              :key="item.id"
              :label="item.deviceName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="采购数量" prop="purchaseQuantity">
          <el-input-number v-model="form.purchaseQuantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="采购单价" prop="purchasePrice">
          <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="采购员" prop="purchaser">
          <el-input v-model="form.purchaser" placeholder="请输入采购员" />
        </el-form-item>
        <el-form-item label="采购日期" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderPage, addOrder, updateOrder, deleteOrder } from '../../api/purchase'
import { getAllDevices } from '../../api/device'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref(null)
const deviceOptions = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  deviceId: null,
  purchaseQuantity: 1,
  purchasePrice: 0,
  purchaser: '',
  purchaseDate: ''
})

const rules = {
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  purchaseQuantity: [{ required: true, message: '请输入采购数量', trigger: 'blur' }],
  purchasePrice: [{ required: true, message: '请输入采购单价', trigger: 'blur' }],
  purchaser: [{ required: true, message: '请输入采购员', trigger: 'blur' }],
  purchaseDate: [{ required: true, message: '请选择采购日期', trigger: 'change' }]
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

async function loadData() {
  loading.value = true
  try {
    const res = await getOrderPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      startDate: dateRange.value?.[0] || undefined,
      endDate: dateRange.value?.[1] || undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    deviceId: row.deviceId,
    purchaseQuantity: row.purchaseQuantity,
    purchasePrice: row.purchasePrice,
    purchaser: row.purchaser,
    purchaseDate: row.purchaseDate
  })
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, {
    id: null,
    deviceId: null,
    purchaseQuantity: 1,
    purchasePrice: 0,
    purchaser: '',
    purchaseDate: ''
  })
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateOrder(form)
      ElMessage.success('修改成功')
    } else {
      await addOrder(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteOrder(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 错误已在拦截器处理
  }
}
</script>
