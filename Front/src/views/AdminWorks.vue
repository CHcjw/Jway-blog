<template>
  <div class="admin-page">
    <AdminTabs />
    <div class="admin-header card-style">
      <div class="title-wrap">
        <h2>作品管理</h2>
        <div class="quick-links">
          <el-button text @click="router.push({ name: 'AdminPosts' })">文章</el-button>
          <el-button text type="primary">作品</el-button>
          <el-button text @click="router.push({ name: 'AdminMoments' })">旧时光</el-button>
          <el-button text @click="router.push({ name: 'AdminWebsiteLinks' })">网站导航</el-button>
        </div>
      </div>
      <div class="actions">
        <el-button @click="resetForm">新建作品</el-button>
        <el-button type="danger" plain @click="logout">退出登录</el-button>
      </div>
    </div>

    <div class="admin-main">
      <div class="left card-style">
        <el-table :data="works" height="640" v-loading="loading">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="period" label="周期" width="120" />
          <el-table-column prop="teamSize" label="团队" width="120" />
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
        <h3>{{ form.id ? '编辑作品' : '新建作品' }}</h3>
        <el-form :model="form" label-width="90px">
          <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
          <el-form-item label="封面URL"><el-input v-model="form.image" /></el-form-item>
          <el-form-item label="描述"><el-input v-model="form.desc" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="周期"><el-input v-model="form.period" placeholder="例如：4个月" /></el-form-item>
          <el-form-item label="团队"><el-input v-model="form.teamSize" placeholder="例如：5人" /></el-form-item>
          <el-form-item label="技术栈">
            <el-input v-model="techsInput" type="textarea" :rows="3" placeholder="用逗号分隔，如：Vue3, Spring Boot, Redis" />
          </el-form-item>
          <el-form-item label="亮点">
            <el-input v-model="pointsInput" type="textarea" :rows="5" placeholder="每行一个亮点" />
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
  adminCreateWork,
  adminDeleteWork,
  adminFetchWork,
  adminFetchWorks,
  adminUpdateWork,
  clearAdminEntryKey,
  clearAdminToken
} from '../api/admin'

const router = useRouter()
const works = ref([])
const loading = ref(false)
const saving = ref(false)
const techsInput = ref('')
const pointsInput = ref('')

const form = reactive({
  id: null,
  title: '',
  image: '',
  desc: '',
  period: '',
  teamSize: ''
})

const loadList = async () => {
  loading.value = true
  try {
    works.value = await adminFetchWorks()
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.image = ''
  form.desc = ''
  form.period = ''
  form.teamSize = ''
  techsInput.value = ''
  pointsInput.value = ''
}

const edit = async (id) => {
  const data = await adminFetchWork(id)
  form.id = data.id
  form.title = data.title
  form.image = data.image
  form.desc = data.desc
  form.period = data.period
  form.teamSize = data.teamSize
  techsInput.value = (data.techs || []).join(', ')
  pointsInput.value = (data.points || []).join('\n')
}

const buildPayload = () => ({
  title: form.title,
  image: form.image,
  desc: form.desc,
  period: form.period,
  teamSize: form.teamSize,
  techs: techsInput.value.split(',').map(v => v.trim()).filter(Boolean),
  points: pointsInput.value.split('\n').map(v => v.trim()).filter(Boolean)
})

const submit = async () => {
  if (!form.title || !form.image || !form.desc || !form.period || !form.teamSize) {
    ElMessage.error('请先填写完整信息')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await adminUpdateWork(form.id, buildPayload())
      ElMessage.success('作品更新成功')
    } else {
      await adminCreateWork(buildPayload())
      ElMessage.success('作品创建成功')
    }
    await loadList()
    resetForm()
  } finally {
    saving.value = false
  }
}

const remove = async (id) => {
  await ElMessageBox.confirm('确认删除该作品吗？', '提示', { type: 'warning' })
  await adminDeleteWork(id)
  ElMessage.success('删除成功')
  if (form.id === id) resetForm()
  await loadList()
}

const duplicate = async (id) => {
  try {
    const data = await adminFetchWork(id)
    await adminCreateWork({
      title: `${data.title}（副本）`,
      image: data.image,
      desc: data.desc,
      period: data.period,
      teamSize: data.teamSize,
      techs: data.techs || [],
      points: data.points || []
    })
    ElMessage.success('作品副本创建成功')
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
.admin-main { display: grid; grid-template-columns: 1fr 1.1fr; gap: 16px; }
.left, .right { padding: 14px; background: var(--blog-card-bg); }
@media (max-width: 1200px) { .admin-main { grid-template-columns: 1fr; } }
</style>
