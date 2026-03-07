<template>
  <div class="admin-login-page">
    <div class="admin-login-card card-style glass-effect">
      <h2>后台登录</h2>
      <el-form :model="form" @submit.prevent>
        <el-form-item label="入口口令">
          <el-input v-model="form.entryKey" type="password" show-password placeholder="请输入后台入口口令" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin, setAdminEntryKey, setAdminToken } from '../api/admin'

const router = useRouter()
const loading = ref(false)
const form = reactive({ entryKey: '', username: '', password: '' })

const handleLogin = async () => {
  if (!form.entryKey || !form.username || !form.password) {
    ElMessage.error('请完整输入入口口令、用户名和密码')
    return
  }
  try {
    loading.value = true
    const data = await adminLogin(form.username, form.password, form.entryKey)
    setAdminToken(data.token)
    setAdminEntryKey(form.entryKey)
    ElMessage.success('登录成功')
    router.push({ name: 'AdminPosts' })
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}
.admin-login-card {
  width: 460px;
  padding: 32px;
  h2 { margin: 0 0 18px; text-align: center; }
  .hint { margin-top: 12px; font-size: 12px; opacity: .7; text-align: center; }
}
</style>
