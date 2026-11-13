<template>
  <div class="hm-home">
    <!-- 顶部欢迎区 -->
    <div class="hm-welcome">
      <div class="hm-welcome-bg">
        <div class="hm-welcome-blob hm-blob-1"></div>
        <div class="hm-welcome-blob hm-blob-2"></div>
        <div class="hm-welcome-blob hm-blob-3"></div>
      </div>
      <div class="hm-welcome-inner">
        <div class="hm-welcome-info">
          <div class="hm-welcome-greet">
            <span class="hm-greet-text">{{ greetingText }}，</span>
            <span class="hm-username">{{ user.nickname || '亲爱的用户' }}</span>
            <span class="hm-badge">
              <i class="el-icon-sunrise"></i>
              {{ timePeriod }}好
            </span>
          </div>
          <p class="hm-welcome-desc">欢迎回到厚朴花开智慧农业平台，今天也是元气满满的一天 🌱</p>
          <div class="hm-stats-row">
            <div class="hm-stat-card hm-stat-1">
              <div class="hm-stat-icon">
                <i class="el-icon-date"></i>
              </div>
              <div class="hm-stat-body">
                <div class="hm-stat-value">{{ statsData[0].value }}</div>
                <div class="hm-stat-label">{{ statsData[0].label }}</div>
              </div>
              <div class="hm-stat-trend up" v-if="statsData[0].trend">
                <i class="el-icon-top"></i>
                {{ Math.abs(statsData[0].trend) }}%
              </div>
            </div>
            <div class="hm-stat-card hm-stat-2">
              <div class="hm-stat-icon">
                <i class="el-icon-time"></i>
              </div>
              <div class="hm-stat-body">
                <div class="hm-stat-value">{{ statsData[1].value }}</div>
                <div class="hm-stat-label">{{ statsData[1].label }}</div>
              </div>
              <div class="hm-stat-trend down" v-if="statsData[1].trend">
                <i class="el-icon-bottom"></i>
                {{ Math.abs(statsData[1].trend) }}%
              </div>
            </div>
            <div class="hm-stat-card hm-stat-3">
              <div class="hm-stat-icon">
                <i class="el-icon-data-line"></i>
              </div>
              <div class="hm-stat-body">
                <div class="hm-stat-value">{{ statsData[2].value }}</div>
                <div class="hm-stat-label">{{ statsData[2].label }}</div>
              </div>
              <div class="hm-stat-trend up" v-if="statsData[2].trend">
                <i class="el-icon-top"></i>
                {{ Math.abs(statsData[2].trend) }}%
              </div>
            </div>
          </div>
        </div>
        <div class="hm-welcome-avatar">
          <div class="hm-avatar-ring"></div>
          <div class="hm-avatar-wrap">
            <el-avatar :size="90" :src="getAvatarUrl(user.avatarUrl)" class="hm-avatar-img">
              {{ user.nickname ? user.nickname.charAt(0) : 'U' }}
            </el-avatar>
            <div class="hm-avatar-dot"></div>
          </div>
          <div class="hm-avatar-info">
            <div class="hm-avatar-name">{{ user.nickname || '用户' }}</div>
            <div class="hm-avatar-role">
              <i class="el-icon-medal"></i>
              VIP会员
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容区 -->
    <div class="hm-content">
      <el-row :gutter="20">
        <!-- 左列 -->
        <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16">
          <!-- 公告卡片 -->
          <el-card class="hm-card hm-notice-card" shadow="never" body-style="padding: 0;">
            <div class="hm-card-head">
              <div class="hm-card-title-wrap">
                <div class="hm-card-icon">
                  <i class="el-icon-bell"></i>
                </div>
                <div>
                  <h3 class="hm-card-title">最新公告</h3>
                  <p class="hm-card-sub">系统通知与重要消息</p>
                </div>
              </div>
              <el-button type="text" class="hm-more-btn" @click="handleViewMoreNotices">
                查看全部
                <i class="el-icon-arrow-right"></i>
              </el-button>
            </div>
            <div class="hm-card-body">
              <el-skeleton :rows="3" animated :loading="loading">
                <template slot="default">
                  <div class="hm-notice-list">
                    <div v-for="(item, index) in notices.slice(0, 4)"
                         :key="index"
                         class="hm-notice-item"
                         :class="{ active: activeNotice === index }"
                         @click="activeNotice = index">
                      <div class="hm-notice-dot" :class="'dot-' + (item.type || 'info')"></div>
                      <div class="hm-notice-main">
                        <div class="hm-notice-top">
                          <span class="hm-notice-title">{{ item.title }}</span>
                          <el-tag size="mini" :type="item.type" effect="plain" class="hm-notice-tag">
                            {{ item.tag }}
                          </el-tag>
                        </div>
                        <div class="hm-notice-bottom">
                          <span class="hm-notice-time">
                            <i class="el-icon-time"></i>
                            {{ item.time }}
                          </span>
                        </div>
                        <div v-show="activeNotice === index" class="hm-notice-detail">
                          <el-image
                              v-if="item.image"
                              :src="formatImageUrl(item.image)"
                              fit="cover"
                              class="hm-notice-img"
                              :preview-src-list="[formatImageUrl(item.image)]">
                          </el-image>
                          <p class="hm-notice-text">{{ item.content }}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
              </el-skeleton>
            </div>
          </el-card>
        </el-col>

        <!-- 右列 -->
        <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
          <!-- 天气卡片 -->
          <el-card class="hm-card hm-weather-card" shadow="never" body-style="padding: 0;">
            <div class="hm-card-head">
              <div class="hm-card-title-wrap">
                <div class="hm-card-icon weather">
                  <i :class="weather.icon"></i>
                </div>
                <div>
                  <h3 class="hm-card-title">实时天气</h3>
                  <p class="hm-card-sub">当前地点 · 实时监测</p>
                </div>
              </div>
              <span class="hm-live-tag">
                <span class="hm-live-dot"></span>
                实时
              </span>
            </div>
            <div class="hm-card-body">
              <div class="hm-weather-main">
                <div class="hm-weather-temp">
                  <span class="hm-temp-num">{{ weather.temperature }}</span>
                  <span class="hm-temp-unit">°C</span>
                </div>
                <div class="hm-weather-desc">
                  <i :class="weather.icon"></i>
                  {{ weather.description }}
                </div>
              </div>
              <div class="hm-weather-grid">
                <div class="hm-weather-item">
                  <div class="hm-w-icon"><i class="el-icon-wind-power"></i></div>
                  <div class="hm-w-text">
                    <span class="hm-w-label">风速</span>
                    <span class="hm-w-value">{{ weather.windSpeed }} m/s</span>
                  </div>
                </div>
                <div class="hm-weather-item">
                  <div class="hm-w-icon"><i class="el-icon-water-cup"></i></div>
                  <div class="hm-w-text">
                    <span class="hm-w-label">湿度</span>
                    <span class="hm-w-value">{{ weather.humidity }}%</span>
                  </div>
                </div>
                <div class="hm-weather-item">
                  <div class="hm-w-icon"><i class="el-icon-view"></i></div>
                  <div class="hm-w-text">
                    <span class="hm-w-label">能见度</span>
                    <span class="hm-w-value">{{ weather.visibility }} km</span>
                  </div>
                </div>
                <div class="hm-weather-item">
                  <div class="hm-w-icon"><i class="el-icon-gauge"></i></div>
                  <div class="hm-w-text">
                    <span class="hm-w-label">气压</span>
                    <span class="hm-w-value">{{ weather.pressure }} hPa</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 快捷功能 -->
          <el-card class="hm-card hm-func-card" shadow="never" body-style="padding: 0;">
            <div class="hm-card-head">
              <div class="hm-card-title-wrap">
                <div class="hm-card-icon func">
                  <i class="el-icon-menu"></i>
                </div>
                <div>
                  <h3 class="hm-card-title">快捷功能</h3>
                  <p class="hm-card-sub">常用功能一键直达</p>
                </div>
              </div>
            </div>
            <div class="hm-card-body">
              <div class="hm-func-grid">
                <div class="hm-func-item" v-for="(func, idx) in funcList" :key="idx" @click="navigateTo(func.path)">
                  <div class="hm-func-icon" :class="'fi-' + idx">
                    <i :class="func.icon"></i>
                  </div>
                  <span class="hm-func-name">{{ func.name }}</span>
                  <span class="hm-func-badge" v-if="func.badge">{{ func.badge }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import request from '@/utils/request'
import router from '@/router'

export default {
  name: 'Home',
  setup() {
    const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
    const loading = ref(true)
    const activeNotice = ref(0)
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

    const todayTasks = ref(28)
    const pendingTasks = ref(12)
    const completionRate = ref(98)

    const statsData = ref([
      { icon: 'el-icon-date', value: todayTasks.value, label: '今日任务', trend: 12 },
      { icon: 'el-icon-time', value: pendingTasks.value, label: '待处理', trend: -5 },
      { icon: 'el-icon-data-line', value: completionRate.value + '%', label: '完成率', trend: 8 }
    ])

    const funcList = ref([
      { name: '积分商城', icon: 'el-icon-goods', path: '/mall', badge: '热门' },
      { name: '公告管理', icon: 'el-icon-bell', path: '/notice', badge: '新' },
      { name: '个人中心', icon: 'el-icon-user', path: '/personal' },
      { name: '积分任务', icon: 'el-icon-medal', path: '/mall/points-tasks', badge: '推荐' },
      { name: '农田管理', icon: 'el-icon-location', path: '/farmland' },
      { name: '病虫害检测', icon: 'el-icon-bug', path: '/pest' },
      { name: '气象服务', icon: 'el-icon-weather', path: '/weather' },
      { name: '系统设置', icon: 'el-icon-setting', path: '/settings' },
    ])

    // 问候语
    const getGreeting = () => {
      const h = new Date().getHours()
      if (h < 6) return { text: '凌晨', period: '凌晨' }
      if (h < 9) return { text: '早上好', period: '早上' }
      if (h < 12) return { text: '上午好', period: '上午' }
      if (h < 14) return { text: '中午好', period: '中午' }
      if (h < 18) return { text: '下午好', period: '下午' }
      if (h < 22) return { text: '晚上好', period: '晚上' }
      return { text: '夜深了', period: '夜里' }
    }

    const greetingText = computed(() => getGreeting().text)
    const timePeriod = computed(() => getGreeting().period)

    const navigateTo = (path) => {
      if (!path) return
      const clean = path.startsWith('/') ? path : '/' + path
      try {
        if (router && typeof router.push === 'function') {
          router.push(clean)
          return
        }
      } catch (e) {
        console.warn('router.push 失败，回退到 hash 跳转', e)
      }
      window.location.hash = '#' + clean
    }

    const getNotices = async () => {
      try {
        loading.value = true
        const response = await request.get('/api/notification/page', {
          params: { pageNum: 1, pageSize: 10 }
        })
        if (response && response.records && Array.isArray(response.records)) {
          notices.value = response.records.map(item => ({
            id: item.id,
            title: item.name || item.title || '',
            content: item.content || '',
            time: item.time || '',
            type: item.type || 'info',
            tag: item.tag || '通知',
            image: item.img || null
          }))
        }
      } catch (error) {
        console.error('获取公告数据失败:', error)
        notices.value = [
          { title: '系统公告', content: '欢迎使用厚朴花开智慧农业平台', time: new Date().toLocaleString('zh-CN'), type: 'info', tag: '通知', image: null }
        ]
      } finally {
        loading.value = false
      }
    }

    const handleViewMoreNotices = () => {
      window.location.href = '/#/notice'
    }

    const getAvatarUrl = (avatarUrl) => {
      if (!avatarUrl) return null
      try {
        if (avatarUrl.startsWith('http')) return avatarUrl
        let url = ''
        if (!avatarUrl.includes('/')) url = '/api/file/' + avatarUrl
        else if (avatarUrl.startsWith('/api/')) url = avatarUrl
        else if (avatarUrl.startsWith('/')) url = '/api' + avatarUrl
        else return null
        const token = user.value.token
        const userId = user.value.id
        const urlObj = new URL(window.location.origin + url)
        urlObj.searchParams.append('userId', userId)
        urlObj.searchParams.append('token', token)
        return urlObj.toString()
      } catch (error) {
        return null
      }
    }

    const formatImageUrl = (imgUrl) => {
      if (!imgUrl) return ''
      if (imgUrl.startsWith('/api/file/')) return imgUrl
      if (imgUrl.startsWith('http') && imgUrl.includes('localhost')) {
        const fileName = imgUrl.split('/').pop()
        return `/api/file/${fileName}${fileName.includes('.') ? '' : '.jpg'}`
      }
      if (imgUrl.startsWith('http')) return imgUrl
      return `/api/file/${imgUrl}`
    }

    let userCheckInterval = null
    const checkUserInfo = () => {
      try {
        const currentUserStr = localStorage.getItem('user')
        if (currentUserStr) {
          const currentUser = JSON.parse(currentUserStr)
          if (currentUser.avatarUrl !== user.value.avatarUrl) {
            user.value = currentUser
          }
        }
      } catch (e) { console.error(e) }
    }

    onMounted(async () => {
      await getNotices()
      userCheckInterval = setInterval(checkUserInfo, 2000)
    })

    onUnmounted(() => {
      if (userCheckInterval) clearInterval(userCheckInterval)
    })

    return {
      user,
      loading,
      activeNotice,
      notices,
      weather,
      statsData,
      funcList,
      greetingText,
      timePeriod,
      navigateTo,
      getAvatarUrl,
      handleViewMoreNotices,
      formatImageUrl
    }
  }
}
</script>

<style scoped>
/* ===== 首页样式 ===== */
.hm-home {
  padding: 20px;
  min-height: 100vh;
  background: #f5f7fa;
  font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, sans-serif;
}

/* 欢迎区 */
.hm-welcome {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #10b981 0%, #059669 50%, #047857 100%);
  box-shadow: 0 10px 40px rgba(16, 185, 129, 0.25);
}

.hm-welcome-bg {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  overflow: hidden;
  pointer-events: none;
}

.hm-welcome-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.3;
  animation: hm-blob-move 15s ease-in-out infinite;
}

