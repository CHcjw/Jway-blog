<template>
  <div class="admin-tabs card-style">
    <el-tabs v-model="activeName" type="card" @tab-change="onTabChange">
      <el-tab-pane label="文章管理" name="AdminPosts" />
      <el-tab-pane label="作品管理" name="AdminWorks" />
      <el-tab-pane label="旧时光管理" name="AdminMoments" />
      <el-tab-pane label="网站导航管理" name="AdminWebsiteLinks" />
    </el-tabs>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const validTabs = ['AdminPosts', 'AdminWorks', 'AdminMoments', 'AdminWebsiteLinks']

const activeName = computed({
  get: () => (validTabs.includes(route.name) ? route.name : 'AdminPosts'),
  set: () => {}
})

const onTabChange = (name) => {
  if (!validTabs.includes(name) || name === route.name) return
  router.push({ name })
}
</script>

<style scoped lang="scss">
.admin-tabs {
  margin-bottom: 12px;
  padding: 8px 12px 0;
  background: var(--blog-card-bg);
}

:deep(.el-tabs__header) {
  margin-bottom: 8px;
}
</style>
