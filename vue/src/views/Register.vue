<template>
  <div class="wrapper" :class="{ 'dark-mode': isDarkMode }">
    <!-- 浮动装饰气泡 -->
    <div class="bubbles">
      <div class="bubble" v-for="n in 15" :key="n" :style="bubbleStyle(n)"></div>
    </div>

    <!-- 主容器 -->
    <div class="main-container">
      <!-- 左侧品牌区 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo-wrapper">
            <img src="../assets/logo.png" alt="平台Logo" class="brand-logo">
          </div>
          <h1 class="brand-title">枣往果来</h1>
          <p class="brand-subtitle">酸枣种植风险预警智能决策系统</p>
          <div class="brand-divider"></div>
          <div class="brand-features">
            <div class="feature-item">
              <i class="el-icon-data-analysis"></i>
              <span>智能数据分析</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-warning-outline"></i>
              <span>风险预警提醒</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-s-tools"></i>
              <span>科学种植决策</span>
            </div>
          </div>
        </div>
        <div class="brand-footer">
          <el-switch
              v-model="isDarkMode"
              active-text="夜间"
              inactive-text="日间"
              active-color="#ff8a80"
              inactive-color="#64b5f6"
              @change="toggleTheme">
          </el-switch>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="form-section">
        <div class="form-scroll">
          <div class="glass-card">
            <!-- 注册头部 -->
            <div class="register-header">
              <div class="header-icon">
                <i class="el-icon-user-plus"></i>
              </div>
              <h2>创建账号</h2>
              <p class="subtitle">开启您的智慧种植之旅</p>
            </div>

            <!-- 步骤指示器 -->
            <div class="step-indicator">
              <div class="step" :class="{ active: currentStep >= 1, done: currentStep > 1 }">
                <div class="step-circle">1</div>
                <span>基本信息</span>
              </div>
              <div class="step-line" :class="{ active: currentStep > 1 }"></div>
              <div class="step" :class="{ active: currentStep >= 2, done: currentStep > 2 }">
                <div class="step-circle">2</div>
                <span>联系方式</span>
              </div>
              <div class="step-line" :class="{ active: currentStep > 2 }"></div>
              <div class="step" :class="{ active: currentStep >= 3 }">
                <div class="step-circle">3</div>
                <span>完善资料</span>
              </div>
            </div>

            <!-- 注册表单 -->
            <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="register-form">
              <!-- 步骤1：基本信息 -->
              <transition name="fade-slide" mode="out-in">
                <div v-if="currentStep === 1" key="step1" class="form-step">
                  <!-- 用户名 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-user"></i> 用户名
                    </label>
                    <el-input
                        v-model="registerForm.username"
                        class="form-input"
                        placeholder="请输入用户名（3-20个字符）"
                        @blur="handleUsernameBlur">
                    </el-input>
                    <transition name="fade-up">
                      <div v-if="usernameStatus" class="status-indicator">
                        <i :class="usernameStatus === 'checking' ? 'el-icon-loading' :
                                  usernameStatus === 'available' ? 'el-icon-circle-check' : 'el-icon-circle-close'"
                           :style="{ color: statusColor(usernameStatus) }"></i>
                        <span :style="{ color: statusColor(usernameStatus) }">
                          {{ statusText(usernameStatus, '用户名') }}
                        </span>
                      </div>
                    </transition>
                  </div>

                  <!-- 昵称 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-postcard"></i> 昵称
                    </label>
                    <el-input
                        v-model="registerForm.nickname"
                        class="form-input"
                        placeholder="请输入昵称（2-20个字符）">
                    </el-input>
                  </div>

                  <!-- 密码 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-lock"></i> 密码
                    </label>
                    <el-input
                        v-model="registerForm.password"
                        type="password"
                        class="form-input"
                        placeholder="请输入密码（6-20个字符）"
                        show-password>
                    </el-input>
                    <!-- 密码强度指示器 -->
                    <div v-if="registerForm.password" class="password-strength">
                      <div class="strength-bar">
                        <div class="strength-fill" :class="strengthClass"></div>
                      </div>
                      <span class="strength-text" :class="strengthClass">{{ strengthText }}</span>
                    </div>
                  </div>

                  <!-- 确认密码 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-lock"></i> 确认密码
                    </label>
                    <el-input
                        v-model="registerForm.confirmPassword"
                        type="password"
                        class="form-input"
                        placeholder="请再次输入密码"
                        show-password>
                    </el-input>
                    <transition name="fade-up">
                      <div v-if="registerForm.confirmPassword && registerForm.password !== registerForm.confirmPassword"
                           class="status-indicator error">
                        <i class="el-icon-circle-close"></i>
                        <span>两次输入密码不一致</span>
                      </div>
                    </transition>
                  </div>

                  <div class="step-actions">
                    <el-button class="btn-next" @click="validateStep1">
                      下一步 <i class="el-icon-arrow-right"></i>
                    </el-button>
                  </div>
                </div>

                <!-- 步骤2：联系方式 -->
                <div v-else-if="currentStep === 2" key="step2" class="form-step">
                  <!-- 手机号 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-phone"></i> 手机号
                    </label>
                    <div class="phone-input-container">
                      <el-input
                          v-model="registerForm.phone"
                          class="form-input phone-input"
                          placeholder="请输入手机号"
                          @blur="handlePhoneBlur">
                        <template #prefix>
                          <span class="phone-prefix">+86</span>
                        </template>
                      </el-input>
                      <el-button
                          class="code-button"
                          :disabled="!canSendCode || countdown > 0"
                          @click="sendSmsCode">
                        {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
                      </el-button>
                    </div>
                    <transition name="fade-up">
                      <div v-if="phoneStatus" class="status-indicator">
                        <i :class="phoneStatus === 'checking' ? 'el-icon-loading' :
                                  phoneStatus === 'available' ? 'el-icon-circle-check' : 'el-icon-circle-close'"
                           :style="{ color: statusColor(phoneStatus) }"></i>
                        <span :style="{ color: statusColor(phoneStatus) }">
                          {{ statusText(phoneStatus, '手机号') }}
                        </span>
                      </div>
                    </transition>
                  </div>

                  <!-- 验证码 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-message"></i> 验证码
                    </label>
                    <el-input
                        v-model="registerForm.code"
                        class="form-input"
                        placeholder="请输入6位验证码"
                        maxlength="6">
                    </el-input>
                  </div>

                  <!-- 邮箱 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-message-solid"></i> 邮箱 <span class="optional-label">（可选）</span>
                    </label>
                    <el-input
                        v-model="registerForm.email"
                        class="form-input"
                        placeholder="请输入邮箱地址"
                        @blur="handleEmailBlur">
                    </el-input>
                    <transition name="fade-up">
                      <div v-if="emailStatus && registerForm.email" class="status-indicator">
                        <i :class="emailStatus === 'checking' ? 'el-icon-loading' :
                                  emailStatus === 'available' ? 'el-icon-circle-check' : 'el-icon-circle-close'"
                           :style="{ color: statusColor(emailStatus) }"></i>
                        <span :style="{ color: statusColor(emailStatus) }">
                          {{ statusText(emailStatus, '邮箱') }}
                        </span>
                      </div>
                    </transition>
                  </div>

                  <div class="step-actions">
                    <el-button class="btn-prev" @click="prevStep">
                      <i class="el-icon-arrow-left"></i> 上一步
                    </el-button>
                    <el-button class="btn-next" @click="validateStep2">
                      下一步 <i class="el-icon-arrow-right"></i>
                    </el-button>
                  </div>
                </div>

                <!-- 步骤3：完善资料 -->
                <div v-else-if="currentStep === 3" key="step3" class="form-step">
                  <!-- 地址 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-location-outline"></i> 地址 <span class="optional-label">（可选）</span>
                    </label>
                    <el-input
                        v-model="registerForm.address"
                        class="form-input"
                        placeholder="请输入您的地址">
                    </el-input>
                  </div>

                  <!-- 用户角色 -->
                  <div class="form-group">
                    <label class="form-label">
                      <i class="el-icon-s-custom"></i> 用户角色
                    </label>
                    <el-select v-model="registerForm.role" placeholder="请选择用户角色" class="form-input role-select">
                      <el-option label="普通用户" value="ROLE_USER">
                        <div class="role-option">
                          <i class="el-icon-user"></i>
                          <span>普通用户</span>
                          <small>基础功能权限</small>
                        </div>
                      </el-option>
                      <el-option label="管理员" value="ROLE_ADMIN">
                        <div class="role-option">
                          <i class="el-icon-s-custom"></i>
                          <span>管理员</span>
                          <small>全部系统权限</small>
                        </div>
                      </el-option>
                      <el-option label="农技员" value="ROLE_USER1">
                        <div class="role-option">
                          <i class="el-icon-s-management"></i>
                          <span>农技员</span>
                          <small>技术指导权限</small>
                        </div>
                      </el-option>
                      <el-option label="种植户" value="ROLE_USER2">
                        <div class="role-option">
                          <i class="el-icon-goods"></i>
                          <span>种植户</span>
                          <small>种植管理权限</small>
                        </div>
                      </el-option>
                      <el-option label="收购商" value="ROLE_USER3">
                        <div class="role-option">
                          <i class="el-icon-shopping-cart-2"></i>
                          <span>收购商</span>
                          <small>采购查看权限</small>
                        </div>
                      </el-option>
                      <el-option label="科研人员" value="ROLE_USER4">
                        <div class="role-option">
                          <i class="el-icon-data-line"></i>
                          <span>科研人员</span>
                          <small>数据分析权限</small>
                        </div>
                      </el-option>
                      <el-option label="监管员" value="ROLE_USER5">
                        <div class="role-option">
                          <i class="el-icon-view"></i>
                          <span>监管员</span>
                          <small>监管查看权限</small>
                        </div>
                      </el-option>
                    </el-select>

                    <transition name="fade-up">
                      <div v-if="registerForm.role" class="role-description">
                        <div :class="['role-info', roleInfoClass]">
                          <i :class="roleIconClass"></i>
                          <span>{{ roleDescription }}</span>
                        </div>
                      </div>
                    </transition>
                  </div>

                  <!-- 协议同意 -->
                  <div class="form-group agreement-group">
                    <el-checkbox v-model="agreedToTerms" class="agreement-checkbox">
                      我已阅读并同意
                      <span class="link-text" @click.stop>《用户协议》</span>
                      和
                      <span class="link-text" @click.stop>《隐私政策》</span>
                    </el-checkbox>
                  </div>

                  <div class="step-actions">
                    <el-button class="btn-prev" @click="prevStep">
                      <i class="el-icon-arrow-left"></i> 上一步
                    </el-button>
                    <el-button
                        type="primary"
                        class="btn-register"
                        :loading="registerLoading"
                        :disabled="!agreedToTerms"
                        @click="handleRegister">
                      <i class="el-icon-check"></i> 立即注册
                    </el-button>
                  </div>
                </div>
              </transition>
            </el-form>

            <!-- 登录链接 -->
            <div class="login-link">
              <span>已有账号？</span>
              <router-link to="/login" class="link">立即登录</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Register',
  data() {
    return {
      isDarkMode: false,
      registerLoading: false,
      countdown: 0,
      canSendCode: false,
      currentStep: 1,
      agreedToTerms: false,
      timer: null,

      usernameStatus: null,
      phoneStatus: null,
      emailStatus: null,

      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        phone: '',
        code: '',
        email: '',
        address: '',
        role: 'ROLE_USER'
      },

      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 20, message: '昵称长度在2到20个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { pattern: /^\d{6}$/, message: '验证码必须是6位数字', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择用户角色', trigger: 'change' }
        ]
      }
    }
  },

  computed: {
    passwordStrength() {
      const pwd = this.registerForm.password
      if (!pwd) return 0
      let score = 0
      if (pwd.length >= 8) score++
      if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) score++
      if (/\d/.test(pwd)) score++
      if (/[^a-zA-Z0-9]/.test(pwd)) score++
      return Math.min(score, 4)
    },
    strengthClass() {
      const s = this.passwordStrength
      if (s <= 1) return 'weak'
      if (s === 2) return 'medium'
      if (s === 3) return 'strong'
      return 'very-strong'
    },
    strengthText() {
      const s = this.passwordStrength
      if (s <= 1) return '弱'
      if (s === 2) return '中等'
      if (s === 3) return '强'
      return '非常强'
    },
    roleDescription() {
      const map = {
        'ROLE_ADMIN': '管理员拥有系统全部权限，可管理用户、配置系统、查看所有数据',
        'ROLE_USER': '普通用户拥有基本功能权限，可查看和管理自己的种植数据',
        'ROLE_USER1': '农技员可提供技术指导，查看病虫害数据并发布建议',
        'ROLE_USER2': '种植户可管理自家枣园，查看预警信息和种植建议',
        'ROLE_USER3': '收购商可查看产地信息、品质数据和收购价格',
        'ROLE_USER4': '科研人员可访问科研数据，进行分析研究',
        'ROLE_USER5': '监管员可查看监管数据，进行质量安全监控'
      }
      return map[this.registerForm.role] || '请选择用户角色'
    },
    roleInfoClass() {
      const map = {
        'ROLE_ADMIN': 'admin-info',
        'ROLE_USER': 'user-info',
        'ROLE_USER1': 'tech-info',
        'ROLE_USER2': 'farmer-info',
        'ROLE_USER3': 'buyer-info',
        'ROLE_USER4': 'research-info',
        'ROLE_USER5': 'supervisor-info'
      }
      return map[this.registerForm.role] || 'custom-info'
    },
    roleIconClass() {
      const map = {
        'ROLE_ADMIN': 'el-icon-s-custom',
        'ROLE_USER': 'el-icon-user',
        'ROLE_USER1': 'el-icon-s-management',
        'ROLE_USER2': 'el-icon-goods',
        'ROLE_USER3': 'el-icon-shopping-cart-2',
        'ROLE_USER4': 'el-icon-data-line',
        'ROLE_USER5': 'el-icon-view'
      }
      return map[this.registerForm.role] || 'el-icon-star-on'
    }
  },

  watch: {
    'registerForm.phone'(newVal) {
      this.canSendCode = /^1[3-9]\d{9}$/.test(newVal)
    }
  },

  methods: {
    toggleTheme() {
      localStorage.setItem('isDarkMode', this.isDarkMode)
    },

    bubbleStyle(n) {
      const size = 20 + Math.random() * 60
      const left = Math.random() * 100
      const delay = Math.random() * 15
      const duration = 10 + Math.random() * 20
      return {
        width: size + 'px',
        height: size + 'px',
        left: left + '%',
        animationDelay: delay + 's',
        animationDuration: duration + 's'
      }
    },

    statusColor(status) {
      if (status === 'available') return '#52c41a'
      if (status === 'unavailable') return '#ff4d4f'
      return '#1890ff'
    },

    statusText(status, field) {
      if (status === 'checking') return '检查中...'
      if (status === 'available') return `${field}可用`
      if (status === 'unavailable') return `${field}已被注册`
      return ''
    },

    async handleUsernameBlur() {
      if (!this.registerForm.username || this.registerForm.username.length < 3) {
        this.usernameStatus = null
        return
      }
      this.usernameStatus = 'checking'
      try {
        const res = await request.get(`/api/user/checkUsername?username=${this.registerForm.username}`)
        this.usernameStatus = res.data ? 'available' : 'unavailable'
      } catch (error) {
        console.error('检查用户名失败:', error)
        this.usernameStatus = null
      }
    },

    async handlePhoneBlur() {
      if (!this.registerForm.phone || !/^1[3-9]\d{9}$/.test(this.registerForm.phone)) {
        this.phoneStatus = null
        return
      }
      this.phoneStatus = 'checking'
      try {
        const res = await request.get(`/api/user/checkPhone?phone=${this.registerForm.phone}`)
        this.phoneStatus = res.data ? 'available' : 'unavailable'
      } catch (error) {
        console.error('检查手机号失败:', error)
        this.phoneStatus = null
      }
    },

    async handleEmailBlur() {
      if (!this.registerForm.email || !this.registerForm.email.trim()) {
        this.emailStatus = null
        return
      }
      this.emailStatus = 'checking'
      try {
        const res = await request.get(`/api/user/checkEmail?email=${this.registerForm.email}`)
        this.emailStatus = res.data ? 'available' : 'unavailable'
      } catch (error) {
        console.error('检查邮箱失败:', error)
        this.emailStatus = null
      }
    },

    async sendSmsCode() {
      if (!this.canSendCode) {
        this.$message.error('请输入正确的手机号')
        return
      }
      try {
        const res = await request.post(`/api/user/sendCode?phone=${this.registerForm.phone}`)
        if (res.code === '200' || res.code === 200) {
          this.$message.success('短信验证码已发送')
          this.startCountdown()
        } else {
          this.$message.error(res.msg || '发送失败，请稍后重试')
        }
      } catch (error) {
        console.error('验证码发送错误:', error)
        this.$message.error('发送失败，请稍后重试')
      }
    },

    startCountdown() {
      this.countdown = 60
      if (this.timer) clearInterval(this.timer)
      this.timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(this.timer)
          this.timer = null
        }
      }, 1000)
    },

    async validateStep1() {
      const valid = await this.validateFields(['username', 'nickname', 'password', 'confirmPassword'])
      if (!valid) return
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.$message.error('两次输入密码不一致')
        return
      }
      if (this.usernameStatus !== 'available') {
        this.$message.error('请检查用户名是否可用')
        return
      }
      this.currentStep = 2
    },

    async validateStep2() {
      const valid = await this.validateFields(['phone', 'code', 'email'])
      if (!valid) return
      if (this.phoneStatus !== 'available') {
        this.$message.error('请检查手机号是否可用')
        return
      }
      if (this.registerForm.email && this.emailStatus === 'unavailable') {
        this.$message.error('邮箱已被注册，请使用其他邮箱')
        return
      }
      this.currentStep = 3
    },

    validateFields(fields) {
      return new Promise((resolve) => {
        this.$refs.registerFormRef.validateField(fields, (errors) => {
          resolve(!errors)
        })
      })
    },

    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--
      }
    },

    async handleRegister() {
      if (!this.agreedToTerms) {
        this.$message.error('请先阅读并同意用户协议和隐私政策')
        return
      }

      try {
        await this.$refs.registerFormRef.validate()
        this.registerLoading = true

        const res = await request.post('/api/user/register', this.registerForm)

        if (res.code === '200' || res.code === 200) {
          this.$message.success(res.data.message || '注册成功！即将跳转到登录页...')
          setTimeout(() => {
            this.$router.push('/login')
          }, 2000)
        } else {
          this.$message.error(res.msg || '注册失败，请稍后重试')
        }
      } catch (error) {
        console.error('注册失败:', error)
        if (error.response && error.response.data && error.response.data.msg) {
          this.$message.error(error.response.data.msg)
        } else {
          this.$message.error('注册失败，请稍后重试')
        }
      } finally {
        this.registerLoading = false
      }
    }
  },

  mounted() {
    const savedTheme = localStorage.getItem('isDarkMode')
    if (savedTheme !== null) {
      this.isDarkMode = JSON.parse(savedTheme)
    }
  },

  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
.wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 50%, #2ecc71 100%);
  display: flex;
  position: relative;
  overflow: hidden;
}

.wrapper.dark-mode {
  background: linear-gradient(135deg, #093028 0%, #237a57 50%, #11998e 100%);
}

/* 浮动气泡 */
.bubbles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
  z-index: 1;
}

.bubble {
  position: absolute;
  bottom: -100px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  animation: float-up linear infinite;
  backdrop-filter: blur(5px);
}

@keyframes float-up {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-110vh) rotate(360deg);
    opacity: 0;
  }
}

/* 主容器 */
.main-container {
  display: flex;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

/* 左侧品牌区 */
.brand-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 60px;
  color: white;
}

.brand-content {
  text-align: center;
  animation: fadeInLeft 0.8s ease-out;
}

.logo-wrapper {
  width: 100px;
  height: 100px;
  margin: 0 auto 30px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.brand-logo {
  width: 60px;
  height: 60px;
}

.brand-title {
  font-size: 48px;
  font-weight: 800;
  margin: 0 0 15px 0;
  text-shadow: 2px 4px 8px rgba(0, 0, 0, 0.2);
  background: linear-gradient(to bottom, #fff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  font-size: 18px;
  margin: 0 0 30px 0;
  opacity: 0.9;
  font-weight: 300;
  letter-spacing: 2px;
}

.brand-divider {
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, rgba(255,255,255,0.5), rgba(255,255,255,0.8), rgba(255,255,255,0.5));
  margin: 0 auto 40px;
  border-radius: 2px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 25px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateX(5px);
  background: rgba(255, 255, 255, 0.25);
}

.feature-item i {
  font-size: 24px;
}

.feature-item span {
  font-size: 16px;
  font-weight: 500;
}

.brand-footer {
  margin-top: 50px;
}

/* 右侧表单区 */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 60px 40px 20px;
}

