<template>
  <div class="page-container">
    <section class="page-hero">
      <h1 class="neon-text">旧时光</h1>
    </section>
    <main class="page-content">
      <div class="timeline-card card-style">
        <el-timeline>
          <el-timeline-item
            v-for="(activity, index) in timelineData"
            :key="index"
            :type="activity.type"
            :size="activity.size"
            :timestamp="activity.timestamp"
            placement="top"
          >
            <div class="timeline-content">
              <h4>{{ activity.title }}</h4>
              <p>{{ activity.content }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { usePostStore } from '../store/posts'

const postStore = usePostStore()

const fallbackTimeline = [
  {
    timestamp: '2026-01-08',
    type: 'primary',
    size: 'large',
    title: '项目立项：从 0 到 1 的第一天',
    content: '确定“个人博客 + 作品集”的方向，先梳理最小可行版本（MVP）：首页、文章、作品、旧时光四个模块。'
  },
  {
    timestamp: '2026-01-12',
    type: 'success',
    size: 'default',
    title: '原型与信息架构',
    content: '完成页面流程图和路由拆分，明确前台展示与后台数据边界，避免后续反复返工。'
  },
  {
    timestamp: '2026-01-17',
    type: 'primary',
    size: 'default',
    title: '前后端骨架搭建',
    content: '前端基于 Vue3 + Vite + Pinia，后端基于 Spring Boot + JPA，打通首条 API 链路。'
  },
  {
    timestamp: '2026-01-23',
    type: 'warning',
    size: 'default',
    title: '第一次踩坑：数据模型改版',
    content: '原始表结构难以扩展标签与分类，重做了表关系并补迁移脚本，代价是两天重构。'
  },
  {
    timestamp: '2026-02-02',
    type: 'success',
    size: 'large',
    title: 'V1 发布：核心功能可用',
    content: '上线文章详情、作品页和全站导航，首版目标达成。'
  },
  {
    timestamp: '2026-02-15',
    type: 'info',
    size: 'default',
    title: '体验优化迭代',
    content: '补齐暗色主题、搜索和页面动效，统一卡片与排版规范，阅读体验明显提升。'
  },
  {
    timestamp: '2026-02-26',
    type: 'warning',
    size: 'default',
    title: '性能专项',
    content: '定位首屏和图片加载瓶颈，引入懒加载与缓存策略，减少重复请求和不必要的资源开销。'
  },
  {
    timestamp: '2026-03-06',
    type: 'primary',
    size: 'large',
    title: 'V2 稳定迭代',
    content: '旧时光内容叙事化，项目进入持续迭代阶段。'
  }
]

const timelineData = computed(() => {
  return postStore.moments && postStore.moments.length ? postStore.moments : fallbackTimeline
})

onMounted(async () => {
  await postStore.fetchMoments()
})
</script>

<style lang="scss" scoped>
.page-hero { height: 400px; display: flex; justify-content: center; align-items: center; background: transparent; }
.page-content { max-width: 800px; margin: -50px auto 60px; padding: 0 20px; }
.timeline-card { padding: 50px; background: var(--blog-card-bg); }
.timeline-content {
  h4 { margin-bottom: 8px; color: var(--blog-text); }
  p { font-size: 0.9rem; color: #888; }
}
</style>
