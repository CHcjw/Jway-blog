<template>
  <div
    class="floating-tools"
    :class="{ 'is-active': isOpen }"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <!-- Sub Buttons -->
    <div class="tools-list">
      <div class="tool-item" @click="scrollToTop" title="鍥炲埌椤堕儴">
        <i class="bi bi-chevron-double-up"></i>
      </div>
      <div class="tool-item" @click="scrollToBottom" title="鐩磋揪搴曢儴">
        <i class="bi bi-chevron-double-down"></i>
      </div>
      <div class="tool-item theme-btn" @click="handleToggleDark" title="鍒囨崲妯″紡">
        <i class="bi" :class="isDark ? 'bi-sun-fill' : 'bi-moon-stars-fill'"></i>
      </div>
      <div class="tool-item share-btn" @click="copyLink" title="鍒嗕韩閾炬帴">
        <i class="bi bi-share-fill"></i>
      </div>
    </div>

    <!-- Main Gear Button -->
    <div class="main-gear" @click="togglePanel" :class="{ 'spinning': isOpen }">
      <i class="bi bi-gear-fill"></i>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useDark } from '@vueuse/core'
import { ElMessage } from 'element-plus'

const props = defineProps(['toggleDarkFunc'])
const isOpen = ref(false)
const isMobileMode = ref(false)
const isDark = useDark()
let mediaQueryList = null

const syncInteractionMode = () => {
  if (typeof window === 'undefined' || !window.matchMedia) return
  isMobileMode.value = window.matchMedia('(hover: none), (pointer: coarse)').matches
}

const handleMouseEnter = () => {
  if (!isMobileMode.value) isOpen.value = true
}

const handleMouseLeave = () => {
  if (!isMobileMode.value) isOpen.value = false
}

const togglePanel = () => {
  if (isMobileMode.value) isOpen.value = !isOpen.value
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  isOpen.value = false
}

const scrollToBottom = () => {
  window.scrollTo({ top: document.documentElement.scrollHeight, behavior: 'smooth' })
  isOpen.value = false
}

const handleToggleDark = (event) => {
  if (props.toggleDarkFunc) {
    props.toggleDarkFunc(event)
  }
  if (isMobileMode.value) isOpen.value = false
}

const copyLink = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage({
      message: '链接已复制到剪贴板',
      type: 'success',
      plain: true,
      customClass: 'custom-message'
    })
  })
  isOpen.value = false
}

onMounted(() => {
  syncInteractionMode()
  if (!window.matchMedia) return
  mediaQueryList = window.matchMedia('(hover: none), (pointer: coarse)')
  if (mediaQueryList.addEventListener) {
    mediaQueryList.addEventListener('change', syncInteractionMode)
  } else if (mediaQueryList.addListener) {
    mediaQueryList.addListener(syncInteractionMode)
  }
})

onUnmounted(() => {
  if (!mediaQueryList) return
  if (mediaQueryList.removeEventListener) {
    mediaQueryList.removeEventListener('change', syncInteractionMode)
  } else if (mediaQueryList.removeListener) {
    mediaQueryList.removeListener(syncInteractionMode)
  }
})
</script>

<style lang="scss" scoped>
.floating-tools {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;

  .tools-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
    opacity: 0;
    transform: translateY(20px) scale(0.8);
    pointer-events: none;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  }

  &.is-active .tools-list {
    opacity: 1;
    transform: translateY(0) scale(1);
    pointer-events: auto;
  }

  .tool-item {
    width: 45px;
    height: 45px;
    background: var(--blog-glass-bg);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(99, 102, 241, 0.2);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: var(--blog-text);
    font-size: 1.2rem;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;

    &:hover {
      background: var(--primary-color);
      color: white;
      transform: translateX(-5px) scale(1.1);
      box-shadow: 0 10px 25px rgba(99, 102, 241, 0.4);
    }
  }

  .main-gear {
    width: 55px;
    height: 55px;
    background: var(--primary-color);
    color: white;
    border-radius: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.6rem;
    cursor: pointer;
    box-shadow: 0 10px 30px rgba(99, 102, 241, 0.4);
    transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
    
    &.spinning i {
      animation: gear-rotate 2s infinite linear;
    }

    &:hover {
      transform: scale(1.1) rotate(45deg);
      background: var(--accent-color);
    }
  }
}

@keyframes gear-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .floating-tools { bottom: 92px; right: 16px; }
  .main-gear { width: 48px; height: 48px; font-size: 1.4rem; }
  .tool-item { width: 40px; height: 40px; font-size: 1.1rem; }
}
</style>

