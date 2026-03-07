<template>
  <div class="navigate-page page-container">
    <div class="page-header">
      <h1 class="neon-text">网址导航</h1>
      <p class="subtitle">精选开发、学习与效率资源</p>
    </div>

    <div class="nav-content">
      <div
        v-for="(section, sIndex) in navData"
        :key="`${section.title}-${sIndex}`"
        class="nav-section reveal-item"
        :style="`--delay: ${0.08 * sIndex}s`"
      >
        <h2 class="section-title">
          <i :class="section.icon"></i> {{ section.title }}
        </h2>

        <div class="nav-grid">
          <a
            v-for="(item, iIndex) in section.links"
            :key="`${item.name}-${iIndex}`"
            :href="item.url"
            target="_blank"
            rel="noopener noreferrer"
            class="nav-card"
          >
            <div class="card-icon">
              <img v-if="item.logo" :src="item.logo" :alt="item.name" @error="handleImageError" />
              <div v-else class="text-icon">{{ item.name.charAt(0).toUpperCase() }}</div>
            </div>
            <div class="card-info">
              <h3 class="name">{{ item.name }}</h3>
              <p class="desc">{{ item.desc }}</p>
            </div>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'

const navData = ref([])

const fallbackData = [
  {
    title: '开发工具推荐',
    icon: 'bi bi-terminal',
    links: [
      { name: 'GitHub', desc: '全球常用代码托管平台', url: 'https://github.com/', logo: 'https://github.githubassets.com/favicons/favicon.svg' },
      { name: 'Gitee', desc: '代码托管与团队协作平台', url: 'https://gitee.com/', logo: 'https://gitee.com/assets/favicon.ico' },
      { name: 'NPM', desc: 'Node.js 包管理平台', url: 'https://www.npmjs.com/', logo: 'https://static-production.npmjs.com/b0f1a8318363185cc2ea6a40ac23eeb2.png' },
      { name: 'Vite', desc: '下一代前端构建工具', url: 'https://cn.vitejs.dev/', logo: 'https://cn.vitejs.dev/logo.svg' },
      { name: 'Vue.js', desc: '渐进式 JavaScript 框架', url: 'https://vuejs.org/', logo: 'https://vuejs.org/logo.svg' },
      { name: 'Element Plus', desc: 'Vue 3 组件库', url: 'https://element-plus.org/zh-CN/', logo: 'https://element-plus.org/images/element-plus-logo-small.svg' }
    ]
  },
  {
    title: '实用工具推荐',
    icon: 'bi bi-tools',
    links: [
      { name: 'PDF24', desc: '在线 PDF 工具集合', url: 'https://tools.pdf24.org/zh/', logo: 'https://tools.pdf24.org/favicon.ico' },
      { name: 'tool.lu', desc: '开发者在线工具箱', url: 'https://tool.lu/', logo: 'https://tool.lu/favicon.ico' },
      { name: '图片格式转换', desc: '多种图片格式转换', url: 'https://www.xunjietupian.com/', logo: 'https://www.xunjietupian.com/favicon.ico' },
      { name: '临时邮箱', desc: '临时邮件地址服务', url: 'https://10minutemail.one/zh', logo: 'https://ui-avatars.com/api/?name=Mail&background=F59E0B&color=fff' }
    ]
  },
  {
    title: '学习资源推荐',
    icon: 'bi bi-book',
    links: [
      { name: 'MDN Web Docs', desc: 'Web 开发权威文档', url: 'https://developer.mozilla.org/', logo: 'https://developer.mozilla.org/favicon-48x48.cbbd161b.png' },
      { name: '掘金', desc: '高质量技术社区', url: 'https://juejin.cn/', logo: 'https://lf3-cdn-tos.bytescm.com/obj/static/xitu_juejin_web/6c61ae65d1c41ae8221a670fa32d05aa.svg' },
      { name: 'LeetCode', desc: '算法练习与面试平台', url: 'https://leetcode.cn/', logo: 'https://leetcode.cn/favicon.ico' },
      { name: 'Bilibili', desc: '优质技术视频教程', url: 'https://www.bilibili.com/', logo: 'https://www.bilibili.com/favicon.ico' }
    ]
  },
  {
    title: '设计资源推荐',
    icon: 'bi bi-palette',
    links: [
      { name: 'Iconfont', desc: '阿里矢量图标库', url: 'https://www.iconfont.cn/', logo: 'https://img.alicdn.com/tfs/TB1O_23Bxn1gK0jSZKPXXXvUXXa-32-32.ico' },
      { name: 'Uiverse', desc: '开源 UI 组件灵感站', url: 'https://uiverse.io/', logo: 'https://uiverse.io/favicon.ico' },
      { name: '好壁纸', desc: '高质量壁纸资源', url: 'https://haowallpaper.com/wallpaperForum', logo: 'https://haowallpaper.com/favicon.ico' }
    ]
  },
  {
    title: '娱乐工具推荐',
    icon: 'bi bi-controller',
    links: [
      { name: 'Steam', desc: '数字游戏平台', url: 'https://store.steampowered.com/', logo: 'https://store.steampowered.com/favicon.ico' },
      { name: '网易云音乐', desc: '音乐播放与发现', url: 'https://music.163.com/', logo: 'https://music.163.com/favicon.ico' },
      { name: 'YouTube', desc: '全球视频平台', url: 'https://www.youtube.com/', logo: 'https://www.youtube.com/favicon.ico' }
    ]
  }
]

