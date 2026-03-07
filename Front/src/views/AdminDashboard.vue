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

          <el-form-item label="写作工具">
            <div class="toolbar">
              <el-button size="small" @click="insertSnippet('# 主标题\n')">H1</el-button>
              <el-button size="small" @click="insertSnippet('## 剠题\n')">H2</el-button>
              <el-button size="small" @click="insertSnippet('### 小标题\n')">H3</el-button>
              <el-button size="small" @click="insertSnippet('**加粗文本**')">粗体</el-button>
              <el-button size="small" @click="insertSnippet('<span style=&quot;font-size: 18px;&quot;>大号文本</span>')">大号?</el-button>
              <el-button size="small" @click="insertSnippet('<span style=&quot;font-size: 13px;&quot;>小号文本</span>')">小号?</el-button>
              <el-button size="small" @click="insertSnippet('> 引用文本\n')">引用</el-button>
              <el-button size="small" @click="insertSnippet('```java\n// code\n```\n')">代码</el-button>
              <el-button size="small" @click="insertImage">插图</el-button>
              <el-button size="small" @click="generateToc">目录模板</el-button>
            </div>
          </el-form-item>

          <el-form-item label="正文">
            <el-tabs v-model="editorTab" class="editor-tabs">
              <el-tab-pane label="编辑" name="edit">
                <el-input
                  ref="contentInputRef"
                  v-model="form.content"
                  type="textarea"
                  :rows="16"
                  placeholder="Markdown 内容"
                />
              </el-tab-pane>
              <el-tab-pane label="预览" name="preview">
                <div class="preview-layout">
                  <div class="preview-toc">
                    <h4>目录</h4>
                    <ul>
                      <li v-for="item in tocList" :key="item.id" :class="`level-${item.level}`">{{ item.title }}</li>
                    </ul>
                  </div>
                  <div class="preview-body markdown-body" v-html="previewHtml"></div>
                </div>
              </el-tab-pane>
            </el-tabs>
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

const previewHtml = computed(() => md.render(form.content || ''))
const tocList = computed(() => {
  const list = []
  const reg = /^(#{1,3})\s+(.+)$/gm
  let m
  while ((m = reg.exec(form.content || '')) !== null) {
    list.push({
      level: m[1].length,
      title: m[2].trim(),
      id: m[2].trim().toLowerCase().replace(/[^\w\u4e00-\u9fa5]/g, '-')
    })
  }
  return list
})

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
  editorTab.value = 'edit'
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
  editorTab.value = 'edit'
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

const insertSnippet = async (text) => {
  await nextTick()
  const textarea = contentInputRef.value?.textarea
  if (!textarea) {
    form.content += (form.content ? '\n' : '') + text
    return
  }
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const before = form.content.slice(0, start)
  const after = form.content.slice(end)
  form.content = before + text + after
  await nextTick()
  textarea.focus()
  const pos = start + text.length
  textarea.setSelectionRange(pos, pos)
}

const insertImage = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入图?URL', '插入图片', {
      inputPlaceholder: 'https://example.com/a.png'
    })
    if (!value) return
    await insertSnippet(`![图片描述](${value})`)
  } catch (_) {
    // canceled
  }
}

const generateToc = async () => {
  if (!form.content || !form.content.trim()) {
    ElMessage.warning('Please input content first')
    return
  }
  const nextContent = upsertToc(form.content)
  if (nextContent === form.content) {
    ElMessage.warning('No headings found (# / ## / ###)')
    return
  }
  form.content = nextContent
  ElMessage.success('TOC updated')
}

onMounted(loadList)
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

