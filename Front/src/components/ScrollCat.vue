<template>
  <div class="scroll-cat-container" :style="{ height: windowHeight + 'px' }">
    <div class="scroll-string" :style="{ height: catTop + 'px' }"></div>
    <div class="cat-wrapper" :style="{ top: catTop + 'px' }" @click="scrollToTop" @mouseenter="isHover = true" @mouseleave="isHover = false">
      <transition name="fade-bubble">
        <div v-show="isHover" class="cat-bubble">春天啦~</div>
      </transition>
      <div class="cat-img" :class="{ 'cat-swing': isHover }"></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const catTop = ref(0)
const windowHeight = ref(1000)
const isHover = ref(false)

const handleScroll = () => {
  const scrollTop = window.scrollY || document.documentElement.scrollTop
  const docHeight = document.documentElement.scrollHeight - window.innerHeight
  if (docHeight <= 0) {
    catTop.value = 0
  } else {
    // minus an exact offset to ensure cat image and padding fits exactly in the view
    const maxTop = window.innerHeight - 75
    catTop.value = (scrollTop / docHeight) * maxTop
  }
}

const handleResize = () => {
  windowHeight.value = window.innerHeight
  handleScroll()
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

onMounted(() => {
  windowHeight.value = window.innerHeight
  window.addEventListener('scroll', handleScroll, { passive: true })
  window.addEventListener('resize', handleResize)
  // brief delay to handle DOM/content expansions on first mount
  setTimeout(() => handleScroll(), 100)
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.scroll-cat-container {
  position: fixed;
  top: 0;
  right: 15px; /* closer to the browser scrollbar */
  width: 30px;
  pointer-events: none; /* Let clicks pass through empty spaces */
  z-index: 1000;

  @media (max-width: 900px) {
    display: none;
  }
}

.scroll-string {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 3px;
  background: rgba(100, 100, 100, 0.4);
  /* string length directly syncs with top */
}

.cat-wrapper {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  pointer-events: auto;
  cursor: pointer;
  width: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* Removed top transition to strictly sync with scrolling wheel! */
}

.cat-img {
  width: 50px;
  height: 70px; /* height to only show the cat part */
  background-image: url('https://fastly.jsdelivr.net/gh/moezx/cdn@3.1.9/img/Sakura/images/scroll.png');
  background-position: center bottom;
  background-size: 100% auto;
  background-repeat: no-repeat;
  transform-origin: top center;
  /* Removed transform transition to avoid conflict with hover animation (fixes the jump flash!) */
}

.cat-swing {
  animation: smooth-swing 1.5s infinite ease-in-out;
}

@keyframes smooth-swing {
  0% { transform: rotate(0deg); }
  25% { transform: rotate(-12deg); }
  75% { transform: rotate(12deg); }
  100% { transform: rotate(0deg); }
}

.cat-bubble {
  position: absolute;
  left: -80px;
  top: -20px;
  background: white;
  border: 2px solid #555;
  border-radius: 15px;
  padding: 8px 12px;
  font-size: 0.9rem;
  font-weight: bold;
  color: #66ccff;
  white-space: nowrap;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  
  /* Bubble Tail */
  &::after {
    content: '';
    position: absolute;
    right: -10px;
    bottom: 5px;
    width: 15px;
    height: 15px;
    background: white;
    border-right: 2px solid #555;
    border-bottom: 2px solid #555;
    border-bottom-right-radius: 5px;
    transform: skew(-20deg) rotate(-45deg);
  }
}

/* Bubble fade animation */
.fade-bubble-enter-active,
.fade-bubble-leave-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.fade-bubble-enter-from,
.fade-bubble-leave-to {
  opacity: 0;
  transform: translateX(10px) scale(0.9);
}
</style>
