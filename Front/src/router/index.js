import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import PostDetail from '../views/PostDetail.vue'
import Tags from '../views/Tags.vue'
import Categories from '../views/Categories.vue'
import Statistics from '../views/Statistics.vue'
import Moments from '../views/OldTimes.vue'
import Works from '../views/Works.vue'
import Skills from '../views/Skills.vue'
import FilterResult from '../views/FilterResult.vue'
import AdminLogin from '../views/AdminLogin.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import AdminWorks from '../views/AdminWorks.vue'
import AdminMoments from '../views/AdminMoments.vue'
import AdminWebsiteLinks from '../views/AdminWebsiteLinks.vue'
import Navigate from '../views/Navigate.vue'
import { getAdminToken } from '../api/admin'

const ADMIN_PANEL_PATH = (import.meta.env.VITE_ADMIN_PANEL_PATH || '').trim()

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/post/:id', name: 'PostDetail', component: PostDetail },
  { path: '/tags', name: 'Tags', component: Tags },
  { path: '/tags/:name', name: 'TagResult', component: FilterResult, props: true },
  { path: '/categories', name: 'Categories', component: Categories },
  { path: '/categories/:name', name: 'CategoryResult', component: FilterResult, props: true },
  { path: '/statistics', name: 'Statistics', component: Statistics },
  { path: '/moments', name: 'Moments', component: Moments },
  { path: '/works', name: 'Works', component: Works },
  { path: '/skills', name: 'Skills', component: Skills },
  { path: '/navigate/website', name: 'Navigate', component: Navigate }
]

if (ADMIN_PANEL_PATH) {
  routes.push(
    { path: `${ADMIN_PANEL_PATH}/login`, name: 'AdminLogin', component: AdminLogin },
    { path: `${ADMIN_PANEL_PATH}/posts`, name: 'AdminPosts', component: AdminDashboard, meta: { requiresAdmin: true } },
    { path: `${ADMIN_PANEL_PATH}/works`, name: 'AdminWorks', component: AdminWorks, meta: { requiresAdmin: true } },
    { path: `${ADMIN_PANEL_PATH}/moments`, name: 'AdminMoments', component: AdminMoments, meta: { requiresAdmin: true } },
    { path: `${ADMIN_PANEL_PATH}/website-links`, name: 'AdminWebsiteLinks', component: AdminWebsiteLinks, meta: { requiresAdmin: true } }
  )
}

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0, behavior: 'smooth' }
  }
})

router.beforeEach((to, from, next) => {
  if (!to.meta.requiresAdmin) {
    next()
    return
  }
  if (!ADMIN_PANEL_PATH) {
    next('/')
    return
  }
  if (!getAdminToken()) {
    next({ name: 'AdminLogin' })
    return
  }
  next()
})

export default router
