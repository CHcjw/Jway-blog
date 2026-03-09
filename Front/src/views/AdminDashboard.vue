<template>
  <div class="admin-page">
    <AdminTabs />
    <div class="admin-header card-style">
      <h2>文章后台管理</h2>
      <div class="actions">
        <el-button @click="resetForm">新建文章</el-button>
        <el-button type="danger" plain @click="logout">退出登录</el-button>
      </div>
    </div>

    <div class="admin-main">
      <div class="left card-style">
        <el-table :data="posts" height="640" v-loading="loadingList">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标" min-width="220" show-overflow-tooltip />
          <el-table-column prop="date" label="日期" width="110" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button link type="primary" @click="edit(scope.row.id)">编辑</el-button>
              <el-button link type="success" @click="duplicate(scope.row.id)">复制</el-button>
              <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="right card-style">
        <h3>{{ form.id ? '编辑文章' : '发布文章' }}</h3>
        <el-form :model="form" label-width="90px">
          <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
          <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="封面URL"><el-input v-model="form.cover" /></el-form-item>
          <el-form-item label="发布日期"><el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" /></el-form-item>
          <el-form-item label="分类"><el-input v-model="form.category" placeholder="输入新分类" /></el-form-item>
          <el-form-item label="标签">
            <el-input v-model="tagsInput" placeholder="多个标签用英文逗号分隔" />
          </el-form-item>
          <el-form-item label="置顶">
            <el-switch v-model="form.isTop" />
          </el-form-item>

          <el-form-item label="正文" style="width: 100%;">
            <div id="vditor" style="width: 100%;"></div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="saving" @click="submit">{{ form.id ? '保存文章' : '发布文章' }}</el-button>
            <el-button :loading="saving" @click="createDuplicateFromForm">新建文章</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import MarkdownIt from 'markdown-it'
import { upsertToc } from '../utils/markdownToc'
import AdminTabs from '../components/AdminTabs.vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import {
  adminCreatePost,
  adminDeletePost,
  adminFetchPost,
  adminFetchPosts,
  adminUpdatePost,
  clearAdminEntryKey,
  clearAdminToken
} from '../api/admin'

const router = useRouter()
const posts = ref([])
const loadingList = ref(false)
const saving = ref(false)
const tagsInput = ref('')
const editorTab = ref('edit')
const contentInputRef = ref(null)
const md = new MarkdownIt({ html: true, linkify: true, typographer: true })

const form = reactive({
  id: null,
  title: '',
  summary: '',
  cover: '',
  date: '',
  category: '',
  isTop: true,
  content: ''
})

let vditorInstance = null

const loadList = async () => {
  loadingList.value = true
  try {
    const data = await adminFetchPosts(1, 100)
    posts.value = data.list
  } finally {
    loadingList.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.summary = ''
  form.cover = ''
  form.date = ''
  form.category = ''
  form.isTop = true
  form.content = ''
  tagsInput.value = ''
  if (vditorInstance) {
    vditorInstance.setValue('')
  }
}

const edit = async (id) => {
  const data = await adminFetchPost(id)
  form.id = data.id
  form.title = data.title
  form.summary = data.summary
  form.cover = data.cover
  form.date = data.date
  form.category = data.category
  form.isTop = data.isTop
  form.content = data.content
  tagsInput.value = (data.tags || []).join(', ')
  if (vditorInstance) {
    vditorInstance.setValue(data.content || '')
  }
}

const buildPayload = () => ({
  title: form.title,
  summary: form.summary,
  cover: form.cover,
  date: form.date,
  category: form.category,
  isTop: form.isTop,
  content: form.content,
  tags: tagsInput.value.split(',').map(v => v.trim()).filter(Boolean)
})

const submit = async () => {
  if (!form.title || !form.summary || !form.cover || !form.date || !form.category || !form.content) {
    ElMessage.error('请先塆完整文章信息')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await adminUpdatePost(form.id, buildPayload())
      ElMessage.success('文章更新成功')
    } else {
      await adminCreatePost(buildPayload())
      ElMessage.success('文章发布成功')
    }
    await loadList()
    resetForm()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    saving.value = false
  }
}

const createDuplicateFromForm = async () => {
  if (!form.title || !form.summary || !form.cover || !form.date || !form.category || !form.content) {
    ElMessage.error('请先塆完整文章信息')
    return
  }
  saving.value = true
  try {
    const payload = buildPayload()
    payload.title = `${payload.title}（副朼`
    await adminCreatePost(payload)
    ElMessage.success('文章剜创建成功')
    await loadList()
  } catch (error) {
    ElMessage.error(error.message || '创建剜失败')
  } finally {
    saving.value = false
  }
}

const remove = async (id) => {
  await ElMessageBox.confirm('确删除这篇文章吗？', '提示', { type: 'warning' })
  await adminDeletePost(id)
  ElMessage.success('删除成功')
  await loadList()
  if (form.id === id) resetForm()
}

const duplicate = async (id) => {
  try {
    const data = await adminFetchPost(id)
    await adminCreatePost({
      title: `${data.title}（副朼`,
      summary: data.summary,
      cover: data.cover,
      date: data.date,
      category: data.category,
      isTop: data.isTop,
      content: data.content,
      tags: data.tags || []
    })
    ElMessage.success('文章剜创建成功')
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

onMounted(() => {
  loadList()
  vditorInstance = new Vditor('vditor', {
    height: 600,
    mode: 'wysiwyg',
    cache: {
      enable: false,
    },
    value: form.content,
    input: (val) => {
      form.content = val
    }
  })
})
</script>

<style scoped lang="scss">
.admin-page { padding: 24px; max-width: 1700px; margin: 70px auto 40px; }
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 22px;
  margin-bottom: 16px;
  h2 { margin: 0; }
  .actions { display: flex; gap: 10px; }
}
.admin-main { display: grid; grid-template-columns: 1fr 1.3fr; gap: 16px; }
.left, .right { padding: 14px; background: var(--blog-card-bg); }
.toolbar { display: flex; flex-wrap: wrap; gap: 8px; }
.editor-tabs { width: 100%; }
.preview-layout { display: grid; grid-template-columns: 260px 1fr; gap: 12px; min-height: 420px; }
.preview-toc {
  border: 1px solid rgba(120,120,120,0.2);
  border-radius: 10px;
  padding: 10px;
  h4 { margin: 0 0 8px; }
  ul { margin: 0; padding-left: 12px; list-style: none; }
  li { margin: 6px 0; }
  .level-2 { padding-left: 12px; }
  .level-3 { padding-left: 24px; }
}
.preview-body {
  border: 1px solid rgba(120,120,120,0.2);
  border-radius: 10px;
  padding: 14px;
  overflow: auto;
  max-height: 560px;
}
.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3) { margin-top: 18px; }
.markdown-body :deep(pre) {
  background: #1f2937;
  color: #e5e7eb;
  padding: 12px;
  border-radius: 8px;
  overflow: auto;
}
@media (max-width: 1200px) {
  .admin-main { grid-template-columns: 1fr; }
  .preview-layout { grid-template-columns: 1fr; }
}
</style>

