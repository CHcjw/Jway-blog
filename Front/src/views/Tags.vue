<template>
  <div class="page-container">
    <section class="page-hero">
      <h1 class="neon-text">标签</h1>
    </section>
    <main class="page-content">
      <div class="tags-card card-style glass-effect">
        <div class="tags-title"><i class="bi bi-tags"></i> 标签 - {{ totalTags }}</div>

        <div class="tags-cloud-large" :key="currentPage">
          <el-tag
            v-for="tag in pagedTags"
            :key="tag.name"
            class="tag-item-lg"
            :style="getTagStyle(tag)"
            @click="goToTagResult(tag)"
          >
            {{ tag.name }}
          </el-tag>
        </div>

        <div v-if="totalTags > pageSize" class="pagination-wrapper">
          <el-pagination
            background
            layout="prev, pager, next"
            :current-page="currentPage"
            :total="totalTags"
            :page-size="pageSize"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '../store/posts'

const router = useRouter()
const postStore = usePostStore()
const currentPage = ref(1)
const pageSize = 30
const colorPalette = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#cc6699', '#36cfc9', '#722ed1']

const filteredTags = computed(() =>
  postStore.tags.filter((tag) => Number(tag?.count ?? 0) > 0)
)

const totalTags = computed(() => filteredTags.value.length)
const pagedTags = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredTags.value.slice(start, start + pageSize)
})

const hashCode = (text = '') => {
  let hash = 0
  for (let i = 0; i < text.length; i += 1) {
    hash = (hash << 5) - hash + text.charCodeAt(i)
    hash |= 0
  }
  return Math.abs(hash)
}

const getTagStyle = (tag) => {
  const seed = hashCode(tag.name)
  const fontSize = 0.95 + ((seed % 95) / 100)
  const color = colorPalette[seed % colorPalette.length]
  return {
    fontSize: `${fontSize.toFixed(2)}rem`,
    color
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  window.scrollTo({ top: 260, behavior: 'smooth' })
}

const goToTagResult = (tag) => {
  router.push(`/tags/${tag.name}`)
}

onMounted(async () => {
  await postStore.fetchTags()
  currentPage.value = 1
})
</script>

<style lang="scss" scoped>
.page-hero { height: 350px; display: flex; justify-content: center; align-items: center; }
.page-content { max-width: 1300px; margin: -50px auto 80px; padding: 0 20px; }
.tags-card { padding: 60px 40px; text-align: center; background: var(--blog-card-bg); }
.tags-title { font-size: 1.6rem; font-weight: bold; margin-bottom: 40px; border-bottom: 1px dashed rgba(0,0,0,0.1); padding-bottom: 25px; color: var(--blog-text); }

.tags-cloud-large {
  height: 250px;
  overflow: hidden;
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  justify-content: center;
  gap: 18px 20px;
  transition: none;
}

.tag-item-lg {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 12px 20px;
  height: auto;
  border: none;
  background: transparent;
  white-space: nowrap;
  &:hover { transform: scale(1.25) rotate(2deg); filter: brightness(1.2); }
}

.pagination-wrapper {
  width: 100%;
  margin-top: 36px;
  display: flex;
  justify-content: center;

  :deep(.el-pagination) {
    --el-pagination-bg-color: rgba(255, 255, 255, 0.1);
    --el-pagination-button-disabled-bg-color: rgba(255, 255, 255, 0.05);
    --el-pagination-hover-color: var(--primary-color);
  }

  :deep(.el-pagination.is-background .el-pager li) {
    background: var(--blog-card-bg);
    border: 1px solid rgba(150, 150, 150, 0.2);
    border-radius: 12px;
    font-weight: 800;
    transition: all 0.3s;
    backdrop-filter: blur(10px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
    color: var(--blog-text-secondary);
  }

  :deep(.el-pagination.is-background .el-pager li:hover) {
    transform: translateY(-3px);
    border-color: var(--primary-color);
    color: var(--primary-color);
    box-shadow: 0 6px 15px rgba(73, 177, 245, 0.2);
  }

  :deep(.el-pagination.is-background .el-pager li.is-active) {
    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
    color: white;
    border: none;
    box-shadow: 0 6px 15px rgba(244, 63, 94, 0.3);
  }

  :deep(.el-pagination.is-background .btn-next),
  :deep(.el-pagination.is-background .btn-prev) {
    background: var(--blog-card-bg);
    border-radius: 12px;
    border: 1px solid rgba(150, 150, 150, 0.2);
    transition: all 0.3s;
    backdrop-filter: blur(10px);
    color: var(--blog-text-secondary);
  }

  :deep(.el-pagination.is-background .btn-next:hover:not(:disabled)),
  :deep(.el-pagination.is-background .btn-prev:hover:not(:disabled)) {
    transform: translateY(-3px);
    border-color: var(--primary-color);
    color: var(--primary-color);
  }
}
</style>
