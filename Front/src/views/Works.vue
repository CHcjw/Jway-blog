<template>
  <div class="page-container">
    <section class="page-hero">
      <h1 class="neon-text">我的作品</h1>
      <p class="subtitle">探索项目背后的技术与挑战</p>
    </section>
    
    <main class="page-content">
      <div class="works-stage">
        <div 
          v-for="(work, index) in postStore.works" 
          :key="work.id" 
          class="work-card-wrapper"
        >
          <div class="work-card card-style glass-effect">
            <!-- Top Image Area -->
            <div class="work-banner" @click="openPreview(work)">
              <div class="work-banner-bg" :style="{ backgroundImage: `url(${work.image})` }"></div>
              <img class="work-cover" :src="work.image" :alt="work.title" loading="lazy" />
              <div class="zoom-tip">点击查看大图</div>
              <div class="banner-overlay"></div>
            </div>

            <!-- Content Area -->
            <div class="work-details">
              <div class="work-header">
                <h2>{{ work.title }}</h2>
                <div class="work-tags">
                  <el-tag v-for="t in work.techs" :key="t" size="small" effect="dark" class="tech-tag">{{ t }}</el-tag>
                </div>
              </div>
              <div class="work-body">
                <p class="desc">{{ work.desc }}</p>
                <div class="meta-info">
                  <span><i class="bi bi-calendar-check"></i> {{ work.period }}</span>
                  <span><i class="bi bi-people"></i> {{ work.teamSize }} 团队</span>
                </div>
                <div class="challenges">
                  <div class="section-title">亮点</div>
                  <ul>
                    <li v-for="(point, idx) in work.points" :key="idx">{{ point }}</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <el-dialog
      v-model="previewVisible"
      :title="previewTitle"
      width="min(94vw, 1100px)"
      top="4vh"
      class="work-preview-dialog"
      append-to-body
    >
      <div class="preview-stage">
        <img class="preview-image" :src="previewImage" :alt="previewTitle" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { usePostStore } from '../store/posts'

const postStore = usePostStore()
const previewVisible = ref(false)
const previewImage = ref('')
const previewTitle = ref('')

function openPreview(work) {
  previewImage.value = work.image
  previewTitle.value = work.title
  previewVisible.value = true
}

onMounted(async () => {
  await postStore.fetchWorks()
})
</script>

<style lang="scss" scoped>
.page-hero { height: 350px; display: flex; flex-direction: column; justify-content: center; align-items: center; }
.subtitle { color: rgba(255,255,255,0.7); margin-top: 10px; font-size: 1.1rem; }
.page-content { max-width: 1300px; margin: 0 auto 100px; padding: 0 40px; }

/* 3D Stage Logic */
.works-stage {
  display: flex;
  justify-content: center;
  gap: 40px;
  perspective: 2000px; /* 营造 3D 深度感 */
  padding: 40px 0;

  &:hover .work-card-wrapper {
    opacity: 0.6;
    transform: scale(0.9) rotateY(10deg); /* 默认缩小并侧转 */
  }

  .work-card-wrapper {
    flex: 1;
    max-width: 550px;
    transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:hover {
      opacity: 1 !important;
      transform: scale(1.08) translateZ(50px) rotateY(0deg) !important; /* 悬停卡片放大并冲向前方 */
      z-index: 10;
    }
  }
}

.work-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid rgba(255,255,255,0.1);
  
  .work-banner {
    height: clamp(260px, 32vw, 360px);
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(8, 12, 20, 0.65);
    position: relative;
    overflow: hidden;
    cursor: zoom-in;

    .work-banner-bg {
      position: absolute;
      inset: -10%;
      background-size: cover;
      background-position: center;
      filter: blur(20px) saturate(1.1);
      transform: scale(1.08);
      opacity: 0.62;
    }

    .work-cover {
      position: relative;
      z-index: 2;
      width: 100%;
      height: 100%;
      object-fit: contain;
      object-position: center;
      display: block;
    }

    .zoom-tip {
      position: absolute;
      right: 14px;
      top: 14px;
      z-index: 3;
      font-size: 12px;
      color: rgba(255, 255, 255, 0.92);
      background: rgba(0, 0, 0, 0.35);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 999px;
      padding: 4px 10px;
      backdrop-filter: blur(4px);
    }

    .banner-overlay {
      z-index: 3;
      position: absolute; bottom: 0; left: 0; width: 100%; height: 50%;
      background: linear-gradient(to top, var(--blog-card-bg), transparent);
      pointer-events: none;
    }
  }

  .work-details {
    padding: 30px;
    flex: 1;
    display: flex;
    flex-direction: column;

    .work-header {
      margin-bottom: 20px;
      h2 { font-size: 1.5rem; margin-bottom: 12px; color: var(--blog-text); }
      .work-tags { display: flex; flex-wrap: wrap; gap: 8px; }
      .tech-tag { background: var(--primary-color); border: none; }
    }

    .work-body {
      flex: 1;
      .desc { color: var(--blog-text-secondary); line-height: 1.7; margin-bottom: 25px; font-size: 0.95rem; }
      .meta-info {
        display: flex; gap: 25px; font-size: 0.85rem; color: #999; margin-bottom: 25px;
        i { color: var(--primary-color); }
      }
      .challenges {
        background: rgba(64, 158, 255, 0.08); padding: 20px; border-radius: 12px;
        .section-title { font-weight: bold; color: var(--primary-color); margin-bottom: 10px; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; }
        ul { padding-left: 18px; margin: 0; color: var(--blog-text-secondary); font-size: 0.9rem; line-height: 1.6; }
        li { margin-bottom: 6px; }
      }
    }
  }
}

@media (max-width: 1000px) {
  .works-stage {
    flex-direction: column;
    align-items: center;
    &:hover .work-card-wrapper { transform: none; opacity: 1; }
    .work-card-wrapper { width: 100%; max-width: 600px; margin-bottom: 30px; }
    .work-card-wrapper:hover { transform: scale(1.02) !important; }
  }

  .work-card .work-banner {
    height: 240px;
  }
}

:deep(.work-preview-dialog .el-dialog) {
  background: rgba(14, 19, 29, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

:deep(.work-preview-dialog .el-dialog__title) {
  color: #fff;
}

:deep(.work-preview-dialog .el-dialog__body) {
  padding-top: 10px;
}

.preview-stage {
  width: 100%;
  max-height: 78vh;
  border-radius: 12px;
  overflow: hidden;
  background: radial-gradient(circle at 20% 20%, rgba(64, 158, 255, 0.18), rgba(8, 12, 20, 0.95) 58%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  width: 100%;
  max-height: 78vh;
  object-fit: contain;
  display: block;
}
</style>
