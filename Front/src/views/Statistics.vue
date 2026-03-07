<template>
  <div class="page-container">
    <section class="page-hero">
      <h1 class="neon-text">网站统计</h1>
    </section>
    <main class="page-content">
      <div class="stats-grid">
        <div v-for="item in stats" :key="item.label" class="stat-card card-style">
          <i :class="['bi', item.icon]"></i>
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>

      <div class="more-stats card-style">
        <h3><i class="bi bi-rocket-takeoff"></i> 探索数据</h3>
        <div class="info-row">
          <span>旅行者 1 号距离地球：</span>
          <span class="value">25,259,248,246 KM</span>
        </div>
        <div class="info-row">
          <span>本站运行时间：</span>
          <span class="value">{{ runTimeStr }}</span>
        </div>
        <div class="info-row">
          <span>最后更新时间：</span>
          <span class="value">{{ lastUpdatedDisplay }}</span>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { usePostStore } from '../store/posts'

const postStore = usePostStore()

const stats = computed(() => ([
  { label: '文章数目', value: String(postStore.statistics.totalPosts || 0), icon: 'bi-file-earmark-text' },
  { label: '本站总字数', value: String(postStore.statistics.totalWords || 0), icon: 'bi-pen' },
  { label: '本站访客数', value: String(postStore.statistics.totalVisitors || 0), icon: 'bi-people' },
  { label: '累计访问量', value: String(postStore.statistics.totalViews || 0), icon: 'bi-eye' }
]))

const lastUpdatedDisplay = computed(() => postStore.latestPosts?.[0]?.date || '--')

const runTimeStr = ref('')
const startTime = new Date('2025-12-20T00:00:00')

const updateRunTime = () => {
  const now = new Date()
  const diff = now - startTime
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff / (1000 * 60 * 60)) % 24)
  const minutes = Math.floor((diff / (1000 * 60)) % 60)
  const seconds = Math.floor((diff / 1000) % 60)
  runTimeStr.value = `${days} 天 ${hours} 小时 ${minutes} 分 ${seconds} 秒`
}

let timer
onMounted(async () => {
  await Promise.all([
    postStore.fetchStatistics(),
    postStore.fetchLatest(1)
  ])
  updateRunTime()
  timer = setInterval(updateRunTime, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style lang="scss" scoped>
.page-hero { height: 400px; display: flex; justify-content: center; align-items: center; background: transparent; }
.page-content { max-width: 1000px; margin: -50px auto 60px; padding: 0 20px; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 30px; }
.stat-card {
  padding: 30px;
  text-align: center;
  background: var(--blog-card-bg);
  i { font-size: 2rem; color: var(--primary-color); margin-bottom: 15px; display: block; }
  .stat-value { font-size: 1.5rem; font-weight: bold; margin-bottom: 5px; }
  .stat-label { color: #999; font-size: 0.9rem; }
}
.more-stats { padding: 40px; background: var(--blog-card-bg); }
.more-stats h3 { margin-bottom: 25px; border-bottom: 1px dashed #ddd; padding-bottom: 15px; }
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
  .value { font-weight: bold; color: var(--primary-color); }
}

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
