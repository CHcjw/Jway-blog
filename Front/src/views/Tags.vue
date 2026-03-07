<template>
  <div class="page-container">
    <section class="page-hero">
      <h1 class="neon-text">标签</h1>
    </section>
    <main class="page-content">
      <div class="tags-card card-style glass-effect">
        <div class="tags-title"><i class="bi bi-tags"></i> 标签 - {{ postStore.tags.length }}</div>
        <div class="tags-cloud-large">
          <el-tag 
            v-for="tag in postStore.tags" 
            :key="tag.name" 
            class="tag-item-lg"
            :style="{ fontSize: getRandomSize(), color: getRandomColor() }"
            @click="goToTagResult(tag)"
          >
            {{ tag.name }}
          </el-tag>
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

const getRandomSize = () => (Math.random() * (1.8 - 0.9) + 0.9) + 'rem'
const getRandomColor = () => {
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#cc6699']
  return colors[Math.floor(Math.random() * colors.length)]
}

const goToTagResult = (tag) => {
  router.push(`/tags/${tag.name}`)
}

onMounted(async () => {
  await postStore.fetchTags()
})
</script>

<style lang="scss" scoped>
.page-hero { height: 350px; display: flex; justify-content: center; align-items: center; }
.page-content { max-width: 1000px; margin: -50px auto 80px; padding: 0 20px; }
.tags-card { padding: 60px 40px; text-align: center; background: var(--blog-card-bg); }
.tags-title { font-size: 1.6rem; font-weight: bold; margin-bottom: 40px; border-bottom: 1px dashed rgba(0,0,0,0.1); padding-bottom: 25px; color: var(--blog-text); }
.tags-cloud-large { display: flex; flex-wrap: wrap; justify-content: center; gap: 20px; }
.tag-item-lg { 
  cursor: pointer; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
  padding: 15px 25px; height: auto; border: none; background: transparent;
  &:hover { transform: scale(1.25) rotate(2deg); filter: brightness(1.2); }
}
</style>
