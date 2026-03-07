const CACHE_PREFIX = 'jway-oss-images'
const CACHE_NAME = `${CACHE_PREFIX}-v1`
const OSS_HOST = 'jway-blog.oss-cn-beijing.aliyuncs.com'
const IMAGE_EXT_RE = /\.(png|jpe?g|webp|gif|avif|svg)$/i

self.addEventListener('install', (event) => {
  event.waitUntil(self.skipWaiting())
})

self.addEventListener('activate', (event) => {
  event.waitUntil(
    (async () => {
      const keys = await caches.keys()
      const staleKeys = keys.filter((key) => key.startsWith(CACHE_PREFIX) && key !== CACHE_NAME)
      await Promise.all(staleKeys.map((key) => caches.delete(key)))
      await self.clients.claim()
    })()
  )
})

function shouldHandle(request) {
  if (request.method !== 'GET') return false
  const url = new URL(request.url)
  if (url.hostname !== OSS_HOST) return false
  return IMAGE_EXT_RE.test(url.pathname)
}

async function fetchAndCache(request, cache) {
  const response = await fetch(request)
  if (response.ok || response.type === 'opaque') {
    cache.put(request, response.clone())
  }
  return response
}

self.addEventListener('fetch', (event) => {
  const { request } = event
  if (!shouldHandle(request)) return

  event.respondWith(
    (async () => {
      const cache = await caches.open(CACHE_NAME)
      const cached = await cache.match(request)
      if (cached) {
        event.waitUntil(fetchAndCache(request, cache).catch(() => null))
        return cached
      }
      return fetchAndCache(request, cache)
    })()
  )
})
