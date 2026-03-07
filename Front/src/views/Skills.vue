<template>
  <div class="page-container">
    <section class="page-hero">
      <h1 class="neon-text">核心技能</h1>
      <p class="subtitle">路漫漫其修远兮，吾将上下而求索</p>
    </section>
    <main class="page-content">
      <div class="skills-wrapper card-style glass-effect">
        <div class="skills-section">
          <div class="section-header"><i class="bi bi-code-slash"></i> 前端开发</div>
          <div class="skills-grid">
            <div v-for="s in frontendSkills" :key="s.name" class="skill-item" :style="{ '--skill-color': s.color }">
              <i :class="['bi', s.icon]"></i>
              <span>{{ s.name }}</span>
            </div>
          </div>
        </div>

        <div class="skills-section">
          <div class="section-header"><i class="bi bi-database"></i> 后端开发</div>
          <div class="skills-grid">
            <div v-for="s in backendSkills" :key="s.name" class="skill-item" :style="{ '--skill-color': s.color }">
              <i :class="['bi', s.icon]"></i>
              <span>{{ s.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { usePostStore } from '../store/posts'

const postStore = usePostStore()
const frontendSkills = computed(() => postStore.skills.frontend || [])
const backendSkills = computed(() => postStore.skills.backend || [])

onMounted(async () => {
  await postStore.fetchSkills()
})
</script>

<style lang="scss" scoped>
.page-hero { height: 350px; display: flex; flex-direction: column; justify-content: center; align-items: center; }
.subtitle { color: rgba(255,255,255,0.7); margin-top: 10px; }
.page-content { max-width: 1100px; margin: -40px auto 80px; padding: 0 20px; }

.skills-wrapper { padding: 50px; background: var(--blog-glass-bg); }

.skills-section {
  margin-bottom: 50px;
  &:last-child { margin-bottom: 0; }
  
  .section-header {
    font-size: 1.4rem; font-weight: bold; color: var(--blog-text); margin-bottom: 25px;
    display: flex; align-items: center; gap: 10px;
    i { color: var(--primary-color); }
  }
}

.skills-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 20px;
}

.skill-item {
  background: var(--blog-card-bg);
  padding: 15px 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0,0,0,0.05);
  cursor: default;

  i { font-size: 1.5rem; color: var(--skill-color); }
  span { font-weight: 500; font-size: 1rem; color: var(--blog-text); }

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0,0,0,0.1);
    border-color: var(--skill-color);
    background: rgba(var(--skill-color), 0.05);
    
    span { color: var(--skill-color); }
  }
}

html.dark .skill-item { border-color: rgba(255,255,255,0.05); }
</style>