.form-scroll {
  width: 100%;
  max-height: 92vh;
  overflow-y: auto;
  padding: 10px;
}

.form-scroll::-webkit-scrollbar {
  width: 6px;
}

.form-scroll::-webkit-scrollbar-thumb {
  background: rgba(46, 204, 113, 0.3);
  border-radius: 3px;
}

/* 玻璃卡片 */
.glass-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(30px);
  border-radius: 24px;
  padding: 45px 45px 30px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.5);
  width: 100%;
  max-width: 480px;
  margin: 0 auto;
  animation: fadeInRight 0.8s ease-out;
}

.dark-mode .glass-card {
  background: rgba(30, 41, 59, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 注册头部 */
.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.header-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(56, 249, 215, 0.4);
}

.header-icon i {
  font-size: 32px;
  color: white;
}

.register-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 8px 0;
}

.dark-mode .register-header h2 {
  color: #f1f5f9;
}

.subtitle {
  color: #94a3b8;
  font-size: 14px;
  margin: 0;
}

.dark-mode .subtitle {
  color: #64748b;
}

/* 步骤指示器 */
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  padding: 0 10px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.step-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e2e8f0;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
}

.dark-mode .step-circle {
  background: #334155;
  color: #64748b;
}

.step.active .step-circle {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  color: white;
  box-shadow: 0 4px 12px rgba(46, 204, 113, 0.4);
}

