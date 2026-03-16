// 媒体资源统一走后端代理，避免前端暴露 OSS 直链
export const OSS_CONFIG = {
  proxyPath: '/api/media/object',
  dirs: {
    backgrounds: '/blog/backgrounds',
    posts: '/blog/posts',
    works: '/blog/works',
    avatar: '/blog/logo'
  }
}

/**
 * 生成后端代理媒体地址
 * @param {string} dirName 目录名称（如 backgrounds）
 * @param {string} fileName 文件名（如 day.png）
 */
export const getOssUrl = (dirName, fileName) => {
  const dir = OSS_CONFIG.dirs[dirName] || ''
  const key = `${dir}/${fileName}`.replace(/\/{2,}/g, '/').replace(/^\//, '')
  return `${OSS_CONFIG.proxyPath}?key=${encodeURIComponent(key)}`
}
