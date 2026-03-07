<template>
  <canvas ref="canvas" class="snowfall-canvas"></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useDark } from '@vueuse/core'

const canvas = ref(null)
const isDark = useDark()

let ctx
let width, height
let snowflakes = []
let animationFrameId

const createSnowflakes = () => {
  const count = Math.floor((width * height) / 10000)
  snowflakes = []
  for (let i = 0; i < count; i++) {
    snowflakes.push({
      x: Math.random() * width,
      y: Math.random() * height,
      radius: Math.random() * 3 + 1,
      speed: Math.random() * 1 + 0.5,
      wind: Math.random() * 0.5 - 0.25,
      opacity: Math.random() * 0.5 + 0.3
    })
  }
}

const update = () => {
  ctx.clearRect(0, 0, width, height)
  
  // Set snowflake color based on theme
  ctx.fillStyle = isDark.value ? 'rgba(255, 255, 255, 0.8)' : 'rgba(100, 150, 255, 0.4)'
  
  snowflakes.forEach(flake => {
    ctx.beginPath()
    ctx.globalAlpha = flake.opacity
    ctx.arc(flake.x, flake.y, flake.radius, 0, Math.PI * 2)
    ctx.fill()
    
    flake.y += flake.speed
    flake.x += flake.wind
    
    if (flake.y > height) {
      flake.y = -flake.radius
      flake.x = Math.random() * width
    }
    if (flake.x > width) flake.x = 0
    if (flake.x < 0) flake.x = width
  })
  
  animationFrameId = requestAnimationFrame(update)
}

const handleResize = () => {
  if (!canvas.value) return
  width = window.innerWidth
  height = window.innerHeight
  canvas.value.width = width
  canvas.value.height = height
  createSnowflakes()
}

onMounted(() => {
  ctx = canvas.value.getContext('2d')
  handleResize()
  window.addEventListener('resize', handleResize)
  update()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  cancelAnimationFrame(animationFrameId)
})
</script>

<style scoped>
.snowfall-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0; /* Behind components but above bg */
}
</style>