.hm-blob-1 {
  width: 300px; height: 300px;
  background: #34d399;
  top: -100px; right: 100px;
}

.hm-blob-2 {
  width: 250px; height: 250px;
  background: #6ee7b7;
  bottom: -80px; left: -50px;
  animation-delay: -5s;
}

.hm-blob-3 {
  width: 200px; height: 200px;
  background: #a7f3d0;
  top: 50%; left: 60%;
  animation-delay: -10s;
}

@keyframes hm-blob-move {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -20px) scale(1.15); }
}

.hm-welcome-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 36px 40px;
  gap: 30px;
}

.hm-welcome-info {
  flex: 1;
  min-width: 0;
}

.hm-welcome-greet {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 8px;
}

.hm-greet-text {
  font-size: 22px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.hm-username {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
}

.hm-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  font-size: 13px;
  color: #fff;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.hm-welcome-desc {
  margin: 0 0 24px 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* 统计卡 */
.hm-stats-row {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.hm-stat-card {
  flex: 1;
  min-width: 140px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 18px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.hm-stat-card:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.hm-stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.2);
}

.hm-stat-1 .hm-stat-icon { background: rgba(251, 191, 36, 0.3); }
.hm-stat-2 .hm-stat-icon { background: rgba(248, 113, 113, 0.3); }
.hm-stat-3 .hm-stat-icon { background: rgba(96, 165, 250, 0.3); }

.hm-stat-body {
  flex: 1;
  min-width: 0;
}

.hm-stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  line-height: 1.2;
  margin-bottom: 2px;
}

