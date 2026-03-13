<template>
  <div class="category-container card-style glass-effect scroll-reveal">
    <div class="category-grid">
      <div 
        v-for="(cat, index) in categories.slice(0, 6)" 
        :key="cat.id" 
        class="category-item"
        :style="{ '--reveal-delay': `${index * 40}ms` }"
        @click="goToCategory(cat.name)"
      >
        <div class="cat-left">
          <span class="cat-emoji">{{ getEmoji(cat.name) }}</span>
          <span class="cat-text">U7の{{ cat.name }} ({{ cat.count || 0 }})</span>
        </div>
        <div class="cat-right">
          <i class="bi bi-arrow-right-circle-fill"></i>
        </div>
      </div>
    </div>
    
    <div v-if="categories.length > 0" class="view-more-container">
      <div class="view-more-btn" @click="router.push('/categories')">
        查看更多...
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  categories: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()

const goToCategory = (name) => {
  router.push(`/categories?category=${name}`)
}

const getEmoji = (name) => {
  if (name.includes('学习')) return '🍡'
  if (name.includes('Life') || name.includes('生活')) return '🍼'
  if (name.includes('项目')) return '🍉'
  if (name.includes('总结') || name.includes('工作')) return '🍧'
  if (name.includes('算法')) return '🍟'
  if (name.includes('面试')) return '🍥'
  if (name.includes('前端')) return '👨‍💻'
  if (name.includes('后端')) return '⚙️'
  if (name.includes('工具')) return '🔧'
  return '📂'
}
</script>

<style lang="scss" scoped>
.category-container {
  margin-bottom: 38px;
  padding: 15px 40px;
  border-radius: 35px;
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.category-item {
  background: rgba(0, 0, 0, 0.04);
  padding: 18px 25px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;

  .cat-left {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .cat-emoji {
      font-size: 1.2rem;
    }
    
    .cat-text {
      font-size: 0.95rem;
      font-weight: 500;
      color: #333;
      font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
    }
  }

  .cat-right {
    font-size: 1.2rem;
    color: #000;
    display: flex;
    align-items: center;
    transition: all 0.3s;
  }

  &:hover {
    background: #73c9ff;
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(115, 201, 255, 0.3);
    
    .cat-text {
      color: #fff;
    }
    
    .cat-right {
      color: #fff;
      transform: translateX(3px);
    }
  }
}

.view-more-container {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.view-more-btn {
  font-size: 0.9rem;
  color: #888;
  cursor: pointer;
  padding: 5px 15px;
  border-radius: 20px;
  transition: all 0.3s;
  position: relative;
  
  &:hover {
    color: #49b1f5;
    background: rgba(73, 177, 245, 0.08);
  }
}

/* Dark mode adjust if global classes available */
@at-root html.dark {
  .category-container {
    background: rgba(30, 41, 59, 0.5) !important;
    border: 1px solid rgba(255, 255, 255, 0.1) !important;
  }
  .category-item {
    background: rgba(255, 255, 255, 0.05) !important;
    .cat-text { color: #e2e8f0 !important; }
    .cat-right { color: #e2e8f0 !important; }
  }
}

@media (max-width: 1200px) {
  .category-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .category-container {
    padding: 25px;
    border-radius: 25px;
  }
  .category-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  .category-item {
    padding: 15px 20px;
  }
}
</style>