const handleImageError = (e) => {
  e.target.style.display = 'none'
  e.target.nextElementSibling && (e.target.nextElementSibling.style.display = 'flex')
}

const fetchNavData = async () => {
  try {
    const res = await http.get('/website-links')
    const data = res.data?.data || []
    navData.value = Array.isArray(data) && data.length ? data : fallbackData
  } catch (_) {
    navData.value = fallbackData
  }
}

onMounted(async () => {
  await fetchNavData()
  window.scrollTo({ top: 0, behavior: 'instant' })
})
</script>

<style scoped lang="scss">
.page-container { max-width: 1400px; margin: 0 auto; padding: 120px 40px 60px; }
.page-header {
  text-align: center;
  margin-bottom: 60px;
  .neon-text { font-size: 3rem; font-family: 'Outfit', sans-serif; margin-bottom: 15px; }
  .subtitle { font-size: 1.1rem; color: var(--blog-text-secondary); font-style: italic; }
}

.nav-content { display: flex; flex-direction: column; gap: 50px; }
.nav-section {
  .section-title {
    font-size: 1.5rem;
    font-weight: 800;
    color: var(--blog-text);
    margin-bottom: 25px;
    display: flex;
    align-items: center;
    gap: 12px;
    border-bottom: 2px solid rgba(150, 150, 150, 0.1);
    padding-bottom: 15px;
    i { color: var(--primary-color); }
  }

  .nav-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
}

.nav-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: var(--blog-card-bg);
  border-radius: 20px;
  border: 1px solid rgba(150, 150, 150, 0.15);
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(99, 102, 241, 0.05);
    border-color: var(--primary-color);
    transform: translateY(-4px);
  }

  .card-icon {
    width: 50px;
    height: 50px;
    flex-shrink: 0;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 14px;
    padding: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    img { width: 100%; height: 100%; object-fit: contain; }
    .text-icon {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.4rem;
      font-weight: 800;
      border-radius: 10px;
    }
  }

  .card-info {
    flex: 1;
    overflow: hidden;
    .name { font-size: 1.1rem; font-weight: 800; color: var(--blog-text); margin-bottom: 6px; }
    .desc {
      font-size: 0.82rem;
      line-height: 1.4;
      color: var(--blog-text-secondary);
      display: -webkit-box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
}

.reveal-item { animation: subReveal 0.6s ease forwards; animation-delay: var(--delay); opacity: 0; }
@keyframes subReveal { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 768px) {
  .page-container { padding: 90px 20px 40px; }
  .page-header .neon-text { font-size: 2.2rem; }
  .nav-section .nav-grid { grid-template-columns: 1fr; }
}
</style>
