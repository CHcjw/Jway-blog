<template>
  <div :class="['app-container', { 'dark': isDark }]">
    <div class="global-fixed-bg" :style="{ backgroundImage: `url(${computedHeroImage})` }"></div>
    <Snowfall />

    <!-- Trailing Mouse Effect -->
    <MouseEffect />

    <!-- Floating Action Center -->
    <FloatingTools :toggleDarkFunc="toggleDark" />

    <!-- Global Live2D Maid -->
    <Live2DMaid />

    <!-- Scroll Cat Widget -->
    <ScrollCat />

    <!-- Navbar -->
    <header :class="['header', { 'is-scrolled': isScrolled }]">
      <div class="scroll-progress" :style="{ width: scrollProgress + '%' }"></div>
      
      <nav class="nav-content">
        <!-- LEFT: Brand Logo -->
        <div class="header-left">
          <div class="logo-static" @click="goHome">
            <span class="logo-emoji">🍋</span>
            <span class="logo-text">Jway's Blog</span>
          </div>
        </div>

        <!-- CENTER: Navigation Menu & Back-to-top -->
        <div class="header-center">
          <div class="center-content-wrapper" 
               :class="{ 'clickable-top': isScrolled }"
               @mouseenter="isHoverCenter = true" 
               @mouseleave="isHoverCenter = false" 
               @click="handleCenterClick">
            <transition name="nav-switch" mode="out-in">
              <div v-if="!isScrolled" class="menu" key="menu">
                <div class="menu-item nav-reveal" style="--delay: 0.1s" @click.stop="goHome"><i class="bi bi-house-door"></i> 首页</div>
                <el-dropdown trigger="hover" popper-class="custom-dropdown-popper">
                  <span class="menu-item nav-reveal" style="--delay: 0.2s"><i class="bi bi-journal-text"></i> 文章 <i class="bi bi-chevron-down"></i></span>
                  <template #dropdown>
                    <el-dropdown-menu class="custom-dropdown">
                      <el-dropdown-item @click="router.push('/tags')"><i class="bi bi-tags"></i> 标签</el-dropdown-item>
                      <el-dropdown-item @click="router.push('/categories')"><i class="bi bi-grid"></i> 分类</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
                <el-dropdown trigger="hover" popper-class="custom-dropdown-popper">
                  <span class="menu-item nav-reveal" style="--delay: 0.3s"><i class="bi bi-person-badge"></i> 个人 <i class="bi bi-chevron-down"></i></span>
                  <template #dropdown>
                    <el-dropdown-menu class="custom-dropdown">
                      <el-dropdown-item @click="router.push('/works')"><i class="bi bi-code-slash"></i> 作品</el-dropdown-item>
                      <el-dropdown-item @click="router.push('/skills')"><i class="bi bi-mortarboard"></i> 技能</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
                <el-dropdown trigger="hover" popper-class="custom-dropdown-popper">
                  <span class="menu-item nav-reveal" style="--delay: 0.4s"><i class="bi bi-globe"></i> 网站 <i class="bi bi-chevron-down"></i></span>
                  <template #dropdown>
                    <el-dropdown-menu class="custom-dropdown">
                      <el-dropdown-item @click="router.push('/statistics')"><i class="bi bi-bar-chart-line"></i> 统计</el-dropdown-item>
                      <el-dropdown-item @click="router.push('/moments')"><i class="bi bi-clock-history"></i> 旧时光</el-dropdown-item>
                      <el-dropdown-item @click="router.push('/navigate/website')"><i class="bi bi-compass"></i> 导航</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
              <div v-else class="scroll-nav-info clickable-top" key="scrolled" @click="handleCenterClick">
                <transition name="fade-up" mode="out-in">
                  <span class="back-top-text" title="回到顶部">
                    <i class="bi bi-rocket-takeoff pulse-rocket"></i> 回到顶部
                  </span>
                </transition>
              </div>
            </transition>
          </div>
        </div>

        <!-- RIGHT: Interactive Icons -->
        <div class="header-right">
          <div class="header-icons">
            <!-- Search & Theme (Hidden on Mobile) -->
            <div class="icon-wrapper search-btn hide-on-mobile" @click="searchVisible = true" title="搜索 (Ctrl+K)">
              <el-icon><Search /></el-icon>
            </div>
            <div class="icon-wrapper theme-toggle-btn hide-on-mobile" @click="toggleDark($event)" title="昼夜切换">
              <el-icon><Sun v-if="isDark" /><Moon v-else /></el-icon>
            </div>
            
            <!-- ONLY Visible on Mobile -->
            <div class="icon-wrapper mobile-menu-btn hide-on-pc" @click="mobileMenuVisible = true">
              <el-icon><Menu /></el-icon>
            </div>
          </div>
        </div>
      </nav>
    </header>

    <!-- Mobile Drawer: Dreamy Sidebar (Pro Max) -->
    <el-drawer v-model="mobileMenuVisible" direction="rtl" size="300px" class="pro-sidebar-drawer" :with-header="false" append-to-body>
      <div class="dreamy-drawer-content">
        <h2 class="sidebar-title">NAVIGATION</h2>
        <div class="drawer-menu">
          <!-- Add Search Button First -->
          <div class="dreamy-menu-item search-item" @click="() => { mobileMenuVisible = false; searchVisible = true; }">
            <div class="item-icon c-search"><i class="bi bi-search"></i></div>
            <span class="item-label">全站搜索</span>
            <i class="bi bi-chevron-right arrow-right"></i>
          </div>

          <div v-for="(item, i) in menuItems" :key="i" class="dreamy-menu-item" @click="mobileNavigate(item.path)">
            <div class="item-icon" :class="item.colorClass"><i :class="`bi ${item.icon}`"></i></div>
            <span class="item-label">{{ item.label }}</span>
            <i class="bi bi-chevron-right arrow-right"></i>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- Search Dialog: Dreamy Command Center -->
    <el-dialog v-model="searchVisible" width="650px" class="premium-search-dialog" :show-close="false" append-to-body>
      <div class="search-premium-container">
        <div class="search-header-row">
          <div class="brand-badge"><i class="bi bi-command"></i> SPOTLIGHT</div>
          <div class="close-hint" @click="searchVisible = false">Press <span class="kbd">ESC</span> to close</div>
        </div>
        
        <div class="search-input-area">
          <i class="bi bi-search search-icon-main"></i>
          <input v-model="searchQuery" placeholder="Search posts, tags, or insights..." class="premium-input" autofocus />
        </div>

        <div class="search-results-area">
          <transition name="fade-in" mode="out-in">
            <!-- Empty State -->
            <div v-if="!searchQuery" class="search-empty-suggestions" key="suggest">
              <div class="suggestion-section">
                <span class="section-title">Popular Tags</span>
                <div class="tag-flex">
                  <span v-for="tag in postStore.tagNames.slice(0, 6)" :key="tag" 
                        class="premium-tag-pill" @click="searchQuery = tag"># {{ tag }}</span>
                </div>
              </div>
              <div class="guide-box">
                <div class="guide-line"><i class="bi bi-arrow-down-up"></i><span>Navigate results</span></div>
                <div class="guide-line"><i class="bi bi-arrow-return-left"></i><span>Open selected item</span></div>
              </div>
            </div>

            <!-- Results List -->
            <div v-else-if="filteredResults.length > 0" class="premium-results-list" key="results">
              <div v-for="(item, idx) in filteredResults" :key="item.id" 
                   class="premium-result-card reveal-item" 
                   :style="`--delay: ${idx * 0.04}s`"
                   @click="handleSearchResultClick(item.id)">
                <div class="res-icon-box" :class="item.category">
                  <i class="bi bi-file-earmark-richtext"></i>
                </div>
                <div class="res-body">
                  <div class="res-title-row">
                    <span class="res-title">{{ item.title }}</span>
                    <span class="res-badge">{{ item.category }}</span>
                  </div>
                  <div class="res-meta-info">{{ item.date }} • Reading Time: 5 min</div>
                </div>
                <i class="bi bi-chevron-right res-arrow"></i>
              </div>
            </div>

            <!-- No Results -->
            <div v-else class="premium-no-results" key="empty">
              <i class="bi bi-search-heart pulse-glow"></i>
              <p>No matches for "{{ searchQuery }}"</p>
            </div>
          </transition>
        </div>
      </div>
    </el-dialog>

    <router-view />

    <!-- Redesigned Footer -->
    <footer class="footer">
      <div class="aurora-line"></div>
      <div class="footer-wrap">
        <div class="footer-grid">
          <div class="footer-brand-section">
            <div class="footer-logo">
              <span class="emoji">🍋</span>
              <h2 class="fancy-text">Jway's Blog</h2>
            </div>
            <p class="motto">“再看看那个光点，它就在这里，这是家园，这是我们。”</p>
            <div class="footer-social">
              <a href="https://github.com/CHcjw" target="_blank" rel="noopener noreferrer" style="color: inherit;">
                <i class="bi bi-github"></i>
              </a>
              <el-popover placement="top" :width="200" trigger="click">
                <template #reference>
                  <i class="bi bi-wechat"></i>
                </template>
                <img :src="wechatQrImage" style="width: 100%; height: auto; border-radius: 8px;" alt="WeChat QR" />
              </el-popover>
              <el-popover placement="top" :width="200" trigger="click">
                <template #reference>
                  <i class="bi bi-tencent-qq"></i>
                </template>
                <img :src="qqQrImage" style="width: 100%; height: auto; border-radius: 8px;" alt="QQ QR" />
              </el-popover>
            </div>
          </div>

          <div class="footer-nav-section">
            <h4 class="footer-subtitle">快速导航</h4>
            <div class="nav-links">
              <el-link @click="goHome">首页</el-link>
              <el-link @click="router.push('/tags')">标签云</el-link>
              <el-link @click="router.push('/statistics')">统计详情</el-link>
              <el-link @click="router.push('/moments')">旧时光</el-link>
            </div>
            <div style="margin-top: 25px;">
              <button class="footer-cta-btn" @click="goHome">点击开启星辰之旅</button>
            </div>
          </div>

          <div class="footer-stats-section">
            <div class="footer-stat-card">
              <div class="card-head"><i class="bi bi-activity"></i> UPTIME</div>
              <div class="card-val digital-font">{{ runTimeStr }}</div>
            </div>
            <div class="footer-stat-card voyager">
              <div class="card-head"><i class="bi bi-rocket-takeoff"></i> VOYAGER 1</div>
              <div class="card-val digital-font">25,259,248,246 KM</div>
            </div>
          </div>
        </div>

        <div class="footer-bottom-info">
          <div class="footer-decor left" aria-hidden="true">
            <span></span><span></span><span></span>
          </div>
          <div class="footer-bottom-main">
            <div class="copy">© 2025-2026 Crafted with ❤️ By Jway</div>
            <div class="icp"><a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">粤ICP备2026025509号</a></div>
          </div>
          <div class="footer-decor right" aria-hidden="true">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<style>
