<template>
  <div class="page-container">
    <section class="page-hero">
      <h1 class="neon-text">分类</h1>
    </section>
    <main class="page-content">
      <div class="cat-card card-style glass-effect">
        <div class="cat-title"><i class="bi bi-grid"></i> 全部分类 - {{ postStore.categories.length }}</div>
        <div class="cat-list">
          <div 
            v-for="cat in postStore.categories" 
            :key="cat.name" 
            class="cat-item"
            @click="goToCategoryResult(cat)"
          >
            <span class="cat-name">{{ cat.name }}</span>
            <span class="cat-badge">{{ cat.count }} 篇文章</span>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '../store/posts'

const router = useRouter()
const postStore = usePostStore()

const goToCategoryResult = (cat) => {
  router.push(`/categories/${cat.name}`)
}

onMounted(async () => {
  await postStore.fetchCategories()
})
</script>

<style lang="scss" scoped>
.page-hero { height: 350px; display: flex; justify-content: center; align-items: center; }
.page-content { max-width: 800px; margin: -40px auto 100px; padding: 0 20px; }
.cat-card { padding: 60px 50px; background: var(--blog-card-bg); }
.cat-title { font-size: 1.6rem; font-weight: bold; margin-bottom: 40px; text-align: center; border-bottom: 1px dashed rgba(0,0,0,0.1); padding-bottom: 25px; color: var(--blog-text); }

.cat-list { 
  display: flex; flex-direction: column; gap: 15px; 
}

.cat-item { 
  display: flex; justify-content: space-between; align-items: center;
  padding: 18px 30px; border-radius: 12px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
  cursor: pointer; background: rgba(0,0,0,0.02);
  border: 1px solid transparent;

  &:hover { 
    background: rgba(64, 158, 255, 0.1); 
    border-color: var(--primary-color);
    transform: translateX(10px);
    .cat-name { color: var(--primary-color); }
    .cat-badge { background: var(--primary-color); color: white; }
  }

  .cat-name { font-weight: 600; font-size: 1.1rem; transition: color 0.3s; color: var(--blog-text); }
  .cat-badge { 
    background: #f0f2f5; padding: 4px 15px; border-radius: 20px; 
    font-size: 0.85rem; color: #999; transition: all 0.3s;
  }
}

html.dark .cat-item { background: rgba(255,255,255,0.03); }
</style>
