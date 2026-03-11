<template>
  <div class="mouse-effect-wrapper">
    <!-- Render all states invisibly to guarantee zero load time/flickering -->
    <div style="display:none;">
      <img v-for="src in allImages" :key="src" :src="src" />
    </div>

    <!-- The physical cursor -->
    <div id="cursor-image-container" 
         class="cursor-image" 
         :style="{ backgroundImage: `url(${currentFrameSrc})` }">
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'

const isHovering = ref(false)
const isGrabbing = ref(false)
const isText = ref(false)
const isWait = ref(false)
const isNotAllowed = ref(false)
const isHelp = ref(false)

const currentFrame = ref(1)
let frameTimer = null
let animationFrameId = null

// Real-time mouse tracking variables
let targetX = window.innerWidth / 2 || 0
let targetY = window.innerHeight / 2 || 0
let currentX = targetX
let currentY = targetY

// Precompute all image URLs for robust caching and preloading
const allImages = []
const statesList = ['default', 'pointer', 'text', 'grabbing', 'wait', 'not-allowed', 'help']
statesList.forEach(state => {
  for (let i = 1; i <= 3; i++) {
    allImages.push(`/cursor/cursor-${state}-${i}.png`)
  }
})

// Calculate current state string
const currentState = computed(() => {
  if (isNotAllowed.value) return 'not-allowed'
  if (isWait.value) return 'wait'
  if (isHelp.value) return 'help'
  if (isGrabbing.value) return 'grabbing'
  if (isText.value) return 'text'
  if (isHovering.value) return 'pointer'
  return 'default'
})

// Construct the dynamic image source path
const currentFrameSrc = computed(() => {
  return `/cursor/cursor-${currentState.value}-${currentFrame.value}.png`
})

// Animation loop to cycle through frames 1-3
// [UPDATED]: Slower flipbook animation. Changed from 150ms to 300ms per frame.
const startFrameAnimation = () => {
  frameTimer = setInterval(() => {
    currentFrame.value = currentFrame.value >= 3 ? 1 : currentFrame.value + 1
  }, 200) 
}

// Smooth hardware-accelerated movement loop using requestAnimationFrame
const renderLoop = () => {
  // [UPDATED]: Linear Interpolation (Lerp) for ultra-smooth movement.
  // Instead of teleporting instantly (currentX = targetX), it glides at 65% distance per frame.
  // This removes any jagged mouse polling stutters without feeling too muddy/unresponsive.
  currentX += (targetX - currentX) * 0.65
  currentY += (targetY - currentY) * 0.65

  const container = document.getElementById('cursor-image-container')
  if (container) {
    let offsetX = '-5%'
    let offsetY = '-5%'
    
    if (isText.value) {
      offsetX = '-50%' 
      offsetY = '-5%'
    }

    container.style.transform = `translate3d(${currentX}px, ${currentY}px, 0) translate(${offsetX}, ${offsetY})`
  }
  
  animationFrameId = requestAnimationFrame(renderLoop)
}

const handleMouseMove = (e) => {
  targetX = e.clientX
  targetY = e.clientY
  
  const target = e.target
  if (target) {
    const computedStyle = window.getComputedStyle(target)
    const cursorStyle = computedStyle.cursor
    
    isText.value = cursorStyle === 'text' || ['INPUT', 'TEXTAREA'].includes(target.tagName)
    isWait.value = cursorStyle === 'wait' || cursorStyle === 'progress'
    isNotAllowed.value = cursorStyle === 'not-allowed' || target.disabled
    
    // Explicitly sniff for search areas for the 'help' Abigail, 
    // because we can't use `cursor: help` CSS without triggering the native cursor bug.
    isHelp.value = cursorStyle === 'help' || 
                   target.closest('.search-input-area') !== null || 
                   target.closest('.search-btn') !== null ||
                   target.closest('.search-item') !== null

    
    isHovering.value = cursorStyle === 'pointer' || 
                        ['A', 'BUTTON', 'SUMMARY', 'LABEL'].includes(target.tagName) || 
                        target.closest('a') !== null ||
                        target.closest('button') !== null ||
                        target.closest('.pointer') !== null ||
                        target.closest('.el-button') !== null ||
                        target.closest('.clickable-top') !== null ||
                        target.closest('.menu-item') !== null ||
                        target.closest('.article-item') !== null
  }
}

const handleMouseDown = () => { isGrabbing.value = true }
const handleMouseUp = () => { isGrabbing.value = false }

onMounted(() => {
  if (typeof window !== 'undefined') {
    targetX = window.innerWidth / 2
    targetY = window.innerHeight / 2
    currentX = targetX
    currentY = targetY
    
    window.addEventListener('mousemove', handleMouseMove, { passive: true })
    window.addEventListener('mousedown', handleMouseDown)
    window.addEventListener('mouseup', handleMouseUp)
    
    startFrameAnimation()
    renderLoop()
  }
})

onUnmounted(() => {
  if (typeof window !== 'undefined') {
    window.removeEventListener('mousemove', handleMouseMove)
    window.removeEventListener('mousedown', handleMouseDown)
    window.removeEventListener('mouseup', handleMouseUp)
    
    if (frameTimer) clearInterval(frameTimer)
    if (animationFrameId) cancelAnimationFrame(animationFrameId)
  }
})
</script>

<style scoped>
.mouse-effect-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 9999999;
  overflow: hidden;
}

:root {
  --custom-cursor-size: 56px; 
}

.cursor-image {
  position: absolute;
  top: 0;
  left: 0;
  width: var(--custom-cursor-size, 56px);
  height: var(--custom-cursor-size, 56px);
  
  background-size: contain;
  background-repeat: no-repeat;
  background-position: top left;
  
  image-rendering: pixelated;
  
  will-change: transform;
  pointer-events: none;
  
  /* [UPDATED]: Slower, creamier image transition fade effect. 
     Changed from 0.08s to 0.25s to blend states far more elegantly without abrupt flashing */
  transition: background-image 0.25s ease-in-out;
}

@media (max-width: 768px) {
  .mouse-effect-wrapper {
    display: none !important;
  }
}
</style>
