// 阿里云 OSS 配置
export const OSS_CONFIG = {
  // 替换为您在阿里云后台创建的 Bucket 访问域名（自定义域名或 OSS 默认域名）
  // 类似于：https://jway-blog.oss-cn-beijing.aliyuncs.com
  baseUrl: 'https://jway-blog.oss-cn-beijing.aliyuncs.com',
  
  // 资源子目录，与您在 OSS 后台建立的目录对应
  dirs: {
    backgrounds: '/blog/backgrounds',
    posts: '/blog/posts',
    works: '/blog/works',
    avatar: '/blog/logo'
    }
    }


/**
 * 快速生成 OSS 链接的工具函数
 * @param {string} dirName 目录键名 (如 backgrounds)
 * @param {string} fileName 文件名 (如 day-bg.jpg)
 */
export const getOssUrl = (dirName, fileName) => {
  return `${OSS_CONFIG.baseUrl}${OSS_CONFIG.dirs[dirName]}/${fileName}`
}
