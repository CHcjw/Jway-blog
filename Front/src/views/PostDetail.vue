<template>
  <div class="post-detail-page" @copy="handleCopy">
    <!-- Main Content Grid -->
    <main class="post-main-container">
      <el-row :gutter="25" style="align-items: stretch;">
        <!-- Left: Article Content -->
        <el-col :xs="24" :sm="24" :md="18" style="display: flex; flex-direction: column;">
          <article class="post-card card-style glass-effect" style="flex: 1;">
            <!-- Title Area -->
            <div class="inner-post-header" v-if="post">
              <h1 class="inner-post-title">{{ post.title }}</h1>
              <div class="inner-post-meta">
                <span class="meta-item"><i class="bi bi-calendar3"></i> 发表于 {{ post.date }}</span>
                <span class="divider">|</span>
                <span class="meta-item pointer" @click="router.push(`/categories/${post.category}`)">
                  <i class="bi bi-grid"></i> {{ post.category }}
                </span>
                <span class="divider">|</span>
                <span class="meta-item"><i class="bi bi-stopwatch"></i> 阅读时长约 {{ readingTimeMinutes }} 分钟</span>
              </div>
            </div>

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
        <el-col :xs="0" :sm="0" :md="6" style="display: flex; flex-direction: column;">
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
                  :class="['toc-item', `toc-level-${item.level}`, { active: activeTocId === item.id }]"
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
import { computed, onMounted, ref, watch, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '../store/posts'
import { getOssUrl } from '../config/oss'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'
import { extractHeadings } from '../utils/markdownToc'
import { ElNotification } from 'element-plus'

window.copyMdCode = (text) => {
  navigator.clipboard.writeText(decodeURIComponent(text)).then(() => {
    ElNotification({
      title: '哎嘿！复制成功 🍬',
      message: '若要转载最好保留原文链接哦，给你一个大大的赞！',
      type: 'success',
      position: 'top-left'
    })
  })
}

window.toggleMdCodeBlock = (id) => {
  const block = document.getElementById(id)
  if (!block) return
  block.classList.toggle('expanded')
}

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const md = new MarkdownIt({ html: true, linkify: true, typographer: true })

const post = ref(null)
const relatedPosts = ref([])
const avatarImage = getOssUrl('avatar', 'avatar.jpg')
const currentUrl = computed(() => window.location.href)

const toc = computed(() => extractHeadings(post.value?.content || ''))

const stripMarkdown = (content = '') => {
  return content
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/`[^`]*`/g, ' ')
    .replace(/!\[[^\]]*]\([^)]*\)/g, ' ')
    .replace(/\[[^\]]*]\([^)]*\)/g, ' ')
    .replace(/<[^>]*>/g, ' ')
    .replace(/[#>*_~\-]/g, ' ')
    .replace(/\r?\n/g, ' ')
    .trim()
}

const estimateReadingMinutes = (content = '') => {
  const plainText = stripMarkdown(content)
  if (!plainText) return 1

  const cjkChars = (plainText.match(/[\u4e00-\u9fff]/g) || []).length
  const words = (plainText.match(/[A-Za-z0-9_]+/g) || []).length
  const totalUnits = cjkChars + words
  return Math.max(1, Math.ceil(totalUnits / 300))
}

const readingTimeMinutes = computed(() => estimateReadingMinutes(post.value?.content || ''))

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
    token.attrJoin('class', `custom-heading custom-heading-${level}`)
    let html = self.renderToken(tokens, idx, options)
    html += `<i class="bi bi-fan heading-icon"></i> `
    return html
  }

  md.renderer.rules.fence = (tokens, idx, options, env, self) => {
    const token = tokens[idx]
    const info = token.info ? token.info.trim() : ''
    const lang = info
    
    let contentHtml = md.utils.escapeHtml(token.content)
    let finalLang = lang || 'text'

    if (lang && hljs.getLanguage(lang)) {
      try {
        contentHtml = hljs.highlight(token.content, { language: lang }).value
      } catch (__) {}
    } else {
      try {
        // 限定自动识别的语言范围，防止出现 XQUERY、HANDLEBARS 等冷门语言的乱标，同时大大提高准确率
        const languageSubset = [
          'java', 'javascript', 'typescript', 'html', 'css', 
          'vue', 'xml', 'json', 'sql', 'bash', 'python', 'c', 'cpp', 'yaml', 'markdown'
        ]
        
        let actualCode = token.content
        // 如果系统无法识别 vue 的高亮模块，我们让它降级为 html 并在显示角标时转换
        const result = hljs.highlightAuto(actualCode, languageSubset)
        contentHtml = result.value
        finalLang = result.language || 'text'
        
        // 如果猜测是 xml，因为前端写了很多标签，实际上通常是 html 或 vue
        if (finalLang === 'xml') {
          finalLang = 'html'
        }
      } catch (__) {}
    }

    let langUpper = finalLang.toUpperCase()
    if (langUpper === 'HTML' && token.content.includes('Vue') && token.content.includes('<script')) {
      langUpper = 'VUE'
    }
    const encodedContent = encodeURIComponent(token.content)
    const rawLines = token.content.replace(/\n$/, '').split('\n')
    const lineCount = Math.max(rawLines.length, 1)
    const lineNumbersHtml = Array.from({ length: lineCount }, (_, i) => `<li>${i + 1}</li>`).join('')
    const collapsible = lineCount > 8
    const blockId = `md-code-block-${idx}`
    
    return `
      <div id="${blockId}" class="mac-code-block ${collapsible ? 'is-collapsible' : ''}">
        <div class="mac-header">
          <div class="mac-buttons">
            <span class="mac-btn close"></span>
            <span class="mac-btn minimize"></span>
            <span class="mac-btn expand"></span>
          </div>
          <span class="mac-lang">${langUpper}</span>
          <div class="mac-actions">
            <button class="mac-copy" onclick="window.copyMdCode('${encodedContent}')" title="复制代码">
              <i class="bi bi-clipboard"></i>
            </button>
          </div>
        </div>
        <div class="mac-code-content">
          <ol class="mac-line-numbers">${lineNumbersHtml}</ol>
          <pre><code class="language-${finalLang} hljs">${contentHtml}</code></pre>
          ${collapsible ? '<div class="code-fade-mask"></div>' : ''}
        </div>
        ${collapsible ? `
          <button class="code-collapse-toggle" onclick="window.toggleMdCodeBlock('${blockId}')" title="展开或收起代码">
            <i class="bi bi-chevron-double-down"></i>
          </button>
        ` : ''}
      </div>
    `
  }

  const html = md.render(post.value.content)
  md.renderer.rules.heading_open = originalHeadingOpen
  // clean up fence if needed, but it's fine globally on this md instance
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

const activeTocId = ref('')

const handleCopy = () => {
  const selection = document.getSelection()
  if (selection && selection.toString().length > 0) {
    ElNotification({
      title: '哎嘿！复制成功 🍬',
      message: '若要转载最好保留原文链接哦，给你一个大大的赞！',
      type: 'success',
      position: 'top-left'
    })
  }
}

const handleScroll = () => {
  const headings = Array.from(document.querySelectorAll('.markdown-body .custom-heading'))
  if (!headings.length) return

  let currentActive = headings[0]?.id || ''
  const scrollPosition = window.scrollY + 180

  for (const heading of headings) {
    const top = heading.getBoundingClientRect().top + window.scrollY
    if (top <= scrollPosition) {
      currentActive = heading.id
    } else {
      break
    }
  }

  if (activeTocId.value !== currentActive) {
    activeTocId.value = currentActive
    
    nextTick(() => {
      const activeEl = document.querySelector(`.toc-content .toc-item.active`)
      const container = document.querySelector('.toc-content')
      if (activeEl && container) {
        const containerHeight = container.clientHeight
        const scrollTop = activeEl.offsetTop - (containerHeight / 2) + (activeEl.clientHeight / 2)
        container.scrollTo({ top: scrollTop, behavior: 'smooth' })
      }
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
    setTimeout(handleScroll, 100)
  },
  { immediate: true }
)

onMounted(async () => {
  await Promise.all([postStore.fetchPosts(1, 1), postStore.fetchTags()])
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss">
.post-detail-page { min-height: 100vh; padding-top: 100px; }

.post-main-container { max-width: 1350px; margin: 0 auto 80px; padding: 0 30px; position: relative; z-index: 2; }

.post-card {
  padding: 50px; background: var(--blog-card-bg); min-height: 700px; height: 100%;
  .inner-post-header {
    text-align: center; margin-bottom: 40px; padding-bottom: 20px; border-bottom: 1px dashed rgba(120,120,120,0.2);
    .inner-post-title { font-size: 2.2rem; color: var(--blog-text); margin-bottom: 15px; font-weight: bold; }
    .inner-post-meta { display: flex; justify-content: center; gap: 15px; color: var(--blog-text-secondary); font-size: 0.95rem; .divider { color: rgba(120,120,120,0.3); } }
  }
  .post-footer-tags { margin-top: 40px; padding-top: 20px; border-top: 1px solid rgba(0,0,0,0.05); display: flex; gap: 10px; }
  .post-tag-node { cursor: pointer; transition: 0.3s; &:hover { background: var(--primary-color); color: white; border-color: var(--primary-color); } }
}

.post-sidebar {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  margin-bottom: 0 !important;
  .sidebar-box { padding: 25px; margin-bottom: 25px; }
  .profile-mini {
    text-align: center;
    .avatar-sm { width: 90px; height: 90px; border-radius: 50%; border: 4px solid rgba(255,255,255,0.2); margin-bottom: 15px; }
    h4 { font-size: 1.2rem; margin-bottom: 8px; }
    p { font-size: 0.85rem; color: #888; margin-bottom: 20px; }
    .sidebar-stats { display: flex; justify-content: space-around; font-size: 0.8rem; color: #999; span { display: block; font-weight: bold; color: var(--blog-text); font-size: 1.1rem; } }
  }
  .sticky-toc {
    position: sticky;
    top: 100px;
    max-height: calc(100vh - 130px);
    margin-bottom: 0 !important;
    display: flex;
    flex-direction: column;
  }
  
  .toc-content {
    flex: 1;
    overflow-y: auto;
    .toc-item {
      padding: 8px 15px; cursor: pointer; font-size: 0.9rem; color: var(--blog-text-secondary);
      border-left: 2px solid transparent; transition: all 0.3s;
      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
      
      &:hover, &.active { color: var(--primary-color); background: rgba(64, 158, 255, 0.05); border-left-color: var(--primary-color); }
      
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
  
  .custom-heading {
    margin-top: 45px; margin-bottom: 20px; font-weight: 700; color: #66ccff; scroll-margin-top: 100px;
    display: flex; align-items: center; gap: 8px; transition: all 0.3s;
    &:hover { color: #409eff; .heading-icon { transform: rotate(180deg); color: var(--primary-color); } }
    .heading-icon { transition: transform 0.5s ease; color: #66ccff; font-size: 1.2em; display: inline-block; }
  }

  p { margin-bottom: 15px; }

  img {
    display: block;
    max-width: 100%;
    max-height: min(72vh, 900px);
    width: auto;
    height: auto;
    margin: 20px auto;
    border-radius: 10px;
    object-fit: contain;
  }

  code:not(pre code) {
    color: var(--primary-color);
    background: rgba(64, 158, 255, 0.1);
    padding: 3px 6px;
    border-radius: 6px;
    font-size: 0.9em;
    font-family: 'Fira Code', monospace;
    font-weight: 600;
  }

  /* Mac Style Code Block */
  .mac-code-block {
    background: #282c34; border-radius: 10px; margin: 30px 0; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.15);
    --code-line-height: 1.6;
    --code-max-lines: 8;
    .mac-header {
      background: #21252b; padding: 10px 15px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #181a1f;
      .mac-buttons {
        display: flex; gap: 8px;
        .mac-btn { width: 12px; height: 12px; border-radius: 50%; opacity: 0.8; transition: 0.3s; }
        .mac-btn:hover { opacity: 1; }
        .close { background: #ff5f56; }
        .minimize { background: #ffbd2e; }
        .expand { background: #27c93f; }
      }
      .mac-lang { color: #888; font-size: 0.85rem; font-weight: bold; position: absolute; left: 50%; transform: translateX(-50%); letter-spacing: 1px; }
      .mac-actions { display: flex; gap: 12px; align-items: center; color: #888;
        .mac-copy { background: none; border: none; color: #888; cursor: pointer; font-size: 1.1rem; transition: 0.3s; padding: 0; outline: none; &:hover { color: white; } }
      }
    }

    .mac-code-content {
      position: relative;
      display: grid;
      grid-template-columns: auto 1fr;
      align-items: start;
    }

    .mac-line-numbers {
      margin: 0;
      padding: 20px 10px 20px 14px;
      list-style: none;
      background: rgba(255, 255, 255, 0.02);
      border-right: 1px solid rgba(255, 255, 255, 0.06);
      color: rgba(171, 178, 191, 0.55);
      user-select: none;
      li {
        line-height: var(--code-line-height);
        font-size: 0.95rem;
        min-width: 2ch;
        text-align: right;
      }
    }

    pre {
      background: transparent;
      padding: 20px;
      margin: 0;
      overflow-x: auto;
      color: #abb2bf;
      font-family: 'Fira Code', monospace;
      line-height: var(--code-line-height);
    }

    &.is-collapsible {
      .mac-code-content {
        max-height: calc((var(--code-max-lines) * var(--code-line-height) * 1em) + 40px);
        overflow: hidden;
      }
      .code-fade-mask {
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        height: 70px;
        background: linear-gradient(to bottom, rgba(40, 44, 52, 0), rgba(40, 44, 52, 0.95) 50%, rgba(40, 44, 52, 1) 100%);
        pointer-events: none;
      }
    }

    &.expanded {
      .mac-code-content {
        max-height: none !important;
      }
      .code-fade-mask {
        display: none;
      }
      .code-collapse-toggle i {
        transform: rotate(180deg);
      }
    }

    .code-collapse-toggle {
      width: 100%;
      height: 34px;
      border: none;
      background: #21252b;
      color: rgba(255, 255, 255, 0.85);
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.25s ease;
      i {
        font-size: 1.15rem;
        transition: transform 0.25s ease;
      }
      &:hover {
        color: #fff;
        background: #1b1f24;
      }
    }

    .hljs { background: transparent; padding: 0; }
  }

  blockquote { border-left: 5px solid #66ccff; padding: 15px 25px; background: rgba(102, 204, 255, 0.05); margin: 30px 0; border-radius: 0 8px 8px 0; font-style: italic; color: var(--blog-text-secondary); }

  /* Beautiful Links - especially for TOC inside content */
  a {
    color: var(--primary-color);
    text-decoration: none;
    transition: all 0.3s ease;
    border-bottom: 1px dashed transparent;
    &:hover {
      color: var(--primary-color);
      border-bottom-color: var(--primary-color);
    }
  }
  
  ul {
    padding-left: 20px;
    li {
      margin-bottom: 8px;
      line-height: 1.8;
      &::marker { color: var(--primary-color); }
    }
  }

  /* Beautiful Tables */
  table {
    width: 100%;
    margin: 20px 0;
    border-collapse: collapse;
    background-color: rgba(255, 255, 255, 0.02);
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

    th, td {
      padding: 12px 16px;
      border: 1px solid rgba(120, 120, 120, 0.2);
    }
    
    th {
      background-color: rgba(64, 158, 255, 0.1);
      font-weight: 600;
      color: var(--primary-color);
      text-align: left;
    }

    tr {
      transition: all 0.3s;
      &:nth-child(even) {
        background-color: rgba(120, 120, 120, 0.02);
      }
      &:hover {
        background-color: rgba(64, 158, 255, 0.05);
      }
    }
  }
}

@media (max-width: 900px) {
  .related-grid { grid-template-columns: 1fr; }
  .post-card { padding: 30px; }
}
</style>