.hm-stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
}

.hm-stat-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 11px;
  font-weight: 600;
  padding: 3px 7px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.15);
}

.hm-stat-trend.up { color: #bbf7d0; }
.hm-stat-trend.down { color: #fecaca; }
.hm-stat-trend i { font-size: 9px; }

/* 头像区 */
.hm-welcome-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.hm-avatar-wrap {
  position: relative;
}

.hm-avatar-ring {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  border: 2px dashed rgba(255, 255, 255, 0.4);
  animation: hm-rotate 20s linear infinite;
}

@keyframes hm-rotate {
  to { transform: rotate(360deg); }
}

.hm-avatar-img {
  border: 4px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.hm-avatar-dot {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #22c55e;
  border: 3px solid #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.hm-avatar-info {
  text-align: center;
  color: #fff;
}

.hm-avatar-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 3px;
}

.hm-avatar-role {
  font-size: 12px;
  opacity: 0.85;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 内容区 */
.hm-content {
  animation: hm-fade-up 0.6s ease-out 0.2s both;
}

@keyframes hm-fade-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 通用卡片 */
.hm-card {
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06) !important;
  margin-bottom: 20px;
  transition: box-shadow 0.3s ease, transform 0.3s ease !important;
  overflow: hidden !important;
}

.hm-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1) !important;
}

