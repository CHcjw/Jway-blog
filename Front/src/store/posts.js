import { defineStore } from 'pinia'
import http from '../api/http'

export const usePostStore = defineStore('posts', {
  state: () => ({
    posts: [],
    totalPosts: 0,
    latestPosts: [],
    categories: [],
    tags: [],
    works: [],
    skills: { frontend: [], backend: [] },
    moments: [],
    statistics: { totalPosts: 0, totalWords: 0, totalViews: 0, totalVisitors: 0 },
    searchResults: [],
    postDetailMap: {}
  }),
  getters: {
    tagNames: (state) => state.tags.map((v) => v.name),
    getPostById: (state) => (id) => state.postDetailMap[id] || state.posts.find((p) => p.id === Number(id))
  },
  actions: {
    async fetchPosts(page = 1, size = 9) {
      const res = await http.get('/posts', { params: { page, size } })
      this.totalPosts = res.data.data.total
      this.posts = res.data.data.list
      return this.posts
    },

    async fetchPostById(id) {
      if (this.postDetailMap[id]) {
        return this.postDetailMap[id]
      }
      const res = await http.get(`/posts/${id}`)
      const post = res.data.data
      this.postDetailMap[id] = post
      return post
    },

    async fetchLatest(limit = 5) {
      const res = await http.get('/posts/latest', { params: { limit } })
      this.latestPosts = res.data.data
      return this.latestPosts
    },

    async fetchRelated(id, limit = 4) {
      const res = await http.get(`/posts/related/${id}`, { params: { limit } })
      return res.data.data
    },

    async searchPosts(params = {}) {
      const merged = { page: 1, size: 9, ...params }
      const res = await http.get('/posts/search', { params: merged })
      return res.data.data
    },

    async searchSpotlight(keyword) {
      if (!keyword || !keyword.trim()) {
        this.searchResults = []
        return []
      }
      const pageData = await this.searchPosts({ keyword, page: 1, size: 20 })
      this.searchResults = pageData.list
      return this.searchResults
    },

    async fetchCategories() {
      const res = await http.get('/categories')
      this.categories = res.data.data
      return this.categories
    },

    async fetchTags() {
      const res = await http.get('/tags')
      this.tags = res.data.data
      return this.tags
    },

    async fetchWorks() {
      const res = await http.get('/works')
      this.works = res.data.data
      return this.works
    },

    async fetchSkills() {
      const res = await http.get('/skills')
      this.skills = res.data.data
      return this.skills
    },

    async fetchMoments() {
      const res = await http.get('/moments')
      this.moments = res.data.data
      return this.moments
    },

    async fetchStatistics() {
      const res = await http.get('/statistics')
      this.statistics = res.data.data
      return this.statistics
    },

    async reportVisit(path = '/') {
      const cacheKey = 'jway_visitor_id'
      const visitorId = localStorage.getItem(cacheKey)
      const res = await http.post('/track/visit', { visitorId, path })
      const data = res.data.data
      if (data?.visitorId) {
        localStorage.setItem(cacheKey, data.visitorId)
      }
      this.statistics.totalVisitors = data.totalVisitors
      this.statistics.totalViews = data.totalViews
      return data
    }
  }
})
