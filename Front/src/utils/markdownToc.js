const TOC_TITLE = '\u76EE\u5F55'
const TOC_HEADING_RE = new RegExp(`^##\\s*${TOC_TITLE}\\s*$`)
const TOC_ITEM_RE = /^\s*[-*]\s+\[[^\]]+\]\(#.+\)\s*$/
const HEADING_RE = /^(#{1,3})\s+(.+?)\s*$/

function slugify(title) {
  const anchor = title
    .trim()
    .toLowerCase()
    .replace(/[^\w\u4e00-\u9fa5]+/g, '-')
    .replace(/^-+/, '')
    .replace(/-+$/, '')
  return anchor || 'section'
}

function stripExistingToc(content) {
  const lines = (content || '').split(/\r?\n/)
  const start = lines.findIndex(line => TOC_HEADING_RE.test(line.trim()))
  if (start < 0) return content || ''

  let end = start + 1
  while (end < lines.length) {
    const line = lines[end]
    if (line.trim() === '' || TOC_ITEM_RE.test(line)) {
      end += 1
      continue
    }
    break
  }
  while (end < lines.length && lines[end].trim() === '') {
    end += 1
  }

  return [...lines.slice(0, start), ...lines.slice(end)].join('\n')
}

export function extractHeadings(content) {
  const source = content || ''
  const headings = []
  const used = new Map()
  let inCodeFence = false

  source.split(/\r?\n/).forEach((line) => {
    const trimmed = line.trim()
    if (trimmed.startsWith('```')) {
      inCodeFence = !inCodeFence
      return
    }
    if (inCodeFence) return

    const match = HEADING_RE.exec(trimmed)
    if (!match) return

    const level = match[1].length
    const title = match[2].trim()
    if (!title) return

    const base = slugify(title)
    const count = (used.get(base) || 0) + 1
    used.set(base, count)
    const id = count === 1 ? base : `${base}-${count}`

    headings.push({ level, title, id })
  })

  return headings
}

export function upsertToc(content) {
  const baseContent = stripExistingToc(content || '')
  const headings = extractHeadings(baseContent)
  if (headings.length === 0) {
    return baseContent
  }

  const tocLines = [`## ${TOC_TITLE}`]
  headings.forEach((item) => {
    tocLines.push(`${'  '.repeat(Math.max(item.level - 1, 0))}- [${item.title}](#${item.id})`)
  })

  return `${tocLines.join('\n')}\n\n${baseContent.replace(/^\s+/, '')}`
}