.hm-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.hm-card-title-wrap {
  display: flex;
  align-items: center;
  gap: 14px;
}

.hm-card-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  background: linear-gradient(135deg, #10b981, #059669);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.hm-card-icon.weather {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.hm-card-icon.func {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.hm-card-title {
  margin: 0 0 3px 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.3;
}

.hm-card-sub {
  margin: 0;
  font-size: 12px;
  color: #94a3b8;
}

.hm-more-btn {
  color: #64748b !important;
  font-size: 13px !important;
  font-weight: 500 !important;
  display: flex !important;
  align-items: center;
  gap: 4px;
  padding: 4px 0 !important;
  transition: all 0.3s ease !important;
}

.hm-more-btn:hover {
  color: #10b981 !important;
  transform: translateX(3px);
}

.hm-card-body {
  padding: 20px 24px;
}

/* 实时标签 */
.hm-live-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: #dcfce7;
  color: #16a34a;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.hm-live-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #22c55e;
  animation: hm-pulse 2s ease-in-out infinite;
}

@keyframes hm-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.3); }
}

/* 公告列表 */
.hm-notice-list {
  display: flex;
  flex-direction: column;
}

.hm-notice-item {
  display: flex;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px dashed #e2e8f0;
  cursor: pointer;
  transition: all 0.25s ease;
}

