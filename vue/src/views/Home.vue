<template>
  <div class="home-container">
    <!-- 顶部欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-card">
        <div class="welcome-background">
          <div class="bg-pattern"></div>
          <div class="bg-gradient"></div>
        </div>
        <div class="welcome-content">
          <div class="welcome-info">
            <div class="welcome-header">
              <h1 class="welcome-title">
                <span class="greeting">您好，</span>
                <span class="username">{{ user.nickname || '亲爱的用户' }}</span>
              </h1>
              <div class="welcome-badge">
                <i class="el-icon-star-on"></i>
                <span>VIP用户</span>
              </div>
            </div>
            <p class="welcome-subtitle">欢迎使用枣往果来智能农业管理系统</p>
            <div class="welcome-stats">
              <div class="stat-item" :class="'stat-' + (index + 1)" v-for="(stat, index) in statsData" :key="index">
                <div class="stat-icon">
                  <i :class="stat.icon"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ stat.value }}</div>
                  <div class="stat-label">{{ stat.label }}</div>
                </div>
                <div class="stat-trend" v-if="stat.trend">
                  <i :class="stat.trend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  <span>{{ Math.abs(stat.trend) }}%</span>
                </div>
              </div>
            </div>
          </div>
          <div class="welcome-avatar">
            <div class="avatar-container">
              <div class="avatar-ring"></div>
              <el-avatar :size="100" :src="getAvatarUrl(user.avatarUrl)" class="user-avatar">
                {{ user.nickname ? user.nickname.charAt(0) : 'U' }}
              </el-avatar>
              <div class="avatar-status"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-row :gutter="20">
        <!-- 左侧区域 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <!-- 通知公告卡片 -->
          <el-card class="notice-card" shadow="hover">
            <div slot="header" class="card-header">
              <div class="header-left">
                <div class="header-icon">
                  <i class="el-icon-bell"></i>
                </div>
                <div class="header-content">
                  <h3 class="card-title">最新公告</h3>
                  <p class="card-subtitle">实时更新系统通知</p>
                </div>
              </div>
              <el-button type="text" class="more-btn" @click="handleViewMoreNotices">
                <i class="el-icon-arrow-right"></i>
                查看更多
              </el-button>
            </div>

            <el-skeleton :rows="3" animated :loading="loading">
              <template slot="default">
                <div class="notice-list">
                  <div v-for="(item, index) in notices"
                       :key="index"
                       class="notice-item"
                       :class="{ 'notice-item-active': activeNotice === index }"
                       @click="activeNotice = index">
                    <div class="notice-item-header">
                      <div class="notice-title">
                        <el-tag size="small" :type="item.type" effect="plain">
                          {{ item.tag }}
                        </el-tag>
                        <span class="title-text">{{ item.title }}</span>
                      </div>
                      <span class="notice-time">{{ item.time }}</span>
                    </div>
                    <div v-show="activeNotice === index" class="notice-content">
                      <el-image
                          v-if="item.image"
                          :src="formatImageUrl(item.image)"
                          fit="cover"
                          class="notice-image"
                          :preview-src-list="[formatImageUrl(item.image)]">
                        <div slot="error" class="image-error">
                          <i class="el-icon-picture"></i>
                          <span>图片加载失败</span>
                        </div>
                      </el-image>
                      <p class="notice-text">{{ item.content }}</p>
                    </div>
                  </div>
                </div>
              </template>
            </el-skeleton>
          </el-card>

        </el-col>

        <!-- 右侧区域 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div class="right-column">
            <!-- 天气卡片 -->
            <el-card class="weather-card" shadow="hover">
              <div slot="header" class="card-header">
                <div class="header-left">
                  <div class="header-icon weather-icon">
                    <i class="el-icon-sunny"></i>
                  </div>
                  <div class="header-content">
                    <h3 class="card-title">实时天气</h3>
                    <p class="card-subtitle">当前天气状况</p>
                  </div>
                </div>
                <el-tag type="success" effect="dark" size="small" class="live-tag">
                  <i class="el-icon-refresh"></i>
                  实时更新
                </el-tag>
              </div>

              <div class="weather-content">
                <div class="weather-main">
                  <div class="temperature">
                    <span class="temp-value">{{ weather.temperature }}</span>
                    <span class="temp-unit">°C</span>
                  </div>
                  <div class="weather-info">
                    <i :class="weather.icon" style="font-size: 24px;"></i>
                    <span class="weather-desc">{{ weather.description }}</span>
                  </div>
                </div>

                <el-divider></el-divider>

                <div class="weather-details">
                  <div class="detail-item">
                    <i class="el-icon-wind-power"></i>
                    <div class="detail-info">
                      <span class="detail-label">风速</span>
                      <span class="detail-value">{{ weather.windSpeed }}m/s</span>
                    </div>
                  </div>
                  <div class="detail-item">
                    <i class="el-icon-water-meter"></i>
                    <div class="detail-info">
                      <span class="detail-label">湿度</span>
                      <span class="detail-value">{{ weather.humidity }}%</span>
                    </div>
                  </div>
                  <div class="detail-item">
                    <i class="el-icon-view"></i>
                    <div class="detail-info">
                      <span class="detail-label">能见度</span>
                      <span class="detail-value">{{ weather.visibility }}km</span>
                    </div>
                  </div>
                  <div class="detail-item">
                    <i class="el-icon-takeaway-box"></i>
                    <div class="detail-info">
                      <span class="detail-label">气压</span>
                      <span class="detail-value">{{ weather.pressure }}hPa</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 系统功能导航卡片 -->
            <el-card class="function-nav-card" shadow="hover">
              <div slot="header" class="card-header">
                <div class="header-left">
                  <div class="header-icon">
                    <i class="el-icon-menu"></i>
                  </div>
                  <div class="header-content">
                    <h3 class="card-title">系统功能</h3>
                    <p class="card-subtitle">快速访问核心功能</p>
                  </div>
                </div>
              </div>

              <div class="function-grid">
                <div class="function-item" @click="navigateTo('/mall')">
                  <div class="function-icon-wrapper">
                    <div class="function-icon">
                      <i class="el-icon-goods"></i>
                    </div>
                    <div class="icon-glow"></div>
                  </div>
                  <div class="function-content">
                    <h4 class="function-title">积分商城</h4>
                    <p class="function-desc">商品兑换与积分管理</p>
                    <div class="function-badge">热门</div>
                  </div>
                  <div class="function-arrow">
                    <i class="el-icon-arrow-right"></i>
                  </div>
                </div>
                
                <div class="function-item" @click="navigateTo('/notice')">
                  <div class="function-icon-wrapper">
                    <div class="function-icon">
                      <i class="el-icon-bell"></i>
                    </div>
                    <div class="icon-glow"></div>
                  </div>
                  <div class="function-content">
                    <h4 class="function-title">公告管理</h4>
                    <p class="function-desc">系统通知与公告发布</p>
                    <div class="function-badge">新</div>
                  </div>
                  <div class="function-arrow">
                    <i class="el-icon-arrow-right"></i>
                  </div>
                </div>
                
                <div class="function-item" @click="navigateTo('/personal')">
                  <div class="function-icon-wrapper">
                    <div class="function-icon">
                      <i class="el-icon-user"></i>
                    </div>
                    <div class="icon-glow"></div>
                  </div>
                  <div class="function-content">
                    <h4 class="function-title">个人中心</h4>
                    <p class="function-desc">个人信息与设置</p>
                  </div>
                  <div class="function-arrow">
                    <i class="el-icon-arrow-right"></i>
                  </div>
                </div>
                
                <div class="function-item" @click="navigateTo('/mall/points-tasks')">
                  <div class="function-icon-wrapper">
                    <div class="function-icon">
                      <i class="el-icon-medal"></i>
                    </div>
                    <div class="icon-glow"></div>
                  </div>
                  <div class="function-content">
                    <h4 class="function-title">积分任务</h4>
                    <p class="function-desc">完成任务获取积分</p>
                    <div class="function-badge">推荐</div>
                  </div>
                  <div class="function-arrow">
                    <i class="el-icon-arrow-right"></i>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { Message } from 'element-ui'