/* Custom Dropdown Popper overrides */
.custom-dropdown-popper.el-popper {
  background: var(--blog-glass-bg) !important;
  backdrop-filter: blur(25px) saturate(200%) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  border-radius: 16px !important;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15) !important;
  padding: 8px !important;
  animation: dropdownEnter 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.custom-dropdown-popper.el-popper .el-popper__arrow::before {
  background: var(--blog-glass-bg) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
}

/* BRUTALIST GLOBAL CURSOR OVERRIDE - MUST BE STATIC TO PREVENT RE-APPEARING DURING VUE DOM UPDATES */
@media (hover: hover) and (pointer: fine) {
  * { cursor: none !important; }
  ::before, ::after { cursor: none !important; }
  :hover, :active, :focus { cursor: none !important; }
  .el-popper, .el-dropdown-menu, .el-dropdown-menu__item, [role="tooltip"],
  .el-dialog, .el-dialog__wrapper, .el-overlay, .el-overlay-dialog { cursor: none !important; }
  iframe, canvas, svg, image, input, textarea { cursor: none !important; }
}

@keyframes dropdownEnter {
  from { opacity: 0; transform: translateY(-10px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.custom-dropdown .el-dropdown-menu__item {
  border-radius: 10px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
  margin-bottom: 4px;
  color: var(--blog-text) !important;
}

.custom-dropdown .el-dropdown-menu__item:hover {
  background-color: var(--primary-color) !important;
  color: #fff !important;
  transform: translateX(5px);
}
.custom-dropdown .el-dropdown-menu__item:last-child { margin-bottom: 0; }
</style>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, Menu } from '@element-plus/icons-vue'
import { Sun, Moon } from 'lucide-vue-next'
import { useDark, useToggle } from '@vueuse/core'
import { usePostStore } from './store/posts'
import { getOssUrl } from './config/oss'
import Snowfall from './components/Snowfall.vue'
import MouseEffect from './components/MouseEffect.vue'
import FloatingTools from './components/FloatingTools.vue'
import ScrollCat from './components/ScrollCat.vue'
import Live2DMaid from './components/Live2DMaid.vue'
import { ElNotification } from 'element-plus'

const isDark = useDark()
const _toggleDark = useToggle(isDark)
const router = useRouter()
const route = useRoute()
const postStore = usePostStore()

const isScrolled = ref(false)
const isHoverCenter = ref(false)
const searchVisible = ref(false)
const mobileMenuVisible = ref(false)
const searchQuery = ref('')
const scrollProgress = ref(0)

const menuItems = [
  { label: '首页', path: '/', icon: 'bi-house-door', colorClass: 'c-home' },
  { label: '标签云', path: '/tags', icon: 'bi-tags', colorClass: 'c-tag' },
  { label: '分类归档', path: '/categories', icon: 'bi-grid', colorClass: 'c-cat' },
  { label: '作品集', path: '/works', icon: 'bi-code-slash', colorClass: 'c-work' },
  { label: '技能树', path: '/skills', icon: 'bi-mortarboard', colorClass: 'c-skill' },
  { label: '运行统计', path: '/statistics', icon: 'bi-bar-chart-line', colorClass: 'c-stat' },
  { label: '旧时光', path: '/moments', icon: 'bi-clock-history', colorClass: 'c-time' },
  { label: '网址导航', path: '/navigate/website', icon: 'bi-compass', colorClass: 'c-nav' }
]

const showThemeNotification = () => {
  if (isDark.value) {
    ElNotification({
      title: '关灯啦 🌙',
      message: '当前已成功切换至夜间模式！',
      type: 'success',
      position: 'top-left'
    })
  } else {
    ElNotification({
      title: '开灯啦 🌞',
      message: '当前已成功切换至白天模式！',
      type: 'success',
      position: 'top-left'
    })
  }
}

const toggleDark = (event) => {
  const isTransition = document.startViewTransition && !window.matchMedia('(prefers-reduced-motion: reduce)').matches
  if (!isTransition) { 
    _toggleDark(); 
    showThemeNotification();
    return 
  }
  const x = event.clientX, y = event.clientY
  const endRadius = Math.hypot(Math.max(x, innerWidth - x), Math.max(y, innerHeight - y))
  document.documentElement.classList.add('switching')
  const transition = document.startViewTransition(async () => { _toggleDark() })
  transition.finished.finally(() => {
    document.documentElement.classList.remove('switching')
    showThemeNotification()
  })
  transition.ready.then(() => {
    document.documentElement.animate(
      { clipPath: [`circle(0px at ${x}px ${y}px)`, `circle(${endRadius}px at ${x}px ${y}px)`] },
      { duration: 400, easing: 'ease-in-out', pseudoElement: '::view-transition-new(root)' }
    )
  })
}

const updateScrollProgress = () => {
  const pixels = window.scrollY
  const docHeight = document.documentElement.scrollHeight - window.innerHeight
  scrollProgress.value = (pixels / docHeight) * 100
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > (window.innerHeight * 0.3)
  updateScrollProgress()
}

const handleCenterClick = () => { if (isScrolled.value && isHoverCenter.value) window.scrollTo({ top: 0, behavior: 'smooth' }) }
const goHome = () => router.push('/')
const mobileNavigate = (p) => { mobileMenuVisible.value = false; router.push(p) }

const filteredResults = computed(() => {
  return postStore.searchResults
})

const handleSearchResultClick = (id) => { searchVisible.value = false; searchQuery.value = ''; router.push(`/post/${id}`) }

const runTimeStr = ref('')
const startTime = new Date('2025-12-20T00:00:00')
const updateRunTime = () => {
  const diff = new Date() - startTime
  const d = Math.floor(diff / 86400000), h = Math.floor((diff / 3600000) % 24), m = Math.floor((diff / 60000) % 60), s = Math.floor((diff / 1000) % 60)
  runTimeStr.value = `${d}天 ${h}时 ${m}分 ${s}秒`
}

let timer
onMounted(() => {
  postStore.fetchTags()
  
  // Clean scroll listener attachment only. The loading interceptors have been entirely removed.
  window.addEventListener('scroll', handleScroll, { passive: true })
  window.addEventListener('keydown', (e) => { if ((e.ctrlKey || e.metaKey) && e.key === 'k') { e.preventDefault(); searchVisible.value = true } })
  updateRunTime(); timer = setInterval(updateRunTime, 1000); handleScroll()
})
onUnmounted(() => { window.removeEventListener('scroll', handleScroll); if (timer) clearInterval(timer) })

watch(searchQuery, async (q) => {
  await postStore.searchSpotlight(q)
})

watch(
  () => route.fullPath,
  async (path) => {
    try {
      await postStore.reportVisit(path)
    } catch (_) {
      // ignore tracking errors to avoid blocking page rendering
    }
  },
  { immediate: true }
)

const avatarImage = getOssUrl('avatar', 'avatar.jpg')
const wechatQrImage = getOssUrl('avatar', 'wechat.png')
const qqQrImage = getOssUrl('avatar', 'qq.png')
const dayHero = getOssUrl('backgrounds', 'day.png')
const nightHero = getOssUrl('backgrounds', 'night.png')
const computedHeroImage = computed(() => isDark.value ? nightHero : dayHero)
</script>

<style lang="scss">
@import "./style.scss";

/* 核心修复：重置 Element Plus 容器 */
.el-overlay, .el-overlay-dialog, .el-drawer, .el-dialog { background-color: transparent !important; }

/* HEADER SYSTEM */
.header {
  position: fixed; top: 0; left: 0; right: 0; z-index: 1000;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);

  .nav-content {
    height: 60px; display: flex; align-items: center; padding: 0 36px;
    background: transparent; backdrop-filter: none;
    border-bottom: 1px solid transparent; border-radius: 0;
  }

  &.is-scrolled {
    .nav-content { 
      background: var(--blog-glass-bg); backdrop-filter: blur(20px); 
      border-bottom: 1px solid rgba(255, 255, 255, 0.05); 
      box-shadow: 0 5px 6px -5px rgba(133, 133, 133, 0.6);
    }
    .menu-item { color: var(--blog-text); text-shadow: none; }
    .icon-wrapper { background: rgba(73, 177, 245, 0.1); color: var(--primary-color); }
  }

  /* Responsive Display Utilities */
  @media (max-width: 768px) {
    .header-center { display: none !important; } /* Hide PC nav on mobile */
    .hide-on-mobile { display: none !important; }
    
    /* Cancel scroll effects on mobile */
    &.is-scrolled .nav-content {
      background: transparent !important;
      backdrop-filter: none !important;
      border-bottom: 1px solid transparent !important;
      box-shadow: none !important;
    }

    .mobile-menu-btn {
      background: rgba(255, 255, 255, 0.25) !important;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.6);
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
      color: white !important;
      font-size: 1.4rem;
    }
  }

  @media (min-width: 769px) {
    .hide-on-pc { display: none !important; } /* Hide sidebar toggle on PC */
  }

  .header-left, .header-right { flex: 1; }
  .header-left .logo-static { 
    font-size: 1.4rem; font-weight: 900; font-family: 'Outfit';
    display: flex; align-items: center; gap: 8px; user-select: none;
    white-space: nowrap;
    color: rgba(255, 255, 255, 0.9); text-shadow: 0 0 15px var(--accent-color);
    transition: 0.3s;
    &:hover { color: #fff; text-shadow: 0 0 25px var(--accent-color); }
    .logo-emoji { transition: 0.5s; } &:hover .logo-emoji { transform: rotate(20deg) scale(1.2); }
    @media (max-width: 768px) { 
      .logo-text { display: block; font-size: 1.5rem; } 
    }
  }

  .header-center { flex: 2; display: flex; justify-content: center; }
  .center-content-wrapper { 
    &.clickable-top { user-select: none; &:hover { transform: scale(1.05); } }
  }

  .menu {
    display: flex; gap: 15px; align-items: center;
    .menu-item {
      padding: 10px 18px; border-radius: 12px;
      font-weight: 600; color: #fff; text-shadow: 0 2px 4px rgba(0,0,0,0.2);
      display: flex; align-items: center; gap: 8px; transition: all 0.3s ease-in-out;
      user-select: none;
      i { transition: transform 0.3s ease-in-out; }
      &:hover { 
        background: rgba(255,255,255,0.1); transform: translateY(-2px); 
        color: var(--primary-color);
        i { transform: scale(1.2) rotate(15deg); }
      }
    }
  }

  .header-right { display: flex; justify-content: flex-end; align-items: center; }
  .header-icons { display: flex; gap: 15px; align-items: center; justify-content: flex-end; }
  .icon-wrapper {
    width: 42px; height: 42px; border-radius: 12px; background: rgba(255,255,255,0.1);
    display: flex; align-items: center; justify-content: center;
    font-size: 1.2rem; transition: 0.3s; user-select: none;
    &:hover { background: var(--primary-color); color: white; transform: scale(1.1) rotate(5deg); box-shadow: 0 5px 15px rgba(73, 177, 245, 0.4); }
  }
}

/* PREMIUM SPOTLIGHT SEARCH V2 */
.premium-search-dialog {
  border-radius: 32px !important; overflow: hidden; background-color: var(--blog-bg) !important;
  border: 1px solid rgba(150, 150, 150, 0.2) !important;
  box-shadow: 0 20px 60px rgba(0,0,0,0.4) !important; margin-top: 10vh !important;
  
  .el-dialog__header { display: none; }
  .el-dialog__body { padding: 0 !important; }
  
  .search-premium-container {
    padding: 30px;
    .search-header-row {
      display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
      .brand-badge { font-size: 0.75rem; font-weight: 900; letter-spacing: 2px; color: var(--primary-color); opacity: 0.8; }
      .close-hint { font-size: 0.75rem; color: var(--blog-text-secondary); .kbd { padding: 2px 6px; background: rgba(0,0,0,0.1); border-radius: 4px; font-family: monospace; } }
    }
    .search-input-area {
      display: flex; align-items: center; gap: 20px; padding: 15px 25px; border-radius: 20px;
      background: var(--blog-card-bg); border: 2px solid rgba(150, 150, 150, 0.2);
      transition: all 0.4s ease;
      /* removed the cursor: help css override that was breaking the global none */
      &:focus-within { border-color: var(--primary-color); box-shadow: 0 0 20px rgba(73, 177, 245, 0.3); background: var(--blog-bg); }
      .search-icon-main { font-size: 1.6rem; color: var(--primary-color); transition: 0.3s; }
      &:focus-within .search-icon-main { transform: scale(1.1) rotate(10deg); text-shadow: 0 0 10px var(--primary-color); }
      .premium-input { flex: 1; background: transparent; border: none; outline: none; font-size: 1.4rem; font-weight: 600; color: var(--blog-text); }
    }
    .search-results-area {
      margin-top: 30px; max-height: 400px; overflow-y: auto; scrollbar-width: none;
      .premium-result-card {
        display: flex; align-items: center; gap: 20px; padding: 18px 25px; border-radius: 20px; transition: 0.4s; margin-bottom: 12px;
        background: var(--blog-card-bg); border: 1px solid rgba(150, 150, 150, 0.15); 
        &:hover { background: rgba(99, 102, 241, 0.1); border-color: var(--primary-color); transform: translateX(8px) scale(1.01); box-shadow: 0 5px 15px rgba(0,0,0,0.1); .res-arrow { opacity: 1; transform: translateX(0); } }
        .res-icon-box { width: 50px; height: 50px; border-radius: 15px; background: rgba(99, 102, 241, 0.1); display: flex; align-items: center; justify-content: center; font-size: 1.4rem; color: var(--primary-color); }
        .res-body { flex: 1; .res-title-row { display: flex; align-items: center; gap: 15px; .res-title { font-weight: 800; font-size: 1.15rem; color: var(--blog-text); } .res-badge { font-size: 0.65rem; padding: 3px 10px; background: var(--primary-color); color: white; border-radius: 20px; font-weight: 900; } } .res-meta-info { font-size: 0.8rem; color: var(--blog-text-secondary); margin-top: 5px; } }
        .res-arrow { color: var(--primary-color); opacity: 0; transform: translateX(-10px); transition: 0.3s; }
      }
      .search-empty-suggestions {
        padding: 20px; .section-title { font-size: 0.8rem; font-weight: 800; letter-spacing: 1px; color: var(--blog-text-secondary); margin-bottom: 15px; display: block; }
        .tag-flex { display: flex; flex-wrap: wrap; gap: 10px; .premium-tag-pill { padding: 8px 18px; border-radius: 14px; background: rgba(99, 102, 241, 0.05); font-weight: 700; transition: 0.3s; &:hover { background: var(--primary-color); color: white; transform: translateY(-3px); } } }
        .guide-box { margin-top: 40px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,0.05); .guide-line { display: flex; align-items: center; gap: 10px; font-size: 0.8rem; color: var(--blog-text-secondary); margin-bottom: 10px; i { color: var(--primary-color); } } }
      }
    }
  }

  @media (max-width: 768px) {
    width: 95% !important;
    margin-top: 5vh !important;
    .search-premium-container {
      padding: 20px 15px;
      .search-header-row .close-hint { display: none; }
      .search-input-area {
        padding: 12px 15px;
        gap: 12px;
        .search-icon-main { font-size: 1.3rem; }
        .premium-input { font-size: 1.1rem; }
      }
      .search-results-area {
        margin-top: 20px;
        .premium-result-card {
           padding: 12px 15px; gap: 12px;
           .res-icon-box { width: 40px; height: 40px; font-size: 1.1rem; flex-shrink: 0; }
           .res-body .res-title-row .res-title { font-size: 1rem; line-height: 1.3; }
        }
        .search-empty-suggestions .guide-box { display: none; }
      }
    }
  }
}