.hm-notice-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.hm-notice-item:hover {
  padding-left: 6px;
}

.hm-notice-item.active {
  padding-left: 6px;
}

.hm-notice-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
  background: #94a3b8;
}

.hm-notice-dot.dot-info { background: #3b82f6; }
.hm-notice-dot.dot-warning { background: #f59e0b; }
.hm-notice-dot.dot-success { background: #10b981; }
.hm-notice-dot.dot-danger { background: #ef4444; }

.hm-notice-main {
  flex: 1;
  min-width: 0;
}

.hm-notice-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 6px;
}

.hm-notice-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.hm-notice-tag {
  flex-shrink: 0;
  transform: scale(0.9);
}

.hm-notice-bottom {
  display: flex;
  align-items: center;
  gap: 12px;
}

.hm-notice-time {
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  gap: 4px;
}

.hm-notice-detail {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #e2e8f0;
  animation: hm-slide-down 0.3s ease;
}

@keyframes hm-slide-down {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}

.hm-notice-img {
  width: 100%;
  height: 160px;
  border-radius: 10px;
  margin-bottom: 12px;
  object-fit: cover;
}

.hm-notice-text {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #64748b;
}

/* 天气卡片 */
.hm-weather-main {
  text-align: center;
  margin-bottom: 18px;
  padding: 18px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 14px;
}

.hm-weather-temp {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4px;
  margin-bottom: 6px;
}

.hm-temp-num {
  font-size: 48px;
  font-weight: 700;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}

.hm-temp-unit {
  font-size: 20px;
  color: #d97706;
  font-weight: 600;
}

.hm-weather-desc {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 15px;
  color: #92400e;
  font-weight: 500;
}

.hm-weather-desc i {
  font-size: 20px;
}

.hm-weather-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.hm-weather-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 10px;
  transition: all 0.25s ease;
}

.hm-weather-item:hover {
  background: #f1f5f9;
  transform: translateY(-2px);
}

.hm-w-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.12);
  flex-shrink: 0;
}