// 移除默认头像导入，使用getAvatarUrl方法处理
import request from '@/utils/request'
// 直接引入全局路由实例（Vue2/Vue3 均可导入创建的实例）
import router from '@/router'

export default {
  name: 'Home',
  components: {
    // 移除 Element Plus 图标组件
  },
  setup() {
    const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
    const loading = ref(true)
    const activeNotice = ref(0)
    const timeRange = ref('week')
    const todayTasks = ref(28)
    const pendingTasks = ref(12)
    const completionRate = ref(98)

    const notices = ref([])

    const weather = ref({
      temperature: 25,
      description: '晴',
      icon: 'el-icon-sunny',
      windSpeed: 2.5,
      humidity: 65,
      visibility: 10,
      pressure: 1013
    })

    // 为最大兼容性，使用 hash 跳转，避免路由实例获取不一致导致的首页白屏
    const navigateTo = (path) => {
      if (!path) return
      const clean = path.startsWith('/') ? path : '/' + path
      // 优先使用路由实例（不会覆盖当前 pathname，如 /home）
      try {
        if (router && typeof router.push === 'function') {
          router.push(clean)
          return
        }
      } catch (e) {
        console.warn('router.push 失败，回退到 hash 跳转', e)
      }
      // 兜底：仅改 hash，保持 /home 前缀不变，地址形如 /home#/mall
      window.location.hash = '#'+ clean
    }

    const statsData = ref([
      {
        icon: 'el-icon-date',
        value: todayTasks.value,
        label: '今日任务',
        trend: 12
      },
      {
        icon: 'el-icon-time',
        value: pendingTasks.value,
        label: '待处理',
        trend: -5
      },
      {
        icon: 'el-icon-data-line',
        value: completionRate.value + '%',
        label: '完成率',
        trend: 8
      }
    ])


    // 获取公告数据
    const getNotices = async () => {
      try {
        loading.value = true
        console.log('发起请求：/api/notification/page')
        const response = await request.get('/api/notification/page', {
          params: {
            pageNum: 1,
            pageSize: 10 // 获取最新的10条公告
          }
        })
        
        if (response && response.records && Array.isArray(response.records)) {
          // 转换数据格式，确保与前端展示所需字段匹配
          notices.value = response.records.map(item => ({
            id: item.id,
            title: item.name || item.title || '', // 公告管理页面使用name作为标题
            content: item.content || '',
            time: item.time || '',
            type: item.type || 'info', // 默认为info类型
            tag: item.tag || '通知', // 默认标签
            image: item.img || null // 公告管理页面使用img存储图片路径
          }))
        }
      } catch (error) {
        console.error('获取公告数据失败:', error)
        // 出错时使用默认数据
        notices.value = [
          {
            title: '系统公告',
            content: '欢迎使用枣往果来智能农业管理系统',
            time: new Date().toLocaleString('zh-CN'),
            type: 'info',
            tag: '通知',
            image: null
          }
        ]
      } finally {
        loading.value = false
      }
    }

    // 处理查看更多公告
    const handleViewMoreNotices = () => {
      window.location.href = '/#/notice'
    }

    // 获取头像URL
    const getAvatarUrl = (avatarUrl) => {
      if (!avatarUrl) return null
      
      try {
        // 如果已经是完整的URL，直接返回
        if (avatarUrl.startsWith('http')) {
          return avatarUrl
        }
        
        // 构建基础URL
        let url = ''
        if (!avatarUrl.includes('/')) {
          // 如果是文件UUID，添加API路径
          url = '/api/file/' + avatarUrl
        } else if (avatarUrl.startsWith('/api/')) {
          // 如果已经包含/api/，直接使用
          url = avatarUrl
        } else if (avatarUrl.startsWith('/')) {
          // 如果是其他相对路径，添加api前缀
          url = '/api' + avatarUrl
        } else {
          return null
        }

        // 获取认证信息
        const token = user.value.token
        const userId = user.value.id
        
        // 构建完整URL
        const urlObj = new URL(window.location.origin + url)
        urlObj.searchParams.append('userId', userId)
        urlObj.searchParams.append('token', token)
        
        return urlObj.toString()
      } catch (error) {
        console.error('处理头像URL时出错:', error)
        return null
      }
    }

    // 格式化图片URL
    const formatImageUrl = (imgUrl) => {
      if (!imgUrl) return ''
      
      // 如果是MinIO的URL格式（/api/file/xxx），直接返回（通过网关访问）
      if (imgUrl.startsWith('/api/file/')) {
        return imgUrl
      }
      
      // 如果包含localhost，提取文件名并转换为API路径
      if (imgUrl.startsWith('http') && imgUrl.includes('localhost')) {
        const fileName = imgUrl.split('/').pop()
        // 检查文件名是否包含扩展名，如果没有则添加默认扩展名
        if (!fileName.includes('.')) {
          return `/api/file/${fileName}.jpg`
        } else {
          return `/api/file/${fileName}`
        }
      }
      
      // 如果是其他HTTP URL，直接返回
      if (imgUrl.startsWith('http')) {
        return imgUrl
      }
      
      // 如果是文件名（如 notice_xxx.jpg），添加API前缀
      if (imgUrl.includes('notice_') || imgUrl.includes('avatar_') || imgUrl.includes('product_')) {
        return `/api/file/${imgUrl}`
      }
      
      // 其他情况，添加API前缀
      return `/api/file/${imgUrl}`
    }

    // 监听localStorage变化
    const handleStorageChange = (e) => {
      if (e.key === 'user' && e.newValue) {
        try {
          const newUser = JSON.parse(e.newValue)
          user.value = newUser
          console.log('首页用户信息已更新:', newUser)
        } catch (error) {
          console.error('解析用户信息失败:', error)
        }
      }
    }

    // 监听自定义事件
    const handleUserInfoUpdate = (event) => {
      const updatedUser = event.detail || event
      user.value = updatedUser
      console.log('首页收到用户信息更新事件:', updatedUser)
    }

    // 定期检查localStorage中的用户信息
    let userCheckInterval = null
    
    const checkUserInfo = () => {
      try {
        const currentUserStr = localStorage.getItem('user')
        if (currentUserStr) {
          const currentUser = JSON.parse(currentUserStr)
          // 检查头像URL是否有变化
          if (currentUser.avatarUrl !== user.value.avatarUrl) {
            user.value = currentUser
            console.log('检测到用户头像变化，已更新:', currentUser.avatarUrl)
          }
        }
      } catch (error) {
        console.error('检查用户信息失败:', error)
      }
    }

    onMounted(async () => {
      await getNotices()
      
      // 监听localStorage变化
      window.addEventListener('storage', handleStorageChange)
      
      // 监听自定义事件
      window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
      
      // 监听事件总线（如果存在）
      if (window.$bus) {
        window.$bus.$on('userInfoUpdated', handleUserInfoUpdate)
      }
      
      // 定期检查用户信息变化（每2秒检查一次）
      userCheckInterval = setInterval(checkUserInfo, 2000)
    })

    onUnmounted(() => {
      // 清理事件监听
      window.removeEventListener('storage', handleStorageChange)
      window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
      
      if (window.$bus) {
        window.$bus.$off('userInfoUpdated', handleUserInfoUpdate)
      }
      
      // 清理定时器
      if (userCheckInterval) {
        clearInterval(userCheckInterval)
      }
    })

    return {
      user,
      loading,
      activeNotice,
      timeRange,
      todayTasks,
      pendingTasks,
      completionRate,
      notices,
      weather,
      statsData,
      navigateTo,
      getAvatarUrl,
      handleViewMoreNotices,
      formatImageUrl
    }
  }
}
</script>

