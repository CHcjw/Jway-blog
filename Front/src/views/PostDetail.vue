<template>
  <div class="post-detail-page">
    <!-- Immersive Banner -->
    <header class="post-banner" :style="{ backgroundImage: `url(${post?.cover})` }">
      <div class="banner-content">
        <h1 class="post-title">{{ post?.title || '文章加载中...' }}</h1>
        <div class="post-meta" v-if="post">
          <span class="meta-item"><i class="bi bi-calendar3"></i> 发表于 {{ post?.date }}</span>
          <span class="divider">|</span>
          <span class="meta-item pointer" @click="router.push(`/categories/${post?.category}`)">
            <i class="bi bi-grid"></i> {{ post?.category }}
          </span>
          <span class="divider">|</span>
          <span class="meta-item"><i class="bi bi-stopwatch"></i> 阅读时长约 5 分钟</span>
        </div>
      </div>
    </header>

    <!-- Main Content Grid -->
    <main class="post-main-container">
      <el-row :gutter="25">
        <!-- Left: Article Content -->
        <el-col :xs="24" :sm="24" :md="18">
          <article class="post-card card-style glass-effect">
            <!-- Inject IDs into rendered content for TOC anchoring -->
            <div class="markdown-body" v-html="processedContent"></div>
            
            <!-- Tags -->
            <div v-if="post?.tags" class="post-footer-tags">
              <el-tag 
                v-for="tag in post.tags" 
                :key="tag" 
                class="post-tag-node"
                @click="router.push(`/tags/${tag}`)"
              >
                <i class="bi bi-hash"></i>{{ tag }}
              </el-tag>
            </div>

            <!-- Copyright Box -->
            <div class="post-copyright">
              <div class="copyright-item">
                <strong>文章作者：</strong><span>Jway</span>
              </div>
              <div class="copyright-item">
                <strong>文章链接：</strong><el-link type="primary" :underline="false">{{ currentUrl }}</el-link>
              </div>
              <div class="copyright-item">
                <strong>版权声明：</strong><span>本博客所有文章除特别声明外，均采用 CC BY-NC-SA 4.0 许可协议。转载请注明来自 Jway's Blog！</span>
              </div>
            </div>

            <!-- Related Posts -->
            <div class="related-posts">
              <h3><i class="bi bi-hand-thumbs-up"></i> 相关推荐</h3>
              <div class="related-grid">
                <div v-for="rp in relatedPosts" :key="rp.id" class="related-item" @click="handleRelatedClick(rp.id)">
                  <div class="rp-img-box">
                    <img :src="rp.cover" alt="cover">
                  </div>
                  <div class="rp-info">
                    <div class="rp-date">{{ rp.date }}</div>
                    <div class="rp-title">{{ rp.title }}</div>
                  </div>
                </div>
              </div>
            </div>
          </article>
        </el-col>

        <!-- Right: Sidebar -->
        <el-col :xs="0" :sm="0" :md="6">
          <aside class="post-sidebar">
            <div class="sidebar-box profile-mini card-style">
              <div class="avatar-container">
                <img :src="avatarImage" alt="avatar" class="avatar-sm avatar-rotate" />
              </div>
              <h4>Jway</h4>
              <p>认真摸鱼中🐟</p>
              <div class="sidebar-stats">
                <div class="s-item"><span>{{ postStore.totalPosts || 0 }}</span>文章</div>
                <div class="s-item"><span>{{ postStore.tagNames.length || 0 }}</span>标签</div>
              </div>
            </div>

            <!-- TOC Box -->
            <div class="sidebar-box toc-box card-style sticky-toc">
              <div class="box-title"><i class="bi bi-list-ul"></i> 目录</div>
              <div class="toc-content">
                <div 
                  v-for="item in toc" 
                  :key="item.id" 
                  :class="['toc-item', `toc-level-${item.level}`]"
                  @click="scrollToAnchor(item.id)"
                >
                  {{ item.title }}
                </div>
                <div v-if="toc.length === 0" class="toc-placeholder">暂无目录</div>
              </div>
            </div>
          </aside>
        </el-col>
      </el-row>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '../store/posts'
import { getOssUrl } from '../config/oss'
import MarkdownIt from 'markdown-it'
import { extractHeadings } from '../utils/markdownToc'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const md = new MarkdownIt({ html: true, linkify: true, typographer: true })

const post = ref(null)
const relatedPosts = ref([])
const avatarImage = getOssUrl('avatar', 'avatar.jpg')
const currentUrl = computed(() => window.location.href)

const toc = computed(() => extractHeadings(post.value?.content || ''))

const processedContent = computed(() => {
  if (!post.value) return ''
  const headingIds = toc.value.map(item => item.id)
  let headingIndex = 0
  const originalHeadingOpen = md.renderer.rules.heading_open

  md.renderer.rules.heading_open = (tokens, idx, options, env, self) => {
    const token = tokens[idx]
    const level = Number(token.tag.replace('h', ''))
    if (level >= 1 && level <= 3) {
      const id = headingIds[headingIndex]
      headingIndex += 1
      if (id) token.attrSet('id', id)
    }
    return self.renderToken(tokens, idx, options)
  }

  const html = md.render(post.value.content)
  md.renderer.rules.heading_open = originalHeadingOpen
  return html
})

