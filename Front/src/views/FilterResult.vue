<template>
  <div class="page-container">
    <section class="page-hero">
      <h1 class="neon-text">{{ filterTitle }}</h1>
    </section>
    
    <main class="main-content">
      <div v-if="filteredPosts.length > 0">
        <div class="article-grid">
          <div 
            v-for="post in filteredPosts" 
            :key="post.id" 
            class="article-item card-style"
            @click="router.push(`/post/${post.id}`)"
          >
            <div class="article-thumb" :style="{ backgroundImage: `url(${post.cover})` }">
              <div class="post-meta-top"><i class="bi bi-calendar3"></i> {{ post.date }}</div>
            </div>
            <div class="article-body">
              <h3>{{ post.title }}</h3>
              <p class="summary">{{ post.summary }}</p>
              <div class="post-tags">
                <span v-for="tag in post.tags" :key="tag" class="tag-link">
                  <i class="bi bi-hash"></i>{{ tag }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="total > 0" class="pagination-wrapper">
          <el-pagination 
            background 
            layout="prev, pager, next" 
            :total="total" 
            :page-size="pageSize" 
            v-model:current-page="currentPage" 
            @current-change="handlePageChange" 
          />
        </div>
      </div>
      <div v-else class="no-result card-style glass-effect">
        <i class="bi bi-emoji-frown"></i>
        <p>找不到相关的文章呢...</p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '../store/posts'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const filteredPosts = ref([])
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)

const isTag = computed(() => route.name === 'TagResult')
const filterName = computed(() => route.params.name)
const filterTitle = computed(() => (isTag.value ? '标签 - ' : '分类 - ') + filterName.value)

const loadFilteredPosts = async () => {
  if (!filterName.value) return
  const params = isTag.value ? { tag: filterName.value } : { category: filterName.value }
  params.page = currentPage.value
  params.size = pageSize.value
  const pageData = await postStore.searchPosts(params)
  filteredPosts.value = pageData.list
  total.value = pageData.total || 0
}

const handlePageChange = (val) => {
  currentPage.value = val
  loadFilteredPosts()
  window.scrollTo({ top: 300, behavior: 'smooth' })
}

watch([isTag, filterName], () => {
  currentPage.value = 1
  loadFilteredPosts()
}, { immediate: true })
</script>

<style lang="scss" scoped>
.page-hero { height: 350px; display: flex; justify-content: center; align-items: center; }
.main-content { max-width: 1300px; margin: -40px auto 100px; padding: 0 40px; }

.article-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 25px;
  .article-item {
    cursor: pointer; display: flex; flex-direction: column; height: 420px;
    .article-thumb { height: 190px; background-size: cover; background-position: center; position: relative; .post-meta-top { position: absolute; bottom: 12px; left: 12px; background: rgba(0,0,0,0.6); padding: 3px 10px; border-radius: 20px; font-size: 0.75rem; color: white; } }
    .article-body { padding: 20px; flex: 1; display: flex; flex-direction: column; h3 { margin: 0 0 10px; font-size: 1.15rem; font-weight: 700; transition: color 0.3s; } .summary { font-size: 0.9rem; color: var(--blog-text-secondary); line-height: 1.6; margin-bottom: 15px; flex-grow: 1; overflow: hidden; } .post-tags { display: flex; flex-wrap: wrap; gap: 8px; .tag-link { font-size: 0.8rem; color: #999; i { margin-right: 3px; } } } }
    &:hover h3 { color: var(--primary-color); }
  }
}

.no-result { padding: 80px; text-align: center; background: var(--blog-card-bg); i { font-size: 3rem; color: #999; margin-bottom: 20px; display: block; } p { color: #888; font-size: 1.1rem; } }

.pagination-wrapper { 
  width: 100%;
  margin-top: 60px; 
  margin-bottom: 50px;
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

@media (max-width: 1100px) { .article-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .article-grid { grid-template-columns: 1fr; } }
</style>
