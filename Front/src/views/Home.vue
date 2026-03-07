<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-container">
        <h1 class="neon-text">Jway's Blog</h1>
        <div class="typewriter-box">
          <span class="neon-glow-strong">{{ currentSentence }}</span>
          <span class="typewriter-cursor"></span>
        </div>
      </div>
      <div class="down-arrow-fixed" @click="scrollToContent">
        <i class="bi bi-chevron-double-down"></i>
      </div>
    </section>

    <!-- Main Content -->
    <main class="main-content">
      <el-row :gutter="30">
        <!-- Left Column: Articles -->
        <el-col :xs="24" :sm="24" :md="18">
          <div class="article-grid">
            <div 
              v-for="(post, index) in displayedPosts" 
              :key="post.id" 
              class="envelope-card card-style reveal-item"
              :style="{ animationDelay: `${index * 0.1}s` }"
              @click="goToPost(post.id)"
            >
              <div class="envelope-top">
                <div class="article-thumb" :style="{ backgroundImage: `url(${post.cover}), url(https://images.unsplash.com/photo-1614850523296-d8c1af93d400?q=80&w=800)` }"></div>
                <!-- Fixed Absolutely Centered Overlay -->
                <div class="envelope-overlay">
                  <div class="envelope-summary-content">
                    <span class="quote">「</span>
                    {{ post.summary }}
                    <span class="quote">」</span>
                  </div>
                </div>
              </div>

              <div class="envelope-body">
                <h3 class="post-title">{{ post.title }}</h3>
                <div class="post-meta-info">
                  <span v-if="post.isTop" class="meta-item top-label"><i class="bi bi-pin-angle-fill"></i> 置顶</span>
                  <span v-if="post.isTop" class="divider">|</span>
                  <span class="meta-item"><i class="bi bi-calendar-event"></i> {{ post.date }}</span>
                </div>
                <div class="post-tags-list">
                  <span v-for="tag in post.tags" :key="tag" class="mini-tag">
                    <i class="bi bi-tag-fill"></i> {{ tag }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Centered Pagination Wrapper -->
          <div class="pagination-wrapper">
            <el-pagination 
              background 
              layout="prev, pager, next" 
              :current-page="currentPage"
              :total="postStore.totalPosts" 
              :page-size="9" 
              @current-change="handlePageChange" 
            />
          </div>
        </el-col>

        <!-- Right Column: Sidebar -->
        <el-col :xs="24" :sm="24" :md="6">
          <aside class="sidebar">
            <!-- Profile -->
            <div class="sidebar-box profile-card card-style">
              <div class="avatar-wrapper">
                <img :src="avatarImage" alt="avatar" class="avatar avatar-rotate" />
              </div>
              <h3 class="fancy-text">Jway</h3>
              <p class="motto">认真摸鱼中🐟</p>
              <div class="social-icons">
                <a href="https://github.com/CHcjw" target="_blank" rel="noopener noreferrer" style="color: inherit;">
                  <i class="bi bi-github"></i>
                </a>
                <el-popover placement="bottom" :width="200" trigger="click">
                  <template #reference>
                    <i class="bi bi-wechat"></i>
                  </template>
                  <img src="https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/logo/wechat.png" style="width: 100%; height: auto; border-radius: 8px;" alt="WeChat QR" />
                </el-popover>
                <el-popover placement="bottom" :width="200" trigger="click">
                  <template #reference>
                    <i class="bi bi-tencent-qq"></i>
                  </template>
                  <img src="https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/logo/qq.png" style="width: 100%; height: auto; border-radius: 8px;" alt="QQ QR" />
                </el-popover>
              </div>
            </div>

            <!-- Announcement -->
            <div class="sidebar-box announcement card-style">
              <div class="box-title"><i class="bi bi-megaphone"></i> 公告栏</div>
              <p class="ann-text">欢迎来到我的全新博客！这里记录了我的技术成长与生活点滴。🎉</p>
            </div>

            <!-- Latest Posts -->
            <div class="sidebar-box latest-posts card-style">
              <div class="box-title"><i class="bi bi-clock-history"></i> 最新文章</div>
              <div class="latest-list">
                <div v-for="p in postStore.latestPosts" :key="p.id" class="latest-item" @click="goToPost(p.id)">
                  <div class="item-img">
                    <img :src="p.cover" alt="thumb" />
                  </div>
                  <div class="item-info">
                    <div class="item-title">{{ p.title }}</div>
                    <div class="item-date">{{ p.date }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Tags -->
            <div class="sidebar-box tags-cloud card-style">
              <div class="box-title"><i class="bi bi-tags"></i> 标签云</div>
              <div class="tag-cloud-wrapper">
                <el-tag v-for="tag in postStore.tagNames.slice(0, 15)" :key="tag" 
                        size="small" 
                        class="tag-node clickable-tag" 
                        type="info" 
                        @click="router.push(`/tags?tag=${tag}`)">
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </aside>
        </el-col>
      </el-row>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '../store/posts'
import { getOssUrl } from '../config/oss'

const router = useRouter()
const postStore = usePostStore()
const avatarImage = getOssUrl('avatar', 'avatar.jpg')

const sentences = [
  "Stay hungry, stay foolish. Exploring the world of code.",
  "The only way to do great work is to love what you do.",
  "Architecture is the art of how to waste space beautifully."
]
const currentSentence = ref('')
let sentenceIndex = 0

const typeWriter = () => {
  const sentence = sentences[sentenceIndex]
  let charIndex = 0
  currentSentence.value = ''
  const timer = setInterval(() => {
    if (charIndex < sentence.length) {
      currentSentence.value += sentence[charIndex++]
    } else {
      clearInterval(timer)
      setTimeout(() => {
        sentenceIndex = (sentenceIndex + 1) % sentences.length
        typeWriter()
      }, 3000)
    }
  }, 80)
}

const currentPage = ref(1)
const displayedPosts = computed(() => postStore.posts)

const handlePageChange = async (val) => {
  currentPage.value = val
  await postStore.fetchPosts(val, 9)
  window.scrollTo({ top: window.innerHeight - 60, behavior: 'smooth' })
}

const scrollToContent = () => window.scrollTo({ top: window.innerHeight, behavior: 'smooth' })
const goToPost = (id) => router.push(`/post/${id}`)
onMounted(async () => {
  typeWriter()
  await Promise.all([
    postStore.fetchPosts(1, 9),
    postStore.fetchLatest(5),
    postStore.fetchTags()
  ])
})
</script>

<style lang="scss" scoped>
.hero-section {
  height: 100vh; display: flex; flex-direction: column; justify-content: center; align-items: center; position: relative; text-align: center;
  .hero-container { 
    z-index: 1; 
    padding: 0 30px;
    h1 { font-size: clamp(3rem, 10vw, 6rem); margin-bottom: 24px; font-family: 'Outfit', sans-serif; line-height: 1.1; } 
    .typewriter-box { 
      font-size: clamp(0.9rem, 4.5vw, 1.9rem); 
      min-height: 1.6em; 
      font-family: 'Pacifico', cursive; 
      width: 100%;
      max-width: 900px;
      margin: 0 auto;
      font-weight: 400;
      letter-spacing: 1px;
      
      .neon-glow-strong {
        color: rgba(255, 255, 255, 0.95);
        animation: subtle-neon-cycle 3s ease-in-out infinite alternate;
      }
    } 
  }
  .down-arrow-fixed { position: absolute; bottom: 35px; left: 50%; transform: translateX(-50%); font-size: 2.5rem; color: white; cursor: pointer; animation: bounce 2s infinite; z-index: 5; }
}

@keyframes subtle-neon-cycle {
  0% { text-shadow: 0 0 10px rgba(99, 102, 241, 0.6), 0 0 20px rgba(99, 102, 241, 0.4), 0 0 30px rgba(99, 102, 241, 0.2); color: rgba(255, 255, 255, 0.95); }
  50% { text-shadow: 0 0 15px rgba(73, 177, 245, 0.8), 0 0 30px rgba(73, 177, 245, 0.5), 0 0 45px rgba(73, 177, 245, 0.3); color: #fff; }
  100% { text-shadow: 0 0 10px rgba(244, 63, 94, 0.6), 0 0 20px rgba(244, 63, 94, 0.4), 0 0 30px rgba(244, 63, 94, 0.2); color: rgba(255, 255, 255, 0.95); }
}

@keyframes bounce { 0%, 20%, 50%, 80%, 100% {transform: translate(-50%, 0);} 40% {transform: translate(-50%, -20px);} }

.main-content { max-width: 1440px; margin: 0px auto; padding: 80px 40px; }

.article-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px; }

/* Envelope Card Design */
.envelope-card {
  cursor: pointer; display: flex; flex-direction: column; height: 480px;
  background: var(--blog-card-bg); overflow: hidden;
  
  .envelope-top {
    height: 240px; position: relative; overflow: hidden;
    clip-path: polygon(0 0, 100% 0, 100% 88%, 50% 100%, 0 88%);
    z-index: 2;
    
    .article-thumb {
      width: 100%; height: 100%; background-size: cover; background-position: center;
      transition: transform 1.2s cubic-bezier(0.2, 1, 0.3, 1);
    }

    .envelope-overlay {
      position: absolute; top: 0; left: 0; width: 100%; height: 100%;
      background: rgba(15, 23, 42, 0.5);
      backdrop-filter: blur(2px);
      display: flex; align-items: center; justify-content: center;
      text-align: center;
      opacity: 0;
      transition: opacity 0.4s ease;
      
      .envelope-summary-content {
        color: white; font-size: 1.05rem; line-height: 1.6; font-weight: 500;
        transform: translateY(20px);
        transition: transform 0.4s cubic-bezier(0.2, 1, 0.3, 1);
        .quote { font-size: 1.5rem; color: var(--primary-color); font-weight: 900; }
      }
    }
  }

  .envelope-body {
    padding: 30px; flex: 1; display: flex; flex-direction: column; text-align: center;
    .post-title { margin: 0 0 15px; font-size: 1.3rem; font-weight: 800; color: var(--blog-text); font-family: 'Outfit', sans-serif; transition: color 0.3s; }
    .post-meta-info { font-size: 0.85rem; color: var(--blog-text-secondary); margin-bottom: 20px; display: flex; justify-content: center; gap: 15px; .top-label { color: var(--accent-color); font-weight: 700; } }
    .post-tags-list { display: flex; flex-wrap: wrap; justify-content: center; gap: 10px; .mini-tag { font-size: 0.75rem; color: var(--blog-text-secondary); background: rgba(99, 102, 241, 0.05); padding: 4px 10px; border-radius: 20px; } }
  }

  &:hover {
    .article-thumb { transform: scale(1.1); }
    .envelope-overlay { opacity: 1; .envelope-summary-content { transform: translateY(0); } }
    .post-title { color: var(--primary-color); }
  }
}

/* Pagination Centering and UI */
.pagination-wrapper { 
  width: 100%;
  margin-top: 60px; 
  margin-bottom: 50px; /* added spacing below pagination before mobile sidebar */
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

  .sidebar {
  .sidebar-box { padding: 30px; margin-bottom: 30px; user-select: none; }
  .profile-card {
    text-align: center;
    .avatar-wrapper { 
      margin-bottom: 25px; 
      .avatar { 
        width: 120px; height: 120px; border-radius: 50%; 
        border: 4px solid rgba(255,255,255,0.1); 
        box-shadow: 0 0 20px rgba(99, 102, 241, 0.2);
        transition: transform 0.8s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.4s ease;
        cursor: pointer;
        &:hover {
          transform: rotate(360deg) scale(1.1);
          box-shadow: 0 0 35px var(--primary-color);
        }
      } 
    }
    .motto { color: var(--blog-text-secondary); font-size: 0.95rem; margin-bottom: 25px; font-style: italic; }
    .social-icons { display: flex; justify-content: center; gap: 24px; font-size: 1.6rem; color: var(--blog-text-secondary); i:hover { color: var(--primary-color); cursor: pointer !important; transform: scale(1.2) rotate(5deg); transition: all 0.3s; } }
  }
  .announcement { .ann-text { font-size: 0.95rem; line-height: 1.8; color: var(--blog-text); font-weight: 500; } }
  .latest-list {
    margin-top: 20px;
    .latest-item {
      display: flex; gap: 15px; margin-bottom: 20px; cursor: pointer !important; align-items: center;
      .item-img { width: 70px; height: 70px; flex-shrink: 0; img { width: 100%; height: 100%; border-radius: 12px; object-fit: cover; } }
      .item-info { flex: 1; overflow: hidden; .item-title { font-size: 0.9rem; font-weight: 700; line-height: 1.4; color: var(--blog-text); display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; } .item-date { font-size: 0.8rem; color: var(--blog-text-secondary); margin-top: 6px; } }
      &:hover .item-title { color: var(--primary-color); }
    }
  }
  .tag-cloud-wrapper { 
    display: flex; flex-wrap: wrap; gap: 10px; margin-top: 15px; 
    .clickable-tag { 
      cursor: pointer !important; 
      transition: all 0.3s ease; 
      font-weight: 600;
      &:hover { 
        background: var(--primary-color) !important; 
        color: white !important; 
        transform: scale(1.1) translateY(-2px);
        box-shadow: 0 5px 15px rgba(99, 102, 241, 0.3);
      }
    }
  }
}

@media (max-width: 1280px) { .article-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { 
  .article-grid { grid-template-columns: 1fr; gap: 25px; } 
  .main-content { padding: 40px 20px; }
  .hero-section .hero-container { padding: 0 20px; }
}
</style>