const handleRelatedClick = (id) => {
  router.push(`/post/${id}`)
}

const scrollToAnchor = (id) => {
  const el = document.getElementById(id)
  if (el) {
    const offset = 80 // Header height offset
    const bodyRect = document.body.getBoundingClientRect().top
    const elementRect = el.getBoundingClientRect().top
    const elementPosition = elementRect - bodyRect
    const offsetPosition = elementPosition - offset

    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
  }
}

const loadPostData = async (id) => {
  post.value = await postStore.fetchPostById(id)
  relatedPosts.value = await postStore.fetchRelated(id, 4)
}

watch(
  () => route.params.id,
  async (id) => {
    if (!id) return
    await loadPostData(Number(id))
    window.scrollTo(0, 0)
  },
  { immediate: true }
)

onMounted(async () => {
  await Promise.all([postStore.fetchPosts(1, 1), postStore.fetchTags()])
})
</script>

<style lang="scss">
.post-detail-page { min-height: 100vh; }

.post-banner {
  height: 400px; background-size: cover; background-position: center;
  position: relative; display: flex; justify-content: center; align-items: center; color: white;
  &::before { content: ''; position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.45); }
  .banner-content { position: relative; z-index: 1; text-align: center; }
  .post-title { font-size: 2.5rem; margin-bottom: 15px; text-shadow: 0 5px 15px rgba(0,0,0,0.3); }
}

.post-main-container { max-width: 1350px; margin: -40px auto 80px; padding: 0 30px; position: relative; z-index: 2; }

.post-card { 
  padding: 50px; background: var(--blog-card-bg); min-height: 700px;
  .post-footer-tags { margin-top: 40px; padding-top: 20px; border-top: 1px solid rgba(0,0,0,0.05); display: flex; gap: 10px; }
  .post-tag-node { cursor: pointer; transition: 0.3s; &:hover { background: var(--primary-color); color: white; border-color: var(--primary-color); } }
}

.post-sidebar {
  .sidebar-box { padding: 25px; margin-bottom: 25px; }
  .profile-mini {
    text-align: center;
    .avatar-sm { width: 90px; height: 90px; border-radius: 50%; border: 4px solid rgba(255,255,255,0.2); margin-bottom: 15px; }
    h4 { font-size: 1.2rem; margin-bottom: 8px; }
    p { font-size: 0.85rem; color: #888; margin-bottom: 20px; }
    .sidebar-stats { display: flex; justify-content: space-around; font-size: 0.8rem; color: #999; span { display: block; font-weight: bold; color: var(--blog-text); font-size: 1.1rem; } }
  }
  .sticky-toc { position: sticky; top: 100px; }
  
  .toc-content {
    max-height: 60vh; overflow-y: auto;
    .toc-item {
      padding: 8px 15px; cursor: pointer; font-size: 0.9rem; color: var(--blog-text-secondary);
      border-left: 2px solid transparent; transition: all 0.3s;
      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
      
      &:hover { color: var(--primary-color); background: rgba(64, 158, 255, 0.05); border-left-color: var(--primary-color); }
      
      &.toc-level-1 { font-weight: bold; }
      &.toc-level-2 { padding-left: 30px; }
      &.toc-level-3 { padding-left: 45px; font-size: 0.85rem; }
    }
  }
}

.post-copyright {
  margin-top: 40px; padding: 25px; border-left: 4px solid var(--primary-color); background: rgba(64, 158, 255, 0.05); border-radius: 8px;
  .copyright-item { margin-bottom: 8px; font-size: 0.9rem; line-height: 1.6; span { color: var(--blog-text-secondary); } }
}

.related-posts {
  margin-top: 50px;
  h3 { font-size: 1.2rem; margin-bottom: 25px; color: var(--blog-text); display: flex; align-items: center; gap: 10px; }
  .related-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
  .related-item {
    display: flex; gap: 15px; cursor: pointer; padding: 12px; border-radius: 12px; transition: all 0.3s;
    background: rgba(0,0,0,0.02);
    &:hover { background: rgba(64, 158, 255, 0.08); transform: translateY(-3px); .rp-title { color: var(--primary-color); } }
    .rp-img-box { width: 100px; height: 75px; flex-shrink: 0; overflow: hidden; border-radius: 8px; img { width: 100%; height: 100%; object-fit: cover; } }
    .rp-info { flex: 1; .rp-date { font-size: 0.75rem; color: #999; } .rp-title { font-size: 0.95rem; font-weight: 600; margin-top: 6px; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; } }
  }
}

.markdown-body {
  font-size: 1.1rem; line-height: 2; color: var(--blog-text);
  h1, h2, h3 { margin-top: 40px; margin-bottom: 20px; font-weight: 700; color: var(--blog-text); scroll-margin-top: 100px; }
  p { margin-bottom: 15px; }
  pre { background: #2d2d2d; padding: 25px; border-radius: 12px; margin: 30px 0; overflow-x: auto; color: #eee; font-family: 'Fira Code', monospace; }
  blockquote { border-left: 5px solid #409eff; padding: 15px 25px; background: rgba(64, 158, 255, 0.05); margin: 30px 0; border-radius: 0 8px 8px 0; font-style: italic; color: var(--blog-text-secondary); }
}

@media (max-width: 900px) { .related-grid { grid-template-columns: 1fr; } .post-card { padding: 30px; } }
</style>