/* FOOTER ASYMMETRIC REBORN */
.footer {
  background: rgba(255, 255, 255, 0.45); backdrop-filter: blur(35px); -webkit-backdrop-filter: blur(35px); border-radius: 40px 40px 0 0; padding: 100px 0 34px; margin-top: 10px; position: relative; border-top: 1px solid rgba(255,255,255,0.4); box-shadow: 0 -10px 40px rgba(0,0,0,0.05);
  .aurora-line { width: 100%; height: 3px; position: absolute; top: 0; background: linear-gradient(90deg, transparent, var(--primary-color), var(--accent-color), transparent); opacity: 0.6; border-radius: 40px 40px 0 0; }
  .footer-wrap { max-width: 1300px; margin: 0 auto; padding: 0 40px; }
  .footer-grid { 
    display: grid; grid-template-columns: repeat(3, 1fr); gap: 60px; padding-bottom: 42px; text-align: center; 
    @media (max-width: 992px) { grid-template-columns: 1fr; gap: 40px; }
  }
  .footer-logo { display: flex; align-items: center; justify-content: center; gap: 15px; margin-bottom: 25px; .emoji { font-size: 2.2rem; } h2 { font-size: 2rem; margin: 0; } }
  .motto { font-size: 1.1rem; line-height: 1.8; color: var(--blog-text-secondary); font-style: italic; max-width: 400px; margin: 0 auto; }
  .footer-social { display: flex; justify-content: center; gap: 25px; margin-top: 35px; font-size: 1.6rem; color: var(--blog-text-secondary); i:hover { color: var(--primary-color); transform: scale(1.2); transition: 0.3s; } }
  .footer-subtitle { font-weight: 900; font-size: 1.1rem; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 30px; color: var(--blog-text); }
  .nav-links { display: flex; flex-direction: column; align-items: center; gap: 18px; .el-link { font-weight: 700; color: var(--blog-text-secondary); &:hover { color: var(--primary-color); transform: translateY(-3px); } } }
  
  .footer-cta-btn {
    background: #66ccff; color: #fff; border-radius: 30px; padding: 12px 28px; 
    border: none; font-weight: 900; transition: 0.3s;
    box-shadow: 0 4px 15px rgba(102, 204, 255, 0.4);
    &:hover { transform: scale(1.05) translateY(-3px); background: var(--primary-color); box-shadow: 0 10px 25px rgba(102, 204, 255, 0.6); }
  }

  .footer-stats-section { display: flex; flex-direction: column; gap: 25px; }
  .footer-stat-card {
    background: rgba(73, 177, 245, 0.05); border: 1px solid rgba(73, 177, 245, 0.15); border-radius: 24px; padding: 25px 30px;
    .card-head { font-size: 0.75rem; font-weight: 900; letter-spacing: 2px; color: var(--primary-color); margin-bottom: 12px; }
    .card-val { font-size: 1.2rem; font-weight: 800; color: var(--blog-text); }
    &.voyager { background: rgba(244, 63, 94, 0.05); border-color: rgba(244, 63, 94, 0.15); .card-head { color: var(--accent-color); } }
  }
  .footer-bottom-info {
    display: grid;
    grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr);
    align-items: center;
    gap: 28px;
    border-top: 1px dashed rgba(255,255,255,0.12);
    padding-top: 18px;
    margin-top: 0;
    color: var(--blog-text-secondary);

    .footer-bottom-main {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
      font-weight: 700;
      text-align: center;
      padding: 0 8px;
    }

    .footer-decor {
      display: flex;
      align-items: center;
      width: 100%;
      gap: 10px;
      opacity: 0.65;
      pointer-events: none;

      &::before {
        content: "";
        width: 100%;
        min-width: 0;
        height: 1px;
        background: linear-gradient(90deg, rgba(73, 177, 245, 0.06), rgba(73, 177, 245, 0.55));
      }

      &.right {
        flex-direction: row-reverse;
        &::before {
          background: linear-gradient(90deg, rgba(73, 177, 245, 0.55), rgba(73, 177, 245, 0.06));
        }
      }

      span {
        width: 7px;
        height: 7px;
        border-radius: 50%;
        background: rgba(73, 177, 245, 0.7);
        box-shadow: 0 0 8px rgba(73, 177, 245, 0.35);
        animation: footerTwinkle 2.8s ease-in-out infinite;
      }

      span:nth-child(2) { animation-delay: 0.35s; }
      span:nth-child(3) { animation-delay: 0.7s; }
    }

    .copy {
      transition: transform 0.25s ease, color 0.25s ease, text-shadow 0.25s ease;
      &:hover {
        color: var(--blog-text);
        transform: translateY(-1px);
        text-shadow: 0 0 10px rgba(73, 177, 245, 0.2);
      }
    }

    .icp a {
      color: inherit;
      text-decoration: none;
      position: relative;
      display: inline-block;
      transition: color 0.25s ease, transform 0.25s ease, text-shadow 0.25s ease;
      &:hover {
        color: var(--primary-color);
        transform: translateY(-2px);
        text-shadow: 0 0 12px rgba(73, 177, 245, 0.35);
      }
      &::after {
        content: "";
        position: absolute;
        left: 50%;
        bottom: -4px;
        width: 0;
        height: 2px;
        background: currentColor;
        transform: translateX(-50%);
        transition: width 0.25s ease;
      }
      &:hover::after { width: 100%; }
    }

    @media (max-width: 992px) {
      grid-template-columns: 1fr;
      gap: 10px;
      .footer-decor { display: none; }
      .footer-bottom-main { width: 100%; max-width: 460px; margin: 0 auto; }
    }
  }

  @keyframes footerTwinkle {
    0%, 100% { opacity: 0.35; transform: scale(0.85); }
    50% { opacity: 1; transform: scale(1.25); }
  }

}

