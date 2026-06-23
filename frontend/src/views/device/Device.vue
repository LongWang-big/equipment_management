<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索设备名称/编码"
        clearable
        style="width: 260px"
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增设备
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" stripe border v-loading="loading">
      <el-table-column prop="deviceCode" label="设备编码" width="140" />
      <el-table-column prop="deviceName" label="设备名称" min-width="150" />
      <el-table-column prop="categoryName" label="设备分类" width="120" />
      <el-table-column prop="model" label="规格型号" width="130" />
      <el-table-column prop="unitPrice" label="单价(元)" width="110">
        <template #default="{ row }">
          {{ row.unitPrice?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="supplier" label="供应商" width="140" />
      <el-table-column prop="manufacturer" label="生产厂家" width="140" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="info" link size="small" @click="handleView(row)">查看</el-button>
          <el-button type="primary" link size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-popconfirm
            title="确定删除该设备吗？"
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
      :title="isEdit ? '编辑设备' : '新增设备'"
      width="550px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="设备编码" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入设备编码" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备分类" prop="categoryId">
          <el-select v-model="form.categoryId" filterable clearable placeholder="请选择分类" style="width: 100%">
            <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入规格型号" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="form.supplier" placeholder="请输入供应商" />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 设备详情弹窗 -->
    <el-dialog v-model="detailVisible" title="设备详情" width="550px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="设备编码">{{ detailData.deviceCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ detailData.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="设备分类">{{ detailData.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ detailData.model || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单价(元)">{{ detailData.unitPrice?.toFixed(2) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detailData.supplier || '-' }}</el-descriptions-item>
        <el-descriptions-item label="生产厂家">{{ detailData.manufacturer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDevicePage, addDevice, updateDevice, deleteDevice } from '../../api/device'
import { getAllCategories } from '../../api/category'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const categoryOptions = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 详情弹窗
const detailVisible = ref(false)
const detailData = ref({})

const form = reactive({
  id: null,
  deviceCode: '',
  deviceName: '',
  categoryId: null,
  model: '',
  unitPrice: 0,
  supplier: '',
  manufacturer: '',
  remark: ''
})

const rules = {
  deviceCode: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
  loadCategories()
})

async function loadCategories() {
  try {
    const res = await getAllCategories()
    categoryOptions.value = res.data?.records || []
  } catch (e) {
    // ignore
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getDevicePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined
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

function handleView(row) {
  detailData.value = row
  detailVisible.value = true
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
    deviceCode: row.deviceCode,
    deviceName: row.deviceName,
    categoryId: row.categoryId,
    model: row.model,
    unitPrice: row.unitPrice,
    supplier: row.supplier,
    manufacturer: row.manufacturer,
    remark: row.remark
  })
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, {
    id: null,
    deviceCode: '',
    deviceName: '',
    categoryId: null,
    model: '',
    unitPrice: 0,
    supplier: '',
    manufacturer: '',
    remark: ''
  })
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateDevice(form)
      ElMessage.success('修改成功')
    } else {
      await addDevice(form)
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
    await deleteDevice(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 错误已在拦截器处理
  }
}
</script>