<style scoped>
.home-container {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow-x: hidden;
}

.home-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="10" cy="60" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="90" cy="40" r="0.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
  pointer-events: none;
}

.welcome-section {
  margin-bottom: 32px;
  animation: fadeInUp 0.8s ease-out;
}

.welcome-card {
  border-radius: 24px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.welcome-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.bg-pattern {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 20%, rgba(102, 126, 234, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 70% 80%, rgba(118, 75, 162, 0.1) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
  position: relative;
  z-index: 1;
}

.welcome-info {
  flex: 1;
}

.welcome-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.welcome-title {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.greeting {
  opacity: 0.8;
}

.username {
  margin-left: 12px;
}

.welcome-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: #8b4513;
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.3);
}

.welcome-subtitle {
  margin: 0 0 32px;
  font-size: 18px;
  color: #7f8c8d;
  font-weight: 500;
}

.welcome-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, transparent, rgba(255, 255, 255, 0.1));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-item:hover::before {
  opacity: 1;
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  position: relative;
  z-index: 1;
}

.stat-1 .stat-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-2 .stat-icon {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-3 .stat-icon {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-content {
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 12px;
  position: relative;
  z-index: 1;
}

.stat-trend i {
  font-size: 10px;
}

.stat-1 .stat-trend,
.stat-3 .stat-trend {
  background: rgba(46, 204, 113, 0.1);
  color: #27ae60;
}

.stat-2 .stat-trend {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.welcome-avatar {
  margin-left: 40px;
}

.avatar-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-ring {
  position: absolute;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  animation: rotate 10s linear infinite;
  opacity: 0.3;
}

.avatar-ring::before {
  content: '';
  position: absolute;
  top: 4px;
  left: 4px;
  right: 4px;
  bottom: 4px;
  border-radius: 50%;
  background: white;
}

.user-avatar {
  position: relative;
  z-index: 2;
  border: 4px solid white;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.avatar-status {
  position: absolute;
  bottom: 8px;
  right: 8px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #27ae60;
  border: 3px solid white;
  z-index: 3;
}

.main-content {
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.el-card {
  border-radius: 20px;
  margin-bottom: 24px;
  border: none;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.el-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.el-card:hover::before {
  opacity: 1;
}

.el-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(248, 249, 250, 0.5);
  backdrop-filter: blur(10px);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.weather-icon {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.3);
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
}

.card-subtitle {
  margin: 0;
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.more-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667eea;
  font-weight: 600;
  transition: all 0.3s ease;
}

.more-btn:hover {
  color: #764ba2;
  transform: translateX(4px);
}

.live-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.time-range-selector {
  display: flex;
  align-items: center;
}

.range-buttons {
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  padding: 4px;
}

.add-todo-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667eea;
  font-weight: 600;
  transition: all 0.3s ease;
}

.add-todo-btn:hover {
  color: #764ba2;
  transform: scale(1.05);
}

/* 通知列表样式 */
.notice-list {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  padding: 20px;
  border-radius: 16px;
  background: rgba(248, 249, 250, 0.8);
  backdrop-filter: blur(10px);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

.notice-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.notice-item:hover::before,
.notice-item-active::before {
  opacity: 1;
}

.notice-item:hover,
.notice-item-active {
  background: rgba(102, 126, 234, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.1);
}

.notice-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-text {
  font-weight: 600;
  color: #2c3e50;
  font-size: 16px;
}

.notice-time {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

.notice-content {
  margin-top: 16px;
}

.notice-image {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  margin-bottom: 16px;
  object-fit: cover;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.notice-text {
  margin: 0;
  line-height: 1.6;
  color: #606266;
  font-size: 14px;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #7f8c8d;
  background: rgba(248, 249, 250, 0.5);
  border-radius: 12px;
  border: 2px dashed #e1e8ed;
}

/* 天气内容样式 */
.weather-content {
  padding: 24px;
}

.weather-main {
  text-align: center;
  margin-bottom: 24px;
  padding: 20px;
  background: rgba(248, 249, 250, 0.5);
  border-radius: 16px;
  backdrop-filter: blur(10px);
}

.temperature {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.temp-value {
  font-size: 56px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.temp-unit {
  font-size: 28px;
  color: #7f8c8d;
  font-weight: 600;
}

.weather-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #606266;
  font-size: 16px;
  font-weight: 500;
}

.weather-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: rgba(248, 249, 250, 0.8);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.detail-item:hover {
  background: rgba(102, 126, 234, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
}

.detail-item i {
  font-size: 20px;
  color: #667eea;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

.detail-value {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

/* 右侧列布局 */
.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
}

.right-column .weather-card {
  flex-shrink: 0;
}

.right-column .function-nav-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.right-column .function-nav-card .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.right-column .function-grid {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 系统功能导航样式 - 全新设计 */
.function-nav-card {
  animation: fadeInUp 0.8s ease-out 0.4s both;
}

.function-grid {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.function-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.function-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.05));
  opacity: 0;
  transition: all 0.4s ease;
}

.function-item:hover::before {
  opacity: 1;
}

.function-item:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
}

.function-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.function-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  position: relative;
  z-index: 2;
  transition: all 0.4s ease;
}

.function-item:hover .function-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.6);
}

.icon-glow {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.3));
  opacity: 0;
  transition: all 0.4s ease;
  z-index: 1;
}

.function-item:hover .icon-glow {
  opacity: 1;
  transform: scale(1.2);
}

.function-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
  z-index: 2;
}

.function-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
  transition: all 0.3s ease;
}

.function-desc {
  margin: 0;
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
  line-height: 1.4;
}

.function-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.3);
  align-self: flex-start;
  margin-top: 4px;
}

.function-arrow {
  color: #7f8c8d;
  font-size: 20px;
  transition: all 0.4s ease;
  position: relative;
  z-index: 2;
}

.function-item:hover .function-arrow {
  color: #667eea;
  transform: translateX(8px) scale(1.2);
}



/* 动画效果 */
@keyframes pulse-ring {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .welcome-title {
    font-size: 28px;
  }
  .welcome-stats {
    gap: 24px;
  }
  .stat-value {
    font-size: 20px;
  }
}

@media (max-width: 992px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
    padding: 32px;
  }
  .welcome-avatar {
    margin: 24px 0 0;
  }
  .welcome-stats {
    justify-content: center;
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .home-container {
    padding: 16px;
  }
  .welcome-title {
    font-size: 24px;
  }
  .welcome-subtitle {
    font-size: 16px;
  }
  .welcome-stats {
    flex-direction: column;
    gap: 16px;
  }
  .stat-item {
    justify-content: center;
  }
  .card-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  .header-left {
    width: 100%;
  }
  .right-column {
    gap: 16px;
  }
  .right-column .function-grid {
    gap: 12px;
  }
}
</style>