/* DREAMY SIDEBAR (MOBILE) PRO MAX */
.pro-sidebar-drawer {
  background: var(--blog-bg) !important;
  border-left: 1px solid rgba(150, 150, 150, 0.2) !important;
  box-shadow: -15px 0 40px rgba(0,0,0,0.3) !important;
  .el-drawer__body { padding: 0 !important; }
  .dreamy-drawer-content {
    height: 100%; display: flex; flex-direction: column; padding: 40px 25px;
    
    .sidebar-title {
      font-size: 0.9rem; font-weight: 900; letter-spacing: 3px; color: var(--primary-color);
      margin-bottom: 25px; text-align: left; opacity: 0.8;
    }
    
    .drawer-menu { 
      flex: 1; display: flex; flex-direction: column; gap: 15px; overflow-y: auto; scrollbar-width: none;
      .dreamy-menu-item { 
        display: flex; align-items: center; padding: 16px 20px; border-radius: 20px; transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
        background: var(--blog-card-bg); 
        border: 2px solid rgba(150, 150, 150, 0.15);
        box-shadow: 0 4px 10px rgba(0,0,0,0.03);
        
        &:hover { 
          background: rgba(99, 102, 241, 0.08); 
          border-color: var(--primary-color); 
          transform: translateX(10px) scale(1.02); 
          box-shadow: 0 8px 20px rgba(0,0,0,0.1); 
        } 
        &:active { transform: translateX(5px) scale(0.98); }
        
        .item-icon { width: 44px; height: 44px; border-radius: 14px; display: flex; align-items: center; justify-content: center; color: white; margin-right: 18px; font-size: 1.3rem; transition: 0.4s; } 
        &:hover .item-icon { transform: rotate(-15deg) scale(1.15); box-shadow: 0 5px 15px rgba(0,0,0,0.2); }
        
        .c-search { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
        .c-home { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); } 
        .c-tag { background: linear-gradient(135deg, #ff0844 0%, #ffb199 100%); } 
        .c-cat { background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); } 
        .c-work { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); } 
        .c-skill { background: linear-gradient(135deg, #b224ef 0%, #7579ff 100%); } 
        .c-stat { background: linear-gradient(135deg, #5ee7df 0%, #b490ca 100%); } 
        .c-time { background: linear-gradient(135deg, #cd9cf2 0%, #f6f3ff 100%); color: #333; }
        .c-nav { background: linear-gradient(135deg, #fbc2eb 0%, #a6c1ee 100%); }
        
        .item-label { font-weight: 800; color: var(--blog-text); flex: 1; font-size: 1.1rem; } 
        .arrow-right { opacity: 0; transform: translateX(-15px); transition: 0.4s; color: var(--primary-color); font-size: 1.2rem; }
        &:hover .arrow-right { opacity: 1; transform: translateX(0); }
      } 
    }
  }
}


.snowflake-divider { display: flex; align-items: center; gap: 15px; margin: 25px 0; .line { flex: 1; height: 2px; background: repeating-linear-gradient(90deg, #3498db, #3498db 4px, transparent 4px, transparent 8px); opacity: 0.3; } .snowflake-icon { font-size: 1.2rem; color: #3498db; animation: gear-rotate 5s infinite linear; } &.compact { margin: 15px 0; } }

/* UTILS */
@keyframes gear-rotate { from { transform: rotate(0); } to { transform: rotate(360deg); } }
@keyframes float-rocket { 0%, 100% { transform: translateY(0) scale(1.1); } 50% { transform: translateY(-3px) scale(1.1); } }
.nav-reveal { animation: navReveal 0.5s ease forwards; animation-delay: var(--delay); opacity: 0; }
@keyframes navReveal { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }

.back-top-text {
  font-weight: 900; letter-spacing: 2px;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  -webkit-background-clip: text; background-clip: text; color: transparent;
  display: flex; align-items: center; gap: 10px; font-size: 1.15rem;
  transition: all 0.3s;
  i { color: var(--primary-color); font-size: 1.35rem; transition: 0.3s; }
  &:hover { 
    transform: scale(1.05); text-shadow: 0 5px 15px rgba(99, 102, 241, 0.3);
    i { animation: float-rocket 1s infinite ease-in-out; color: var(--accent-color); }
  }
}

html.dark {
  .footer { background: transparent; .footer-brand-section h2 { color: #fff; } .footer-stat-card { background: rgba(255,255,255,0.03); .card-val { color: #fff; } } }
  .premium-search-dialog { background-color: var(--blog-bg) !important; border-color: rgba(255,255,255,0.1) !important; .premium-input { color: #eee; } .res-title { color: #ddd; } .premium-tag-pill { background: rgba(255,255,255,0.05); color: #ccc; } }
}

/* View Transitions */
::view-transition-old(root), ::view-transition-new(root) { animation: none; mix-blend-mode: normal; display: block; height: 100%; object-fit: none; }
::view-transition-old(root) { z-index: 1; } ::view-transition-new(root) { z-index: 9999; }
::view-transition-group(root) { animation-duration: 400ms; }
.switching, .switching * { transition: none !important; }
html { scrollbar-gutter: stable; }

</style>
