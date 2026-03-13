<template>
  <div class="recommend-carousel card-style scroll-reveal">
    <el-carousel v-if="posts && posts.length" height="300px" indicator-position="outside" arrow="hover">
      <el-carousel-item v-for="post in posts" :key="post.id">
        <div class="carousel-item-content" @click="goToPost(post.id)">
          <div class="post-cover" :style="{ backgroundImage: `url(${post.cover})` }"></div>
          <div class="post-info">
            <span class="recommend-tag"><i class="bi bi-star-fill"></i> 推荐文章</span>
            <div class="post-date">{{ post.date }}</div>
            <h2 class="post-title">{{ post.title }}</h2>
            <p class="post-summary">{{ truncate(post.summary, 80) }}</p>
            <div class="post-footer">
              <span class="read-more">阅读更多 <i class="bi bi-arrow-right-short"></i></span>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    <div v-else class="empty-recommend">
      <el-skeleton :rows="5" animated />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  posts: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()

const goToPost = (id) => {
  router.push(`/post/${id}`)
}

const truncate = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}
</script>

<style lang="scss" scoped>
element.style {
    height: 280px;
}

.recommend-carousel {
  margin-bottom: 30px;
  border-radius: 24px;
  overflow: hidden;
  background: var(--blog-card-bg);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.05);

  :deep(.el-carousel__indicators--outside) {
    margin-top: -30px;
    z-index: 10;
  }
  
  :deep(.el-carousel__indicator--horizontal .el-carousel__button) {
    width: 30px;
    height: 4px;
    border-radius: 2px;
    background-color: var(--primary-color);
  }
}

.el-carousel__item {
  height: 90%;
}

.carousel-item-content {
  display: flex;
  height: calc(100% - 20px);
  margin: 10px;
  cursor: pointer;
  position: relative;
  background: var(--blog-card-bg);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid rgba(150, 150, 150, 0.1);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  
  &:hover {
    .post-cover {
      transform: scale(1.08) rotate(1deg);
    }
    .post-title {
      color: var(--primary-color);
    }
    box-shadow: 0 15px 45px rgba(0, 0, 0, 0.12);
  }
}

.post-cover {
  width: 40%;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: transform 1200ms cubic-bezier(0.25, 0.46, 0.45, 0.94);
  border-right: 1px solid rgba(150, 150, 150, 0.1);
}

@media (max-width: 768px) {
    .post-info {
        padding: 25px;
    }
}

.post-info {
  flex: 1;
  padding: 10px 45px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  z-index: 1;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.02) 0%, transparent 100%);
  backdrop-filter: blur(5px);

  .recommend-tag {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-size: 0.8rem;
    font-weight: 800;
    color: #fff;
    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
    padding: 4px 12px;
    border-radius: 8px;
    margin-bottom: 20px;
    width: fit-content;
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
    
    i { font-size: 0.9rem; }
  }

  .post-date {
    font-size: 0.85rem;
    color: var(--blog-text-secondary);
    margin-bottom: 8px;
  }

  .post-title {
    font-size: 1.8rem;
    font-weight: 900;
    color: var(--blog-text);
    margin: 0 0 15px;
    line-height: 1.3;
    font-family: 'Outfit', sans-serif;
    transition: color 0.3s;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .post-summary {
    font-size: 1rem;
    color: var(--blog-text-secondary);
    line-height: 1.6;
    margin-bottom: 25px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .post-footer {
    display: flex;
    align-items: center;

    .read-more {
      font-size: 0.95rem;
      font-weight: 700;
      color: var(--primary-color);
      display: flex;
      align-items: center;
      gap: 4px;
      transition: gap 0.3s;
      
      &:hover {
        gap: 8px;
      }
    }
  }
}

.empty-recommend {
  padding: 40px;
}

@media (max-width: 768px) {
  .post-cover {
    display: none;
  }
  .post-info {
    padding: 30px;
    .post-title { font-size: 1.4rem; }
  }
}
</style>