.step.done .step-circle {
  background: linear-gradient(135deg, #52c41a, #389e0d);
  color: white;
}

.step span {
  font-size: 12px;
  color: #94a3b8;
  white-space: nowrap;
}

.dark-mode .step span {
  color: #64748b;
}

.step.active span {
  color: #2ecc71;
  font-weight: 500;
}

.step-line {
  flex: 1;
  height: 2px;
  background: #e2e8f0;
  margin: 0 8px;
  border-radius: 1px;
  transform: translateY(-10px);
}

.dark-mode .step-line {
  background: #334155;
}

.step-line.active {
  background: linear-gradient(90deg, #52c41a, #389e0d);
}

/* 表单样式 */
.register-form {
  width: 100%;
}

.form-step {
  min-height: 320px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 8px;
}

.dark-mode .form-label {
  color: #e2e8f0;
}

.form-label i {
  color: #2ecc71;
  font-size: 14px;
}

.optional-label {
  font-weight: 400;
  color: #94a3b8;
  font-size: 12px;
}

.dark-mode .optional-label {
  color: #64748b;
}

.form-input {
  width: 100%;
}

.form-input :deep(.el-input__inner) {
  height: 46px;
  border-radius: 12px;
  border: 1.5px solid #e2e8f0;
  padding: 0 16px;
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #f8fafc;
}

.form-input :deep(.el-input__inner):focus {
  border-color: #2ecc71;
  box-shadow: 0 0 0 4px rgba(46, 204, 113, 0.1);
  background: white;
}

.dark-mode .form-input :deep(.el-input__inner) {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark-mode .form-input :deep(.el-input__inner):focus {
  background: #1e293b;
  border-color: #2ecc71;
}

/* 密码强度 */
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}

.strength-bar {
  flex: 1;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.dark-mode .strength-bar {
  background: #334155;
}

.strength-fill {
  height: 100%;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.strength-fill.weak {
  width: 25%;
  background: linear-gradient(90deg, #ff4d4f, #ff7875);
}

.strength-fill.medium {
  width: 50%;
  background: linear-gradient(90deg, #faad14, #ffc53d);
}

.strength-fill.strong {
  width: 75%;
  background: linear-gradient(90deg, #52c41a, #73d13d);
}

.strength-fill.very-strong {
  width: 100%;
  background: linear-gradient(90deg, #13c2c2, #36cfc9);
}

.strength-text {
  font-size: 12px;
  font-weight: 500;
  min-width: 50px;
}

.strength-text.weak { color: #ff4d4f; }
.strength-text.medium { color: #faad14; }
.strength-text.strong { color: #52c41a; }
.strength-text.very-strong { color: #13c2c2; }

/* 手机号输入 */
.phone-input-container {
  display: flex;
  gap: 10px;
}

.phone-input {
  flex: 1;
}

.phone-prefix {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.dark-mode .phone-prefix {
  color: #94a3b8;
}

.code-button {
  height: 46px;
  padding: 0 18px;
  border-radius: 12px;
  font-size: 13px;
  white-space: nowrap;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: white;
  transition: all 0.3s ease;
}

.code-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(46, 204, 113, 0.4);
}

.code-button:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
  color: white;
  opacity: 0.7;
}

.dark-mode .code-button:disabled {
  background: #475569;
}

/* 状态指示器 */
.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 12px;
}

.status-indicator.error {
  color: #ff4d4f;
}

.status-indicator i {
  font-size: 14px;
}

/* 角色选择 */
.role-select {
  width: 100%;
}

.role-select :deep(.el-input__inner) {
  height: 46px;
  border-radius: 12px;
  border: 1.5px solid #e2e8f0;
  padding: 0 16px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.dark-mode .role-select :deep(.el-input__inner) {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.role-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 0;
}

.role-option i {
  color: #2ecc71;
  font-size: 16px;
}

.role-option span {
  flex: 1;
  font-size: 14px;
  color: #1e293b;
}

.role-option small {
  font-size: 12px;
  color: #94a3b8;
}

/* 角色描述 */
.role-description {
  margin-top: 10px;
}

.role-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.5;
  animation: fade-up 0.3s ease-out;
}

.role-info i {
  font-size: 18px;
  flex-shrink: 0;
}

.admin-info {
  background: linear-gradient(135deg, rgba(255, 77, 79, 0.1), rgba(255, 120, 117, 0.1));
  color: #cf1322;
  border-left: 3px solid #ff4d4f;
}

.user-info {
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.1), rgba(64, 169, 255, 0.1));
  color: #096dd9;
  border-left: 3px solid #1890ff;
}

.tech-info {
  background: linear-gradient(135deg, rgba(82, 196, 26, 0.1), rgba(115, 209, 61, 0.1));
  color: #389e0d;
  border-left: 3px solid #52c41a;
}

.farmer-info {
  background: linear-gradient(135deg, rgba(250, 173, 20, 0.1), rgba(255, 197, 61, 0.1));
  color: #d48806;
  border-left: 3px solid #faad14;
}

.buyer-info {
  background: linear-gradient(135deg, rgba(114, 46, 209, 0.1), rgba(147, 86, 229, 0.1));
  color: #531dab;
  border-left: 3px solid #722ed1;
}

.research-info {
  background: linear-gradient(135deg, rgba(19, 194, 194, 0.1), rgba(54, 207, 201, 0.1));
  color: #08979c;
  border-left: 3px solid #13c2c2;
}

.supervisor-info {
  background: linear-gradient(135deg, rgba(235, 47, 150, 0.1), rgba(236, 95, 165, 0.1));
  color: #c41d7f;
  border-left: 3px solid #eb2f96;
}

.custom-info {
  background: linear-gradient(135deg, rgba(162, 155, 254, 0.1), rgba(108, 92, 231, 0.1));
  color: #531dab;
  border-left: 3px solid #a29bfe;
}

/* 协议 */
.agreement-group {
  margin-bottom: 20px;
}

.agreement-checkbox {
  font-size: 13px;
  color: #64748b;
}

.dark-mode .agreement-checkbox {
  color: #94a3b8;
}

.link-text {
  color: #2ecc71;
  cursor: pointer;
  transition: color 0.2s;
}

.link-text:hover {
  color: #27ae60;
  text-decoration: underline;
}

/* 步骤按钮 */
.step-actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
}

.btn-prev,
.btn-next,
.btn-register {
  flex: 1;
  height: 48px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-prev {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.dark-mode .btn-prev {
  background: #334155;
  border-color: #475569;
  color: #94a3b8;
}

.btn-prev:hover {
  background: #e2e8f0;
}

.dark-mode .btn-prev:hover {
  background: #475569;
}

.btn-next {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: white;
}

.btn-next:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(56, 249, 215, 0.4);
  color: white;
}

.btn-register {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: white;
}

.btn-register:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(56, 249, 215, 0.4);
  color: white;
}

.btn-register:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
  color: white;
}

.dark-mode .btn-register:disabled {
  background: #475569;
}

/* 登录链接 */
.login-link {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
  font-size: 13px;
  color: #94a3b8;
}

.dark-mode .login-link {
  color: #64748b;
  border-top-color: #334155;
}

.link {
  color: #2ecc71;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
  margin-left: 4px;
}

.link:hover {
  color: #27ae60;
  text-decoration: underline;
}

/* 动画 */
@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-up-enter-active,
.fade-up-leave-active {
  transition: all 0.3s ease;
}

.fade-up-enter,
.fade-up-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* 响应式 */
@media (max-width: 960px) {
  .main-container {
    flex-direction: column;
  }

  .brand-section {
    padding: 40px 30px 20px;
  }

  .brand-title {
    font-size: 32px;
  }

  .brand-features {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
  }

  .feature-item {
    padding: 10px 16px;
  }

  .form-section {
    padding: 20px 20px 40px;
  }

  .glass-card {
    padding: 30px 25px 20px;
  }
}

@media (max-width: 600px) {
  .brand-section {
    padding: 30px 20px 10px;
  }

  .brand-title {
    font-size: 28px;
  }

  .brand-subtitle {
    font-size: 14px;
  }

  .brand-features {
    display: none;
  }

  .glass-card {
    padding: 25px 20px 15px;
    border-radius: 18px;
  }

  .register-header h2 {
    font-size: 22px;
  }

  .header-icon {
    width: 52px;
    height: 52px;
    border-radius: 16px;
  }

  .header-icon i {
    font-size: 26px;
  }

  .step span {
    display: none;
  }

  .phone-input-container {
    flex-direction: column;
  }

  .code-button {
    width: 100%;
  }
}
</style>