.hm-w-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.hm-w-label {
  font-size: 11px;
  color: #94a3b8;
}

.hm-w-value {
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

/* 快捷功能 */
.hm-func-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.hm-func-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 14px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s ease;
  position: relative;
  background: transparent;
}

.hm-func-item:hover {
  background: #f8fafc;
  transform: translateY(-3px);
}

.hm-func-item:hover .hm-func-icon {
  transform: scale(1.1) rotate(-5deg);
}

.hm-func-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  transition: all 0.3s ease;
}

.hm-func-icon.fi-0 { background: linear-gradient(135deg, #f472b6, #ec4899); box-shadow: 0 4px 10px rgba(236, 72, 153, 0.3); }
.hm-func-icon.fi-1 { background: linear-gradient(135deg, #60a5fa, #3b82f6); box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3); }
.hm-func-icon.fi-2 { background: linear-gradient(135deg, #34d399, #10b981); box-shadow: 0 4px 10px rgba(16, 185, 129, 0.3); }
.hm-func-icon.fi-3 { background: linear-gradient(135deg, #fbbf24, #f59e0b); box-shadow: 0 4px 10px rgba(245, 158, 11, 0.3); }
.hm-func-icon.fi-4 { background: linear-gradient(135deg, #a78bfa, #8b5cf6); box-shadow: 0 4px 10px rgba(139, 92, 246, 0.3); }
.hm-func-icon.fi-5 { background: linear-gradient(135deg, #f87171, #ef4444); box-shadow: 0 4px 10px rgba(239, 68, 68, 0.3); }
.hm-func-icon.fi-6 { background: linear-gradient(135deg, #38bdf8, #0ea5e9); box-shadow: 0 4px 10px rgba(14, 165, 233, 0.3); }
.hm-func-icon.fi-7 { background: linear-gradient(135deg, #94a3b8, #64748b); box-shadow: 0 4px 10px rgba(100, 116, 139, 0.3); }

.hm-func-name {
  font-size: 12px;
  color: #475569;
  font-weight: 500;
  text-align: center;
}

.hm-func-badge {
  position: absolute;
  top: 6px;
  right: 6px;
  padding: 1px 6px;
  background: #ef4444;
  color: #fff;
  font-size: 10px;
  border-radius: 8px;
  font-weight: 600;
  line-height: 1.4;
}

/* 响应式 */
@media (max-width: 1200px) {
  .hm-stats-row {
    gap: 10px;
  }
  .hm-stat-card {
    min-width: 120px;
    padding: 12px 14px;
  }
  .hm-stat-value {
    font-size: 18px;
  }
}

@media (max-width: 992px) {
  .hm-welcome-inner {
    flex-direction: column;
    text-align: center;
    padding: 28px 24px;
  }
  .hm-welcome-greet {
    justify-content: center;
  }
  .hm-stats-row {
    justify-content: center;
  }
  .hm-welcome-avatar {
    order: -1;
  }
}

@media (max-width: 768px) {
  .hm-home {
    padding: 12px;
  }
  .hm-welcome {
    border-radius: 14px;
  }
  .hm-welcome-inner {
    padding: 22px 18px;
  }
  .hm-username {
    font-size: 22px;
  }
  .hm-greet-text {
    font-size: 18px;
  }
  .hm-stats-row {
    flex-direction: column;
  }
  .hm-stat-card {
    width: 100%;
  }
  .hm-card-head {
    padding: 16px 18px;
  }
  .hm-card-body {
    padding: 16px 18px;
  }
  .hm-func-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 8px;
  }
  .hm-func-icon {
    width: 38px;
    height: 38px;
    font-size: 16px;
  }
  .hm-func-name {
    font-size: 11px;
  }
  .hm-weather-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
