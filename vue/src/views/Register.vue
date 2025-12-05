<template>
  <div class="wrapper" :class="{ 'dark-mode': isDarkMode }">
    <!-- 顶部栏 -->
    <div class="header">
      <img src="../assets/logo.png" alt="平台Logo" class="logo">
      <div class="title">枣往果来-酸枣种植风险预警智能决策系统</div>
      <el-switch
          v-model="isDarkMode"
          class="theme-switch"
          active-color="#13ce66"
          inactive-color="#ff4949"
          @change="toggleTheme">
      </el-switch>
    </div>

    <!-- 注册表单容器 -->
    <div class="register-container">
      <div class="glass-card">
        <div class="register-header">
          <h2>用户注册</h2>
          <p class="subtitle">加入酸枣种植风险预警智能决策系统</p>
        </div>

        <!-- 注册表单 -->
        <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="register-form">
          <!-- 用户名 -->
          <div class="form-group">
            <label class="form-label">用户名</label>
            <el-input
                v-model="registerForm.username"
                class="form-input"
                placeholder="请输入用户名（3-20个字符）"
                @focus="handleFocus"
                @blur="handleUsernameBlur">
              <template #prefix>
                <i class="el-icon-user"></i>
              </template>
            </el-input>
            <div v-if="usernameStatus" class="status-indicator">
              <i :class="usernameStatus === 'checking' ? 'el-icon-loading' : 
                        usernameStatus === 'available' ? 'el-icon-success' : 'el-icon-error'"
                 :style="{ color: usernameStatus === 'available' ? '#67c23a' : 
                           usernameStatus === 'unavailable' ? '#f56c6c' : '#409eff' }"></i>
              <span v-if="usernameStatus === 'available'">用户名可用</span>
              <span v-else-if="usernameStatus === 'unavailable'">用户名已存在</span>
              <span v-else-if="usernameStatus === 'checking'">检查中...</span>
            </div>
          </div>

          <!-- 密码 -->
          <div class="form-group">
            <label class="form-label">密码</label>
            <el-input
                v-model="registerForm.password"
                type="password"
                class="form-input"
                placeholder="请输入密码（6-20个字符）"
                show-password
                @focus="handleFocus"
                @blur="handleBlur">
              <template #prefix>
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </div>

          <!-- 确认密码 -->
          <div class="form-group">
            <label class="form-label">确认密码</label>
            <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                class="form-input"
                placeholder="请再次输入密码"
                show-password
                @focus="handleFocus"
                @blur="handleBlur">
              <template #prefix>
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </div>

          <!-- 昵称 -->
          <div class="form-group">
            <label class="form-label">昵称</label>
            <el-input
                v-model="registerForm.nickname"
                class="form-input"
                placeholder="请输入昵称（2-20个字符）"
                @focus="handleFocus"
                @blur="handleBlur">
              <template #prefix>
                <i class="el-icon-user-solid"></i>
              </template>
            </el-input>
          </div>

          <!-- 手机号 -->
          <div class="form-group">
            <label class="form-label">手机号</label>
            <div class="phone-input-container">
              <el-input
                  v-model="registerForm.phone"
                  class="form-input phone-input"
                  placeholder="请输入手机号"
                  @focus="handleFocus"
                  @blur="handlePhoneBlur">
                <template #prefix>
                  <span class="phone-prefix">+86</span>
                </template>
              </el-input>
              <el-button
                  class="code-button"
                  :disabled="!canSendCode || countdown > 0"
                  @click="sendSmsCode">
                {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
              </el-button>
            </div>
            <div v-if="phoneStatus" class="status-indicator">
              <i :class="phoneStatus === 'checking' ? 'el-icon-loading' : 
                        phoneStatus === 'available' ? 'el-icon-success' : 'el-icon-error'"
                 :style="{ color: phoneStatus === 'available' ? '#67c23a' : 
                           phoneStatus === 'unavailable' ? '#f56c6c' : '#409eff' }"></i>
              <span v-if="phoneStatus === 'available'">手机号可用</span>
              <span v-else-if="phoneStatus === 'unavailable'">手机号已注册</span>
              <span v-else-if="phoneStatus === 'checking'">检查中...</span>
            </div>
          </div>

          <!-- 验证码 -->
          <div class="form-group">
            <label class="form-label">验证码</label>
            <el-input
                v-model="registerForm.code"
                class="form-input"
                placeholder="请输入6位验证码"
                maxlength="6"
                @focus="handleFocus"
                @blur="handleBlur">
              <template #prefix>
                <i class="el-icon-message"></i>
              </template>
            </el-input>
          </div>

          <!-- 邮箱 -->
          <div class="form-group">
            <label class="form-label">邮箱（可选）</label>
            <el-input
                v-model="registerForm.email"
                class="form-input"
                placeholder="请输入邮箱地址"
                @focus="handleFocus"
                @blur="handleEmailBlur">
              <template #prefix>
                <i class="el-icon-message-solid"></i>
              </template>
            </el-input>
            <div v-if="emailStatus && registerForm.email" class="status-indicator">
              <i :class="emailStatus === 'checking' ? 'el-icon-loading' : 
                        emailStatus === 'available' ? 'el-icon-success' : 'el-icon-error'"
                 :style="{ color: emailStatus === 'available' ? '#67c23a' : 
                           emailStatus === 'unavailable' ? '#f56c6c' : '#409eff' }"></i>
              <span v-if="emailStatus === 'available'">邮箱可用</span>
              <span v-else-if="emailStatus === 'unavailable'">邮箱已注册</span>
              <span v-else-if="emailStatus === 'checking'">检查中...</span>
            </div>
          </div>

          <!-- 地址 -->
          <div class="form-group">
            <label class="form-label">地址（可选）</label>
            <el-input
                v-model="registerForm.address"
                class="form-input"
                placeholder="请输入地址"
                @focus="handleFocus"
                @blur="handleBlur">
              <template #prefix>
                <i class="el-icon-location"></i>
              </template>
            </el-input>
          </div>

          <!-- 用户角色 -->
          <div class="form-group">
            <label class="form-label">用户角色</label>
            <el-form-item prop="role">
              <el-select v-model="registerForm.role" placeholder="请选择用户角色" class="form-input role-select">
              <el-option label="普通用户" value="ROLE_USER"></el-option>
              <el-option label="管理员" value="ROLE_ADMIN"></el-option>
              <el-option label="用户1" value="ROLE_USER1"></el-option>
              <el-option label="用户2" value="ROLE_USER2"></el-option>
              <el-option label="用户3" value="ROLE_USER3"></el-option>
              <el-option label="用户4" value="ROLE_USER4"></el-option>
              <el-option label="用户5" value="ROLE_USER5"></el-option>
              </el-select>
            </el-form-item>
            <div class="role-description">
              <p v-if="registerForm.role === 'ROLE_ADMIN'" class="role-info admin-info">
                <i class="el-icon-warning"></i>
                管理员拥有系统全部权限，可以管理用户、配置系统等
              </p>
              <p v-else-if="registerForm.role === 'ROLE_USER'" class="role-info user-info">
                <i class="el-icon-info"></i>
                普通用户拥有基本功能权限，可以查看和管理自己的数据
              </p>
              <p v-else-if="registerForm.role.startsWith('ROLE_USER')" class="role-info custom-info">
                <i class="el-icon-star-on"></i>
                自定义角色，拥有特定的功能权限
              </p>
            </div>
          </div>

          <!-- 注册按钮 -->
          <div class="form-group">
            <el-button
                type="primary"
                class="register-button"
                :loading="registerLoading"
                @click="handleRegister">
              {{ registerLoading ? '注册中...' : '立即注册' }}
            </el-button>
          </div>

          <!-- 登录链接 -->
          <div class="login-link">
            <span>已有账号？</span>
            <router-link to="/login" class="link">立即登录</router-link>
          </div>
        </el-form>
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
      
      // 状态指示器
      usernameStatus: null, // null, 'checking', 'available', 'unavailable'
      phoneStatus: null,
      emailStatus: null,
      
      // 注册表单数据
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
      
      // 表单验证规则
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
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
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
  
  watch: {
    'registerForm.phone'(newVal) {
      this.canSendCode = /^1[3-9]\d{9}$/.test(newVal)
    }
  },
  
  methods: {
    // 主题切换
    toggleTheme() {
      localStorage.setItem('isDarkMode', this.isDarkMode)
    },
    
    // 输入框焦点处理
    handleFocus(event) {
      event.target.parentElement.classList.add('focused')
    },
    
    handleBlur(event) {
      event.target.parentElement.classList.remove('focused')
    },
    
    // 用户名失焦检查
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
    
    // 手机号失焦检查
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
    
    // 邮箱失焦检查
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
    
    // 发送短信验证码
    async sendSmsCode() {
      if (!this.canSendCode) {
        this.$message.error('请输入正确的手机号')
        return
      }
      
      console.log('发送验证码请求，手机号:', this.registerForm.phone)
      
      try {
        const res = await request.post(`/api/user/sendCode?phone=${this.registerForm.phone}`)
        console.log('验证码发送响应:', res)
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
    
    // 开始倒计时
    startCountdown() {
      this.countdown = 60
      const timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    },
    
    // 确认密码验证
    validateConfirmPassword(rule, value, callback) {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    },
    
    // 处理注册
    async handleRegister() {
      try {
        // 表单验证
        await this.$refs.registerFormRef.validate()
        
        // 检查用户名和手机号状态
        if (this.usernameStatus !== 'available') {
          this.$message.error('请检查用户名是否可用')
          return
        }
        
        if (this.phoneStatus !== 'available') {
          this.$message.error('请检查手机号是否可用')
          return
        }
        
        if (this.registerForm.email && this.emailStatus === 'unavailable') {
          this.$message.error('邮箱已被注册，请使用其他邮箱')
          return
        }
        
        this.registerLoading = true
        console.log('提交注册请求:', this.registerForm)
        
        const res = await request.post('/api/user/register', this.registerForm)
        console.log('注册响应:', res)
        
        if (res.code === '200' || res.code === 200) {
          this.$message.success(res.data.message || '注册成功！')
          // 注册成功后跳转到登录页面
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
    // 从localStorage读取主题设置
    const savedTheme = localStorage.getItem('isDarkMode')
    if (savedTheme !== null) {
      this.isDarkMode = JSON.parse(savedTheme)
    }
  }
}
</script>

<style scoped>
/* 基础样式 - 与登录页面保持一致 */
.wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('../assets/background2.jpg') center/cover;
  opacity: 0.1;
  z-index: 0;
}

.wrapper.dark-mode {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
}

/* 顶部栏样式 */
.header {
  display: flex;
  align-items: center;
  padding: 20px 40px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 10;
}

.logo {
  height: 40px;
  margin-right: 20px;
}

.title {
  flex: 1;
  font-size: 24px;
  font-weight: bold;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.theme-switch {
  margin-left: auto;
}

/* 注册容器样式 */
.register-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  z-index: 5;
}

.glass-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  width: 100%;
  max-width: 500px;
  animation: fadeInUp 0.8s ease-out;
}

.dark-mode .glass-card {
  background: rgba(44, 62, 80, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 注册头部样式 */
.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 10px 0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.dark-mode .register-header h2 {
  color: #ecf0f1;
}

.subtitle {
  color: #7f8c8d;
  font-size: 14px;
  margin: 0;
}

.dark-mode .subtitle {
  color: #bdc3c7;
}

/* 表单样式 */
.register-form {
  width: 100%;
}

.form-group {
  margin-bottom: 25px;
  position: relative;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  text-align: left;
}

.dark-mode .form-label {
  color: #ecf0f1;
}

.form-input {
  width: 100%;
}

.form-input :deep(.el-input__inner) {
  height: 50px;
  border-radius: 12px;
  border: 2px solid #e1e8ed;
  padding: 0 45px 0 45px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
}

.form-input :deep(.el-input__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

.dark-mode .form-input :deep(.el-input__inner) {
  background: rgba(52, 73, 94, 0.9);
  border-color: #34495e;
  color: #ecf0f1;
}

.dark-mode .form-input :deep(.el-input__inner):focus {
  background: #34495e;
  border-color: #667eea;
}

.form-input :deep(.el-input__prefix) {
  left: 15px;
  color: #7f8c8d;
}

.form-input :deep(.el-input__prefix i) {
  font-size: 18px;
}

/* 手机号输入容器 */
.phone-input-container {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.phone-input {
  flex: 1;
}

.phone-prefix {
  color: #7f8c8d;
  font-size: 16px;
  font-weight: 500;
}

.code-button {
  height: 50px;
  padding: 0 20px;
  border-radius: 12px;
  font-size: 14px;
  white-space: nowrap;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  transition: all 0.3s ease;
}

.code-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.code-button:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

/* 状态指示器 */
.status-indicator {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 5px;
  font-size: 12px;
  animation: slideInLeft 0.3s ease-out;
}

.status-indicator i {
  font-size: 14px;
}

/* 角色选择 */
.role-select {
  width: 100%;
}

.role-select :deep(.el-select .el-input__inner) {
  height: 50px;
  border-radius: 12px;
  border: 2px solid #e1e8ed;
  padding: 0 45px 0 45px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
}

.role-select :deep(.el-select .el-input__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

/* 角色描述 */
.role-description {
  margin-top: 8px;
}

.role-info {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  margin: 0;
  transition: all 0.3s ease;
}

.role-info i {
  margin-right: 8px;
  font-size: 16px;
}

.admin-info {
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  color: white;
  border-left: 4px solid #c23616;
}

.user-info {
  background: linear-gradient(135deg, #74b9ff, #0984e3);
  color: white;
  border-left: 4px solid #2d3436;
}

.custom-info {
  background: linear-gradient(135deg, #a29bfe, #6c5ce7);
  color: white;
  border-left: 4px solid #5f3dc4;
}

/* 注册按钮 */
.register-button {
  width: 100%;
  height: 55px;
  border-radius: 15px;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.register-button:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
}

.register-button:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 登录链接 */
.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #7f8c8d;
}

.dark-mode .login-link {
  color: #bdc3c7;
}

.link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.link:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 15px 20px;
  }
  
  .title {
    font-size: 18px;
  }
  
  .glass-card {
    padding: 30px 20px;
    margin: 20px;
  }
  
  .register-header h2 {
    font-size: 24px;
  }
  
  .phone-input-container {
    flex-direction: column;
  }
  
  .code-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .title {
    font-size: 16px;
  }
  
  .glass-card {
    padding: 20px 15px;
  }
}
</style>