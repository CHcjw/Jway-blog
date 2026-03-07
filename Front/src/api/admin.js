import http from './http'

const ADMIN_TOKEN_KEY = 'jway_admin_token'
const ADMIN_ENTRY_KEY = 'jway_admin_entry_key'
const adminPathPrefix = (import.meta.env.VITE_ADMIN_API_PREFIX || '').trim()

export const getAdminToken = () => sessionStorage.getItem(ADMIN_TOKEN_KEY) || ''
export const setAdminToken = (token) => sessionStorage.setItem(ADMIN_TOKEN_KEY, token)
export const clearAdminToken = () => sessionStorage.removeItem(ADMIN_TOKEN_KEY)

export const getAdminEntryKey = () => sessionStorage.getItem(ADMIN_ENTRY_KEY) || ''
export const setAdminEntryKey = (entryKey) => sessionStorage.setItem(ADMIN_ENTRY_KEY, entryKey)
export const clearAdminEntryKey = () => sessionStorage.removeItem(ADMIN_ENTRY_KEY)

const authHeaders = () => ({
  Authorization: `Bearer ${getAdminToken()}`,
  'X-Admin-Entry-Key': getAdminEntryKey()
})

const ensureAdminApiEnabled = () => {
  if (!adminPathPrefix) {
    throw new Error('后台 API 前缀未配置，请设置 VITE_ADMIN_API_PREFIX')
  }
}

export const adminLogin = async (username, password, entryKey) => {
  ensureAdminApiEnabled()
  const res = await http.post(`${adminPathPrefix}/login`, { username, password }, {
    headers: { 'X-Admin-Entry-Key': entryKey }
  })
  return res.data.data
}

export const adminFetchPosts = async (page = 1, size = 20) => {
  ensureAdminApiEnabled()
  const res = await http.get(`${adminPathPrefix}/posts`, { params: { page, size }, headers: authHeaders() })
  return res.data.data
}

export const adminFetchPost = async (id) => {
  ensureAdminApiEnabled()
  const res = await http.get(`${adminPathPrefix}/posts/${id}`, { headers: authHeaders() })
  return res.data.data
}

export const adminCreatePost = async (payload) => {
  ensureAdminApiEnabled()
  const res = await http.post(`${adminPathPrefix}/posts`, payload, { headers: authHeaders() })
  return res.data.data
}

export const adminUpdatePost = async (id, payload) => {
  ensureAdminApiEnabled()
  const res = await http.put(`${adminPathPrefix}/posts/${id}`, payload, { headers: authHeaders() })
  return res.data.data
}

export const adminDeletePost = async (id) => {
  ensureAdminApiEnabled()
  const res = await http.delete(`${adminPathPrefix}/posts/${id}`, { headers: authHeaders() })
  return res.data.data
}

export const adminFetchWorks = async () => {
  ensureAdminApiEnabled()
  const res = await http.get(`${adminPathPrefix}/works`, { headers: authHeaders() })
  return res.data.data
}

export const adminFetchWork = async (id) => {
  ensureAdminApiEnabled()
  const res = await http.get(`${adminPathPrefix}/works/${id}`, { headers: authHeaders() })
  return res.data.data
}

export const adminCreateWork = async (payload) => {
  ensureAdminApiEnabled()
  const res = await http.post(`${adminPathPrefix}/works`, payload, { headers: authHeaders() })
  return res.data.data
}

export const adminUpdateWork = async (id, payload) => {
  ensureAdminApiEnabled()
  const res = await http.put(`${adminPathPrefix}/works/${id}`, payload, { headers: authHeaders() })
  return res.data.data
}

export const adminDeleteWork = async (id) => {
  ensureAdminApiEnabled()
  const res = await http.delete(`${adminPathPrefix}/works/${id}`, { headers: authHeaders() })
  return res.data.data
}

export const adminFetchMoments = async () => {
  ensureAdminApiEnabled()
  const res = await http.get(`${adminPathPrefix}/moments`, { headers: authHeaders() })
  return res.data.data
}

export const adminFetchMoment = async (id) => {
  ensureAdminApiEnabled()
  const res = await http.get(`${adminPathPrefix}/moments/${id}`, { headers: authHeaders() })
  return res.data.data
}

export const adminCreateMoment = async (payload) => {
  ensureAdminApiEnabled()
  const res = await http.post(`${adminPathPrefix}/moments`, payload, { headers: authHeaders() })
  return res.data.data
}

export const adminUpdateMoment = async (id, payload) => {
  ensureAdminApiEnabled()
  const res = await http.put(`${adminPathPrefix}/moments/${id}`, payload, { headers: authHeaders() })
  return res.data.data
}

export const adminDeleteMoment = async (id) => {
  ensureAdminApiEnabled()
  const res = await http.delete(`${adminPathPrefix}/moments/${id}`, { headers: authHeaders() })
  return res.data.data
}

export const adminFetchWebsiteLinks = async () => {
  ensureAdminApiEnabled()
  const res = await http.get(`${adminPathPrefix}/website-links`, { headers: authHeaders() })
  return res.data.data
}

export const adminFetchWebsiteLink = async (id) => {
  ensureAdminApiEnabled()
  const res = await http.get(`${adminPathPrefix}/website-links/${id}`, { headers: authHeaders() })
  return res.data.data
}

export const adminCreateWebsiteLink = async (payload) => {
  ensureAdminApiEnabled()
  const res = await http.post(`${adminPathPrefix}/website-links`, payload, { headers: authHeaders() })
  return res.data.data
}

export const adminUpdateWebsiteLink = async (id, payload) => {
  ensureAdminApiEnabled()
  const res = await http.put(`${adminPathPrefix}/website-links/${id}`, payload, { headers: authHeaders() })
  return res.data.data
}

export const adminDeleteWebsiteLink = async (id) => {
  ensureAdminApiEnabled()
  const res = await http.delete(`${adminPathPrefix}/website-links/${id}`, { headers: authHeaders() })
  return res.data.data
}
