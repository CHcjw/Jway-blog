<template>
  <div class="admin-page">
    <AdminTabs />
    <div class="admin-header card-style">
      <div class="title-wrap">
        <h2>网站导航管理</h2>
        <div class="quick-links">
          <el-button text @click="router.push({ name: 'AdminPosts' })">文章</el-button>
          <el-button text @click="router.push({ name: 'AdminWorks' })">作品</el-button>
          <el-button text @click="router.push({ name: 'AdminMoments' })">旧时光</el-button>
          <el-button text type="primary">网站导航</el-button>
        </div>
      </div>
      <div class="actions">
        <el-button @click="resetForm">新建导航</el-button>
        <el-button type="danger" plain @click="logout">退出登录</el-button>
      </div>
    </div>

    <div class="admin-main">
      <div class="left card-style">
        <el-table :data="links" height="640" v-loading="loading">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="sectionTitle" label="分组" width="160" show-overflow-tooltip />
          <el-table-column prop="name" label="名称" width="160" show-overflow-tooltip />
          <el-table-column prop="url" label="URL" min-width="220" show-overflow-tooltip />
          <el-table-column label="操作" width="240">
            <template #default="scope">
              <el-button link type="primary" @click="edit(scope.row.id)">编辑</el-button>
              <el-button link type="success" @click="duplicate(scope.row.id)">复制</el-button>
              <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="right card-style">
        <h3>{{ form.id ? '编辑导航' : '新建导航' }}</h3>
        <el-form :model="form" label-width="95px">
          <el-form-item label="分组名称"><el-input v-model="form.sectionTitle" placeholder="如：开发工具推荐" /></el-form-item>
          <el-form-item label="分组图标"><el-input v-model="form.sectionIcon" placeholder="如：bi bi-terminal" /></el-form-item>
          <el-form-item label="分组排序"><el-input-number v-model="form.sectionOrder" :min="0" :max="999" /></el-form-item>
          <el-form-item label="站点名称"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="站点描述"><el-input v-model="form.desc" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="链接URL"><el-input v-model="form.url" /></el-form-item>
          <el-form-item label="Logo URL"><el-input v-model="form.logo" /></el-form-item>
          <el-form-item label="站点排序"><el-input-number v-model="form.sortOrder" :min="0" :max="999" /></el-form-item>
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
  adminCreateWebsiteLink,
  adminDeleteWebsiteLink,
  adminFetchWebsiteLink,
  adminFetchWebsiteLinks,
  adminUpdateWebsiteLink,
  clearAdminEntryKey,
  clearAdminToken
} from '../api/admin'

const router = useRouter()
const links = ref([])
const loading = ref(false)
const saving = ref(false)

const form = reactive({
  id: null,
  sectionTitle: '',
  sectionIcon: 'bi bi-link-45deg',
  sectionOrder: 0,
  name: '',
  desc: '',
  url: '',
  logo: '',
  sortOrder: 0
})

const loadList = async () => {
  loading.value = true
  try {
    links.value = await adminFetchWebsiteLinks()
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.sectionTitle = ''
  form.sectionIcon = 'bi bi-link-45deg'
  form.sectionOrder = 0
  form.name = ''
  form.desc = ''
  form.url = ''
  form.logo = ''
  form.sortOrder = 0
}

const edit = async (id) => {
  const data = await adminFetchWebsiteLink(id)
  form.id = data.id
  form.sectionTitle = data.sectionTitle
  form.sectionIcon = data.sectionIcon
  form.sectionOrder = data.sectionOrder
  form.name = data.name
  form.desc = data.desc
  form.url = data.url
  form.logo = data.logo || ''
  form.sortOrder = data.sortOrder
}

const buildPayload = () => ({
  sectionTitle: form.sectionTitle,
  sectionIcon: form.sectionIcon,
  sectionOrder: form.sectionOrder,
  name: form.name,
  desc: form.desc,
  url: form.url,
  logo: form.logo,
  sortOrder: form.sortOrder
})

const submit = async () => {
  if (!form.sectionTitle || !form.sectionIcon || !form.name || !form.desc || !form.url) {
    ElMessage.error('请先填写完整信息')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await adminUpdateWebsiteLink(form.id, buildPayload())
      ElMessage.success('导航更新成功')
    } else {
      await adminCreateWebsiteLink(buildPayload())
      ElMessage.success('导航创建成功')
    }
    await loadList()
    resetForm()
  } finally {
    saving.value = false
  }
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除该导航项吗？', '提示', { type: 'warning' })
  await adminDeleteWebsiteLink(id)
  ElMessage.success('删除成功')
  if (form.id === id) resetForm()
  await loadList()
}

const duplicate = async (id) => {
  try {
    const data = await adminFetchWebsiteLink(id)
    await adminCreateWebsiteLink({
      sectionTitle: data.sectionTitle,
      sectionIcon: data.sectionIcon,
      sectionOrder: data.sectionOrder,
      name: `${data.name}（副本）`,
      desc: data.desc,
      url: data.url,
      logo: data.logo || '',
      sortOrder: data.sortOrder
    })
    ElMessage.success('导航副本创建成功')
    await loadList()
  } catch (error) {
    ElMessage.error(error.message || '复制失败')
  }
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
.admin-main { display: grid; grid-template-columns: 1.2fr 1fr; gap: 16px; }
.left, .right { padding: 14px; background: var(--blog-card-bg); }
@media (max-width: 1200px) { .admin-main { grid-template-columns: 1fr; } }
</style>
