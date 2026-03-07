<template>
  <div class="admin-page">
    <AdminTabs />
    <div class="admin-header card-style">
      <div class="title-wrap">
        <h2>旧时光管理</h2>
        <div class="quick-links">
          <el-button text @click="router.push({ name: 'AdminPosts' })">文章</el-button>
          <el-button text @click="router.push({ name: 'AdminWorks' })">作品</el-button>
          <el-button text type="primary">旧时光</el-button>
          <el-button text @click="router.push({ name: 'AdminWebsiteLinks' })">网站导航</el-button>
        </div>
      </div>
      <div class="actions">
        <el-button @click="resetForm">新建节点</el-button>
        <el-button type="danger" plain @click="logout">退出登录</el-button>
      </div>
    </div>

    <div class="admin-main">
      <div class="left card-style">
        <el-table :data="moments" height="640" v-loading="loading">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="timestamp" label="日期" width="120" />
          <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button link type="primary" @click="edit(scope.row.id)">编辑</el-button>
              <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="right card-style">
        <h3>{{ form.id ? '编辑节点' : '新建节点' }}</h3>
        <el-form :model="form" label-width="90px">
          <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
          <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
          <el-form-item label="日期">
            <el-date-picker v-model="form.timestamp" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="form.type" style="width: 100%">
              <el-option label="primary" value="primary" />
              <el-option label="success" value="success" />
              <el-option label="warning" value="warning" />
              <el-option label="info" value="info" />
              <el-option label="danger" value="danger" />
            </el-select>
          </el-form-item>
          <el-form-item label="尺寸">
            <el-select v-model="form.size" style="width: 100%">
              <el-option label="large" value="large" />
              <el-option label="default" value="default" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import AdminTabs from '../components/AdminTabs.vue'
import {
  adminCreateMoment,
  adminDeleteMoment,
  adminFetchMoment,
  adminFetchMoments,
  adminUpdateMoment,
  clearAdminEntryKey,
  clearAdminToken
} from '../api/admin'

const router = useRouter()
const moments = ref([])
const loading = ref(false)
const saving = ref(false)
const form = reactive({
  id: null,
  title: '',
  content: '',
  timestamp: '',
  type: 'primary',
  size: 'default'
})

const loadList = async () => {
  loading.value = true
  try {
    moments.value = await adminFetchMoments()
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.content = ''
  form.timestamp = ''
  form.type = 'primary'
  form.size = 'default'
}

const edit = async (id) => {
  const data = await adminFetchMoment(id)
  form.id = data.id
  form.title = data.title
  form.content = data.content
  form.timestamp = data.timestamp
  form.type = data.type
  form.size = data.size
}

const submit = async () => {
  if (!form.title || !form.content || !form.timestamp || !form.type || !form.size) {
    ElMessage.error('请先填写完整信息')
    return
  }
  saving.value = true
  try {
    const payload = {
      title: form.title,
      content: form.content,
      timestamp: form.timestamp,
      type: form.type,
      size: form.size
    }
    if (form.id) {
      await adminUpdateMoment(form.id, payload)
      ElMessage.success('节点更新成功')
    } else {
      await adminCreateMoment(payload)
      ElMessage.success('节点创建成功')
    }
    await loadList()
    resetForm()
  } finally {
    saving.value = false
  }
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除该时间节点吗？', '提示', { type: 'warning' })
  await adminDeleteMoment(id)
  ElMessage.success('删除成功')
  if (form.id === id) resetForm()
  await loadList()
}

const logout = () => {
  clearAdminToken()
  clearAdminEntryKey()
  router.push({ name: 'AdminLogin' })
}

onMounted(loadList)
</script>

<style scoped lang="scss">
.admin-page { padding: 24px; max-width: 1700px; margin: 70px auto 40px; }
.admin-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 22px; margin-bottom: 16px; }
.title-wrap { display: flex; flex-direction: column; gap: 6px; }
.quick-links { display: flex; gap: 6px; flex-wrap: wrap; }
.actions { display: flex; gap: 10px; }
.admin-main { display: grid; grid-template-columns: 1fr 1.1fr; gap: 16px; }
.left, .right { padding: 14px; background: var(--blog-card-bg); }
@media (max-width: 1200px) { .admin-main { grid-template-columns: 1fr; } }
</style>
