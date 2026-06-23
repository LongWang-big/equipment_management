<template>
  <div class="page-container">
    <div class="search-bar">
      <el-select v-model="filterStatus" clearable placeholder="全部状态" style="width: 150px" @change="handleSearch">
        <el-option label="待审批" value="PENDING" />
        <el-option label="已审批" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button type="danger" @click="handleAdd">
        <el-icon><Warning /></el-icon> 新增报损
      </el-button>
    </div>

    <el-table :data="tableData" stripe border v-loading="loading">
      <el-table-column prop="deviceCode" label="设备编码" width="130" />
      <el-table-column prop="deviceName" label="设备名称" min-width="150" />
      <el-table-column prop="scrapType" label="报损类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.scrapType === 'SCRAP'" type="danger">报废</el-tag>
          <el-tag v-else-if="row.scrapType === 'REPAIR'" type="warning">报修</el-tag>
          <el-tag v-else type="info">损坏</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="scrapQuantity" label="报损数量" width="90" />
      <el-table-column prop="scrapReason" label="报损原因" min-width="180" show-overflow-tooltip />
      <el-table-column prop="scrapDate" label="报损日期" width="110" />
      <el-table-column prop="applicant" label="申请人" width="90" />
      <el-table-column prop="approver" label="审批人" width="90" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'PENDING'" type="warning">待审批</el-tag>
          <el-tag v-else-if="row.status === 'APPROVED'" type="success">已审批</el-tag>
          <el-tag v-else type="info">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING'" type="success" link size="small"
            @click="handleApprove(row, 'APPROVE')">通过</el-button>
          <el-button v-if="row.status === 'PENDING'" type="warning" link size="small"
            @click="handleApprove(row, 'REJECT')">驳回</el-button>
          <el-popconfirm v-if="row.status !== 'APPROVED'" title="确定删除该记录吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" link size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
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

    <!-- 新增报损弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增报损申请" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="选择设备" prop="deviceId">
          <el-select v-model="form.deviceId" filterable placeholder="请选择设备" style="width: 100%">
            <el-option v-for="item in deviceOptions" :key="item.id" :label="item.deviceName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报损类型" prop="scrapType">
          <el-select v-model="form.scrapType" placeholder="请选择类型" style="width: 100%">
            <el-option label="报废" value="SCRAP" />
            <el-option label="报修" value="REPAIR" />
            <el-option label="损坏" value="DAMAGE" />
          </el-select>
        </el-form-item>
        <el-form-item label="报损数量" prop="scrapQuantity">
          <el-input-number v-model="form.scrapQuantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报损日期" prop="scrapDate">
          <el-date-picker v-model="form.scrapDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="申请人" prop="applicant">
          <el-input v-model="form.applicant" placeholder="请输入申请人" />
        </el-form-item>
        <el-form-item label="报损原因" prop="scrapReason">
          <el-input v-model="form.scrapReason" type="textarea" :rows="3" placeholder="请描述报损原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 审批弹窗 -->
    <el-dialog v-model="approveVisible" title="审批报损" width="400px" destroy-on-close>
      <el-form :model="approveForm" label-width="90px">
        <el-form-item label="审批人">
          <el-input v-model="approveForm.approver" placeholder="请输入审批人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button :type="approveForm.action === 'APPROVE' ? 'success' : 'warning'"
          :loading="approveLoading" @click="handleApproveSubmit">
          {{ approveForm.action === 'APPROVE' ? '确认通过' : '确认驳回' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getScrapPage, addScrap, approveScrap, deleteScrap } from '../../api/scrap'
import { getAllDevices } from '../../api/device'

const loading = ref(false)
const submitLoading = ref(false)
const approveLoading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const deviceOptions = ref([])

// 新增弹窗
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  deviceId: null, scrapType: '', scrapQuantity: 1,
  scrapDate: '', applicant: '', scrapReason: ''
})
const rules = {
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  scrapType: [{ required: true, message: '请选择报损类型', trigger: 'change' }],
  scrapQuantity: [{ required: true, message: '请输入报损数量', trigger: 'blur' }],
  scrapDate: [{ required: true, message: '请选择报损日期', trigger: 'change' }],
  applicant: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  scrapReason: [{ required: true, message: '请输入报损原因', trigger: 'blur' }]
}

// 审批弹窗
const approveVisible = ref(false)
const approveForm = reactive({ id: null, action: '', approver: '' })

onMounted(() => { loadData(); loadDevices() })

async function loadDevices() {
  try {
    const res = await getAllDevices()
    deviceOptions.value = res.data?.records || []
  } catch (e) { /* ignore */ }
}

function handleSearch() { pageNum.value = 1; loadData() }

async function loadData() {
  loading.value = true
  try {
    const res = await getScrapPage({
      pageNum: pageNum.value, pageSize: pageSize.value,
      status: filterStatus.value || undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  Object.assign(form, { deviceId: null, scrapType: '', scrapQuantity: 1, scrapDate: '', applicant: '', scrapReason: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await addScrap(form)
    ElMessage.success('报损申请已提交')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

function handleApprove(row, action) {
  approveForm.id = row.id
  approveForm.action = action
  approveForm.approver = ''
  approveVisible.value = true
}

async function handleApproveSubmit() {
  if (!approveForm.approver) {
    ElMessage.warning('请输入审批人')
    return
  }
  approveLoading.value = true
  try {
    await approveScrap(approveForm)
    ElMessage.success(approveForm.action === 'APPROVE' ? '审批通过' : '已驳回')
    approveVisible.value = false
    loadData()
  } finally {
    approveLoading.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteScrap(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* 错误已在拦截器处理 */ }
}
</script>
