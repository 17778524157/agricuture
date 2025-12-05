<template>
  <div class="wrapper" :class="{ 'dark-mode': isDarkMode }">
    <!-- 动态背景效果 -->
    <div class="animated-background">
      <div class="floating-shapes">
        <div class="shape shape-1" v-for="n in 8" :key="'shape1-' + n" :style="getShapeStyle(1)"></div>
        <div class="shape shape-2" v-for="n in 6" :key="'shape2-' + n" :style="getShapeStyle(2)"></div>
        <div class="shape shape-3" v-for="n in 4" :key="'shape3-' + n" :style="getShapeStyle(3)"></div>
      </div>
      <div class="particles">
        <div class="particle" v-for="n in 30" :key="'particle-' + n" :style="getParticleStyle()"></div>
      </div>
      <div class="gradient-orbs">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
      </div>
    </div>

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

    <!-- 登录表单容器 -->
    <div class="login-container">
      <div class="glass-card">
        <!-- 装饰性边框 -->
        <div class="card-border"></div>
        <!-- 内部光效 -->
        <div class="card-glow"></div>
        <div class="login-header">
          <h2>欢迎登录</h2>
          <p class="subtitle">酸枣种植风险预警智能决策系统</p>
        </div>

        <!-- 登录方式切换选项卡 - 移到表单顶部 -->
        <div class="login-method-selector">
          <el-tabs v-model="loginType" type="card" class="login-tabs">
            <el-tab-pane label="用户名登录" name="username"></el-tab-pane>
            <el-tab-pane label="手机号登录" name="phone"></el-tab-pane>
          </el-tabs>
        </div>

        <!-- 用户名登录表单 -->
        <el-form v-if="loginType === 'username'" :model="usernameFormData" :rules="usernameRules" ref="usernameForm" class="login-form">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <el-input
                v-model="usernameFormData.username"
                class="form-input"
                placeholder="请输入用户名"
                @focus="handleFocus"
                @blur="handleBlur">
              <template #prefix>
                <i class="el-icon-user"></i>
              </template>
            </el-input>
          </div>

          <div class="form-group">
            <label class="form-label">密码</label>
            <el-input
                v-model="usernameFormData.password"
                type="password"
                class="form-input"
                placeholder="请输入密码"
                show-password
                @focus="handleFocus"
                @blur="handleBlur">
              <template #prefix>
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </div>

          <!-- 验证码输入框 -->
          <div class="form-group">
            <label class="form-label">验证码</label>
            <div class="code-input-wrapper">
              <el-input
                  v-model="usernameFormData.code"
                  class="form-input code-input"
                  placeholder="请输入验证码"
                  @focus="handleFocus"
                  @blur="handleBlur">
                <template #prefix>
                  <i class="el-icon-shield"></i>
                </template>
              </el-input>
              <!-- 验证码图片 -->
              <div class="identify-code" @click="refreshCode">
                <Identify :identifyCode="identifyCode" />
              </div>
            </div>
          </div>

          <!-- 角色选择 -->
          <div class="form-group">
            <label class="form-label">用户角色</label>
            <el-form-item prop="role">
              <el-select v-model="usernameFormData.role" placeholder="请选择用户角色" class="form-input role-select">
                <el-option label="管理员" value="ROLE_ADMIN"></el-option>
                <el-option label="普通用户" value="ROLE_USER"></el-option>
                <el-option label="用户1" value="ROLE_USER1"></el-option>
                <el-option label="用户2" value="ROLE_USER2"></el-option>
                <el-option label="用户3" value="ROLE_USER3"></el-option>
                <el-option label="用户4" value="ROLE_USER4"></el-option>
                <el-option label="用户5" value="ROLE_USER5"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>

        <!-- 手机号登录表单 -->
        <el-form v-if="loginType === 'phone'" :model="phoneFormData" :rules="phoneRules" ref="phoneForm" class="login-form">
          <!-- Phone input with +86 prefix -->
          <div class="form-group">
            <label class="form-label">手机号</label>
            <div class="phone-input-container">
              <div class="phone-prefix">+86</div>
              <el-input
                  v-model="phoneFormData.phone"
                  class="form-input phone-input"
                  placeholder="请输入手机号"
                  @focus="handleFocus"
                  @blur="handleBlur">
                <template #prefix>
                  <i class="el-icon-phone"></i>
                </template>
              </el-input>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">短信验证码</label>
            <div class="sms-input-wrapper">
              <el-input
                  v-model="phoneFormData.smsCode"
                  class="form-input sms-input"
                  placeholder="请输入短信验证码"
                  @focus="handleFocus"
                  @blur="handleBlur">
                <template #prefix>
                  <i class="el-icon-message"></i>
                </template>
              </el-input>
              <el-button
                  class="sms-code-btn"
                  :disabled="countdown > 0"
                  @click="sendSmsCode">
                {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
              </el-button>
            </div>
          </div>

          <!-- 角色选择 -->
          <div class="form-group">
            <label class="form-label">用户角色</label>
            <el-form-item prop="role">
              <el-select v-model="phoneFormData.role" placeholder="请选择用户角色" class="form-input role-select">
                <el-option label="管理员" value="ROLE_ADMIN"></el-option>
                <el-option label="普通用户" value="ROLE_USER"></el-option>
                <el-option label="用户1" value="ROLE_USER1"></el-option>
                <el-option label="用户2" value="ROLE_USER2"></el-option>
                <el-option label="用户3" value="ROLE_USER3"></el-option>
                <el-option label="用户4" value="ROLE_USER4"></el-option>
                <el-option label="用户5" value="ROLE_USER5"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>

        <div class="form-actions">
          <el-button
              type="primary"
              class="neon-button"
              :loading="loading"
              @click="login">
            <span v-if="!loading">登录</span>
            <lottie-player
                v-else
                src="https://assets2.lottiefiles.com/packages/lf20_p8bfn5to.json"
                background="transparent"
                speed="1"
                style="width: 24px; height: 24px;"
                loop
                autoplay>
            </lottie-player>
          </el-button>

          <div class="secondary-actions">
            <el-button type="text" @click="$router.push('/register')">注册账号</el-button>
            <el-button type="text" @click="handlePass">找回密码</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 找回密码弹窗 -->
    <el-dialog
        title="找回密码"
        :visible.sync="dialogFormVisible"
        width="30%"
        custom-class="glass-dialog">
      <el-form label-width="100px" size="small">
        <el-form-item label="用户名">
          <el-input v-model="pass.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="pass.phone" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="passwordBack">重置密码</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {resetRouter, setRoutes} from "@/router";
import Identify from "@/components/Identify";

export default {
  name: "Login",
  data() {
    return {
      // 登录方式类型
      loginType: 'username', // 'username' 或 'phone'
      // 用户名登录表单数据
      user: {
        username: '',
        password: '',
        code: ''
      },
      // 用户名登录表单数据（包含角色）
      usernameFormData: {
        username: '',
        password: '',
        code: '',
        role: ''
      },
      // 手机号登录表单数据
      phoneUser: {
        phone: '',
        smsCode: ''
      },
      // 手机号登录表单数据（包含角色）
      phoneFormData: {
        phone: '',
        smsCode: '',
        role: ''
      },
      // 角色选择（保留用于兼容）
      selectedRole: '',
      // 其他数据
      pass: {},
      code: '',
      dialogFormVisible: false,
      identifyCode: '',
      identifyCodes: '3456789ABCDEFGHGKMNPQRSTUVWXY',
      // 用户名登录验证规则
      usernameRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { min: 4, max: 4, message: '验证码长度为4位', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value && value.toLowerCase() !== this.identifyCode.toLowerCase()) {
                callback(new Error('验证码错误'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ],
        role: [
          { required: true, message: '请选择用户角色', trigger: 'change' }
        ]
      },
      // 手机号登录验证规则
      phoneRules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        smsCode: [
          { required: true, message: '请输入短信验证码', trigger: 'blur' },
          { min: 4, max: 6, message: '验证码长度为4-6位', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择用户角色', trigger: 'change' }
        ]
      },
      // 短信验证码倒计时
      countdown: 0,
      // 主题和加载状态
      isDarkMode: false,
      loading: false,
    }
  },
  components: {Identify},
  mounted() {
    resetRouter()
    this.refreshCode()
  },
  methods: {
    // 登录方法
    login() {
      this.loading = true

      if (this.loginType === 'username') {
        // 用户名登录
        this.$refs['usernameForm'].validate((valid) => {
          if (valid) {
            this.handleUsernameLogin()
          } else {
            this.loading = false
          }
        })
      } else {
        // 手机号登录
        this.$refs['phoneForm'].validate((valid) => {
          if (valid) {
            this.handlePhoneLogin()
          } else {
            this.loading = false
          }
        })
      }
    },

    // 用户名登录逻辑
    handleUsernameLogin() {
      const loginData = {
        username: this.usernameFormData.username,
        password: this.usernameFormData.password,
        code: this.usernameFormData.code,
        role: this.usernameFormData.role
      }

      this.request.post("/api/user/login", loginData)
          .then(res => {
            this.handleLoginSuccess(res)
          })
          .catch(error => {
            this.handleLoginError(error)
          })
          .finally(() => {
            this.loading = false
          })
    },

    // 手机号登录逻辑
    handlePhoneLogin() {
      const loginData = {
        phone: this.phoneFormData.phone,
        code: this.phoneFormData.smsCode, // 前端使用smsCode，但后端LoginRequest需要code字段
        role: this.phoneFormData.role
      }

      this.request.post("/api/user/phoneLogin", loginData)
          .then(res => {
            this.handleLoginSuccess(res)
          })
          .catch(error => {
            this.handleLoginError(error)
          })
          .finally(() => {
            this.loading = false
          })
    },

    // 处理登录成功
    handleLoginSuccess(res) {
      if(res.code === '200' || res.code === 200) {
        console.log('登录成功，用户信息:', res.data)
        console.log('用户头像URL:', res.data.avatarUrl)
        localStorage.setItem("user", JSON.stringify(res.data))
        localStorage.setItem("menus", JSON.stringify(res.data.menus))
        setRoutes()
        this.$router.push("/")
        this.$message.success("登录成功")
      } else {
        this.$message.error(res.msg)
        // 登录失败时刷新验证码（如果是用户名登录）
        if (this.loginType === 'username') {
          this.refreshCode()
        }
      }
    },

    // 处理登录错误
    handleLoginError(error) {
      console.error('登录请求错误:', error)
      
      let errorMessage = '登录失败，请稍后重试'
      
      // 检查是否有响应数据
      if (error.response) {
        const response = error.response
        console.log('响应状态:', response.status)
        console.log('响应数据:', response.data)
        
        // 根据HTTP状态码处理
        if (response.status === 401) {
          errorMessage = '认证失败，请检查用户名和密码'
        } else if (response.status === 403) {
          errorMessage = '访问被拒绝，请联系管理员'
        } else if (response.status === 404) {
          errorMessage = '服务未找到，请检查网络连接'
        } else if (response.status === 500) {
          errorMessage = '服务器内部错误，请稍后重试'
        } else if (response.status >= 400 && response.status < 500) {
          errorMessage = '请求错误，请检查输入信息'
        } else if (response.status >= 500) {
          errorMessage = '服务器错误，请稍后重试'
        }
        
        // 尝试从响应数据中获取具体错误信息
        if (response.data) {
          if (response.data.message) {
            errorMessage = response.data.message
          } else if (response.data.msg) {
            errorMessage = response.data.msg
          } else if (response.data.error) {
            errorMessage = response.data.error
          } else if (typeof response.data === 'string') {
            errorMessage = response.data
          }
        }
      } else if (error.data) {
        // 处理axios响应拦截器返回的错误数据格式
        console.log('错误数据:', error.data)
        if (error.data.message) {
          errorMessage = error.data.message
        } else if (error.data.msg) {
          errorMessage = error.data.msg
        } else if (error.data.error) {
          errorMessage = error.data.error
        } else if (typeof error.data === 'string') {
          errorMessage = error.data
        }
      } else if (error.request) {
        // 网络错误，没有收到响应
        console.error('网络错误:', error.request)
        errorMessage = '网络连接失败，请检查网络连接或后端服务是否运行'
      } else {
        // 其他错误
        console.error('其他错误:', error.message)
        if (error.message) {
          errorMessage = error.message
        }
      }
      
      this.$message.error(errorMessage)
      
      // 登录失败时刷新验证码（如果是用户名登录）
      if (this.loginType === 'username') {
        this.refreshCode()
      }
    },

    // 模拟登录
    handleMockLogin() {
      this.$message.warning('已启用模拟登录模式')
      // 创建模拟用户数据
      const mockUser = {
        username: this.loginType === 'username' ? this.user.username : 'phone_user',
        nickname: '演示用户',
        role: this.selectedRole || 'USER',
        token: 'mock-token',
        menus: [
          { id: 1, name: '首页', path: '/', icon: 'el-icon-s-home' },
          { id: 2, name: '农田管理', path: '/farmland', icon: 'el-icon-location' },
        ]
      }
      localStorage.setItem("user", JSON.stringify(mockUser))
      localStorage.setItem("menus", JSON.stringify(mockUser.menus))
      setRoutes()
      this.$router.push("/")
    },

    // 刷新图片验证码
    refreshCode() {
      this.identifyCode = ''
      this.makeCode(this.identifyCodes, 4)
    },

    // 生成验证码
    makeCode(o, l) {
      for (let i = 0; i < l; i++) {
        this.identifyCode += this.identifyCodes[Math.floor(Math.random() * (this.identifyCodes.length))]
      }
    },

    // 发送短信验证码
    sendSmsCode() {
      // 验证手机号
      if (!/^1[3-9]\d{9}$/.test(this.phoneFormData.phone)) {
        this.$message.error('请输入正确的手机号码')
        return
      }

      console.log('发送验证码请求，手机号:', this.phoneFormData.phone)

      // 发送请求获取短信验证码，调用后端的sendCode方法
      // 注意：后端需要phone作为请求参数，而不是请求体
      this.request.post(`/api/user/sendCode?phone=${this.phoneFormData.phone}`)
          .then(res => {
            console.log('验证码发送响应:', res)
            if (res.code === '200' || res.code === 200) {
              this.$message.success('短信验证码已发送')
              // 开始倒计时
              this.startCountdown()
            } else {
              this.$message.error(res.msg || '发送失败，请稍后重试')
            }
          })
          .catch(error => {
            console.error('验证码发送错误:', error)
            this.$message.error('发送失败，请稍后重试')
          })
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

    // 找回密码
    handlePass() {
      this.dialogFormVisible = true
      this.pass = {}
    },

    // 重置密码
    passwordBack() {
      this.request.put("/user/reset", this.pass).then(res => {
        if (res.code === '200') {
          this.$message.success("重置密码成功，新密码为：123，请尽快修改密码")
          this.dialogFormVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    // 处理输入框聚焦
    handleFocus(event) {
      event.target.parentElement.classList.add('focused')
    },

    // 处理输入框失焦
    handleBlur(event) {
      if (!event.target.value) {
        event.target.parentElement.classList.remove('focused')
      }
    },

    // 切换主题
    toggleTheme() {
      document.body.classList.toggle('dark-mode')
    },
    // 生成形状样式
    getShapeStyle(type) {
      const baseStyles = {
        1: { size: '60px', color: 'rgba(16, 185, 129, 0.1)', animation: 'float1' },
        2: { size: '40px', color: 'rgba(59, 130, 246, 0.08)', animation: 'float2' },
        3: { size: '80px', color: 'rgba(139, 92, 246, 0.06)', animation: 'float3' }
      }
      
      const style = baseStyles[type]
      return {
        left: Math.random() * 100 + '%',
        top: Math.random() * 100 + '%',
        width: style.size,
        height: style.size,
        backgroundColor: style.color,
        animationName: style.animation,
        animationDelay: Math.random() * 10 + 's',
        animationDuration: (Math.random() * 20 + 15) + 's'
      }
    },
    // 生成粒子样式
    getParticleStyle() {
      return {
        left: Math.random() * 100 + '%',
        top: Math.random() * 100 + '%',
        animationDelay: Math.random() * 15 + 's',
        animationDuration: (Math.random() * 8 + 6) + 's'
      }
    },
  }
}
</script>

<style>
/* CSS Variables - 优化视觉层次和用户体验 */
:root {
  /* 主色调 - 农业主题色彩 */
  --primary-color: #10b981;
  --primary-color-light: #34d399;
  --primary-color-dark: #059669;
  --primary-gradient: linear-gradient(135deg, #10b981, #3b82f6);
  --secondary-color: #f59e0b;
  --accent-color: #8b5cf6;

  /* 背景和卡片 - 更现代的渐变 */
  --background-color: #0f172a;
  --background-gradient: linear-gradient(135deg, #0f172a, #1e293b, #334155);
  --card-background: rgba(255, 255, 255, 0.08);
  --card-gradient: linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.05));
  --card-border: rgba(255, 255, 255, 0.1);

  /* 文本和排版 */
  --text-color: #f8fafc;
  --text-secondary: rgba(248, 250, 252, 0.8);
  --text-tertiary: rgba(248, 250, 252, 0.6);
  --text-muted: rgba(148, 163, 184, 0.8);

  /* 输入框和边框 - 增强视觉反馈 */
  --input-background: rgba(255, 255, 255, 0.08);
  --input-border: rgba(255, 255, 255, 0.15);
  --input-border-hover: rgba(255, 255, 255, 0.25);
  --input-border-focus: var(--primary-color);
  --glowing-border-color: rgba(16, 185, 129, 0.4);
  --label-color-unfocused: rgba(248, 250, 252, 0.9);
  --prefix-icon-color: rgba(248, 250, 252, 0.7);
  --input-text-color: #f8fafc;

  /* 尺寸和间距 - 优化比例和布局 */
  --border-radius: 16px;
  --border-radius-small: 10px;
  --spacing-unit: 8px;
  --shadow-color: rgba(0, 0, 0, 0.6);
  --card-shadow: 0 20px 40px rgba(0, 0, 0, 0.4), 0 0 20px rgba(16, 185, 129, 0.1);
  --card-shadow-hover: 0 25px 50px rgba(0, 0, 0, 0.5), 0 0 30px rgba(16, 185, 129, 0.2);
  --button-shadow: 0 8px 25px rgba(16, 185, 129, 0.3);
  --button-shadow-hover: 0 12px 35px rgba(16, 185, 129, 0.4);
}

.dark-mode {
  --background-color: #0a0a1a;
  --background-gradient: linear-gradient(135deg, var(--background-color), #0d162a);
  --card-background: rgba(255, 255, 255, 0.05);
  --card-gradient: linear-gradient(135deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.03));
  --text-color: #f0f0f0;
  --text-secondary: rgba(240, 240, 240, 0.7);
  --text-tertiary: rgba(240, 240, 240, 0.5);
  --shadow-color: rgba(0, 0, 0, 0.6);
  --input-background: rgba(255, 255, 255, 0.08);
  --glowing-border-color: rgba(19, 206, 102, 0.2);
  --label-color-unfocused: rgba(240, 240, 240, 0.9);
  --prefix-icon-color: rgba(240, 240, 240, 0.6);
}

/* 全局重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: var(--background-gradient);
  color: var(--text-color);
  overflow-x: hidden;
  font-smooth: antialiased;
  -webkit-font-smoothing: antialiased;
}

/* 主容器 */
.wrapper {
  background: var(--background-gradient);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 动态背景效果 */
.animated-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

/* 浮动形状 */
.floating-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;
  animation-timing-function: ease-in-out;
  animation-iteration-count: infinite;
  animation-direction: alternate;
}

.shape-1 {
  animation: float1 20s ease-in-out infinite;
}

.shape-2 {
  animation: float2 25s ease-in-out infinite;
}

.shape-3 {
  animation: float3 30s ease-in-out infinite;
}

/* 粒子效果 */
.particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.particle {
  position: absolute;
  width: 3px;
  height: 3px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  animation: particleFloat linear infinite;
}

/* 渐变光球 */
.gradient-orbs {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  animation: orbFloat 15s ease-in-out infinite;
}

.orb-1 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.3), transparent);
  top: -150px;
  left: -150px;
  animation-delay: 0s;
}

.orb-2 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2), transparent);
  bottom: -100px;
  right: -100px;
  animation-delay: 5s;
}

.orb-3 {
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.15), transparent);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 10s;
}

/* 动画定义 */
@keyframes float1 {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  25% {
    transform: translateY(-20px) rotate(90deg);
  }
  50% {
    transform: translateY(-40px) rotate(180deg);
  }
  75% {
    transform: translateY(-20px) rotate(270deg);
  }
}

@keyframes float2 {
  0%, 100% {
    transform: translateX(0px) translateY(0px) rotate(0deg);
  }
  33% {
    transform: translateX(30px) translateY(-30px) rotate(120deg);
  }
  66% {
    transform: translateX(-20px) translateY(-50px) rotate(240deg);
  }
}

@keyframes float3 {
  0%, 100% {
    transform: translateY(0px) scale(1) rotate(0deg);
  }
  50% {
    transform: translateY(-60px) scale(1.2) rotate(180deg);
  }
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

@keyframes orbFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(50px, -30px) scale(1.1);
  }
  50% {
    transform: translate(-30px, -60px) scale(0.9);
  }
  75% {
    transform: translate(30px, -20px) scale(1.05);
  }
}

/* 边框发光动画 */
@keyframes borderGlow {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

/* 内部光效动画 */
@keyframes innerGlow {
  0%, 100% {
    opacity: 0.3;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(1.2);
  }
}

/* 背景图片 - 增强视觉效果 */
.wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('../assets/background2.jpg') center/cover;
  opacity: 0.2;
  z-index: 0;
  filter: blur(2px) brightness(0.8);
}

/* 装饰性背景元素 - 多层渐变效果 */
.wrapper::after {
  content: '';
  position: absolute;
  top: -20%;
  right: -20%;
  width: 80%;
  height: 80%;
  background: 
    radial-gradient(circle at 30% 20%, rgba(16, 185, 129, 0.15), transparent 50%),
    radial-gradient(circle at 70% 80%, rgba(59, 130, 246, 0.1), transparent 50%),
    radial-gradient(circle at 20% 60%, rgba(139, 92, 246, 0.08), transparent 50%);
  z-index: 0;
  border-radius: 50%;
  animation: float 20s ease-in-out infinite;
}

/* 添加浮动动画 */
@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

/* 页头 - 增强玻璃态效果 */
.header {
  position: relative;
  z-index: 1;
  height: calc(var(--spacing-unit) * 13);
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(30px) saturate(180%);
  display: flex;
  align-items: center;
  padding: 0 calc(var(--spacing-unit) * 7);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  border-image: linear-gradient(90deg, transparent, rgba(16, 185, 129, 0.3), transparent) 1;
}

/* Logo - 增强视觉效果 */
.logo {
  height: calc(var(--spacing-unit) * 8.5);
  margin-right: calc(var(--spacing-unit) * 3.5);
  animation: flipInY 1s ease-out;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffffff, #f0f9ff);
  padding: 8px;
  box-shadow: 
    0 0 30px rgba(16, 185, 129, 0.4),
    0 8px 25px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: 2px solid rgba(16, 185, 129, 0.2);
}

.logo:hover {
  transform: rotate(8deg) scale(1.08);
  box-shadow: 
    0 0 40px rgba(16, 185, 129, 0.6),
    0 12px 35px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  border-color: rgba(16, 185, 129, 0.4);
}

/* 标题 - 增强渐变效果 */
.title {
  color: var(--text-color);
  font-size: calc(var(--spacing-unit) * 3.5);
  font-weight: 700;
  letter-spacing: 0.8px;
  text-shadow: 
    0 4px 15px rgba(0,0,0,0.7),
    0 0 20px rgba(16, 185, 129, 0.3);
  background: linear-gradient(135deg, #ffffff, #10b981, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% 200%;
  animation: gradientShift 8s ease-in-out infinite;
  position: relative;
}

/* 标题发光效果 */
.title::before {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #ffffff, #10b981, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: blur(1px);
  opacity: 0.5;
  z-index: -1;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 主题切换 */
.theme-switch {
  margin-left: auto;
  --el-switch-on-color: #13ce66;
  --el-switch-off-color: #ff4949;
}

/* 登录容器 */
.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  padding: calc(var(--spacing-unit) * 5) calc(var(--spacing-unit) * 4);
}

/* 玻璃态卡片 - 增强现代感 */
.glass-card {
  background: var(--card-gradient);
  backdrop-filter: blur(40px) saturate(180%);
  border-radius: var(--border-radius);
  padding: calc(var(--spacing-unit) * 6);
  width: 100%;
  max-width: 500px;
  box-shadow: var(--card-shadow);
  border: 1px solid var(--card-border);
  animation: fadeInUp 0.8s ease-out;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

/* 装饰性边框 */
.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: var(--border-radius);
  background: linear-gradient(45deg, 
    rgba(16, 185, 129, 0.3), 
    rgba(59, 130, 246, 0.3), 
    rgba(139, 92, 246, 0.3), 
    rgba(16, 185, 129, 0.3));
  background-size: 400% 400%;
  animation: borderGlow 3s ease-in-out infinite;
  z-index: -1;
  padding: 2px;
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask-composite: xor;
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
}

/* 内部光效 */
.card-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.1), transparent);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: innerGlow 4s ease-in-out infinite;
  z-index: -1;
  pointer-events: none;
}

.glass-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: var(--card-shadow-hover);
  border-color: rgba(16, 185, 129, 0.2);
}

.glass-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.glass-card:hover::before {
  opacity: 1;
}

/* 发光边框效果 - 增强动画 */
.glass-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--primary-gradient);
  opacity: 0.8;
  animation: shimmer 4s infinite;
  border-radius: var(--border-radius) var(--border-radius) 0 0;
}

/* 登录标题 - 增强动画效果 */
.login-header {
  text-align: center;
  margin-bottom: calc(var(--spacing-unit) * 6);
  position: relative;
  animation: slideInRight 0.8s ease-out;
}

.login-header h2 {
  color: var(--text-color);
  font-size: calc(var(--spacing-unit) * 4.2);
  margin-bottom: calc(var(--spacing-unit) * 1.5);
  font-weight: 700;
  text-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.7),
    0 0 25px rgba(16, 185, 129, 0.3);
  background: linear-gradient(135deg, #ffffff, #10b981, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.8px;
  background-size: 200% 200%;
  animation: gradientShift 6s ease-in-out infinite;
}

.subtitle {
  color: var(--text-secondary);
  opacity: 1;
  font-size: calc(var(--spacing-unit) * 2.1);
  font-weight: 500;
  letter-spacing: 0.5px;
  line-height: 1.6;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  animation: fadeIn 1s ease-out 0.3s both;
}

/* 表单组 - 重新设计布局 */
.form-group {
  position: relative;
  margin-bottom: 20px;
  animation: fadeIn 0.6s ease-out;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  transition: all 0.3s ease;
}

.form-group:hover {
  transform: translateX(2px);
}

/* 表单标签样式 - 在输入框上方 */
.form-label {
  display: block;
  text-align: left;
  color: var(--text-color);
  font-size: 14px;
  font-weight: 600;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  margin-bottom: 8px;
  line-height: 1.2;
  letter-spacing: 0.3px;
  transition: color 0.3s ease;
}

.form-group:hover .form-label {
  color: var(--primary-color);
}

/* 表单输入框样式 */
.form-input .el-input__inner {
  background: var(--input-background) !important;
  border: 1px solid var(--input-border) !important;
  border-radius: var(--border-radius-small) !important;
  padding: 12px 15px 12px 45px !important;
  color: var(--input-text-color) !important;
  height: 48px !important;
  line-height: 48px !important;
  transition: all 0.3s ease !important;
  font-weight: 500;
  font-size: 15px;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100% !important;
  position: relative;
}

/* 输入框聚焦时的发光效果 */
.form-input .el-input__inner:focus {
  border-color: var(--primary-color) !important;
  box-shadow:
      0 0 0 3px rgba(16, 185, 129, 0.3),
      0 4px 12px rgba(0, 0, 0, 0.3),
      0 0 20px rgba(16, 185, 129, 0.2) !important;
  background: rgba(255, 255, 255, 0.15) !important;
  transform: scale(1.02);
}

/* 输入框悬停效果 */
.form-input .el-input__inner:hover {
  border-color: var(--input-border-hover) !important;
  background: rgba(255, 255, 255, 0.1) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.form-input .el-input__inner::placeholder {
  color: var(--text-muted);
  opacity: 0.8;
}

.form-input .el-input__inner:hover {
  border-color: var(--input-border-hover) !important;
  background: rgba(255, 255, 255, 0.1) !important;
}

.form-input .el-input__prefix {
  left: 15px;
  color: var(--prefix-icon-color);
  opacity: 1;
  font-size: 18px;
  transition: all 0.3s ease;
  z-index: 1;
}

.form-input .el-input__inner:focus {
  border-color: var(--primary-color) !important;
  box-shadow:
      0 0 0 3px rgba(16, 185, 129, 0.3),
      0 4px 12px rgba(0, 0, 0, 0.3) !important;
  background: rgba(255, 255, 255, 0.15) !important;
  transform: scale(1.02);
}

.form-input .el-input__inner:focus ~ .el-input__prefix {
  color: var(--primary-color) !important;
}

.form-group.focused .form-input .el-input__inner {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2) !important;
}


/* 验证码输入框容器 */
.code-input-wrapper {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
}

.code-input {
  flex: 1;
}

/* 短信验证码输入框容器 */
.sms-input-wrapper {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
}

.sms-input {
  flex: 1;
}

/* 手机号输入框容器 */
.phone-input-container {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  border: 1px solid var(--input-border);
  border-radius: var(--border-radius-small);
  background: var(--input-background);
  height: 48px;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.phone-input-container:hover {
  border-color: var(--input-border-hover);
  background: rgba(255, 255, 255, 0.1);
}

.phone-input-container:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.3);
  background: rgba(255, 255, 255, 0.15);
}

/* 手机号国家代码选择器样式 */
.phone-prefix {
  padding: 0 15px;
  color: var(--input-text-color);
  font-size: 15px;
  font-weight: 500;
  border-right: 1px solid var(--input-border);
  height: 100%;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: color 0.3s ease;
}

.phone-input-container:focus-within .phone-prefix {
  color: var(--primary-color);
}

/* 手机号输入框样式优化 */
.phone-input {
  flex: 1;
  border: none !important;
  background: transparent !important;
  height: 100% !important;
}

.phone-input .el-input__inner {
  border: none !important;
  background: transparent !important;
  padding-left: 15px !important;
  padding-right: 15px !important;
  box-shadow: none !important;
  height: 100% !important;
  line-height: 100% !important;
}



/* 角色选择 */
.role-select .el-input__inner {
  background: var(--input-background) !important;
  border: 1px solid var(--input-border) !important;
  border-radius: var(--border-radius-small) !important;
  color: var(--text-color) !important;
  height: 48px !important;
  line-height: 48px !important;
  padding: 0 15px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  width: 100% !important;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.role-select .el-input__inner:hover {
  border-color: var(--input-border-hover) !important;
  background: rgba(255, 255, 255, 0.1) !important;
}

.role-select .el-input__inner:focus {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.3) !important;
  background: rgba(255, 255, 255, 0.15) !important;
}

.role-select .el-input__suffix .el-input__suffix-inner i {
  color: var(--prefix-icon-color);
  font-size: 18px;
}

/* 登录方式选择器 - 顶部居中 */
.login-method-selector {
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
  animation: slideInLeft 0.6s ease-out;
}

/* 登录选项卡 - 增强视觉效果 */
.login-tabs {
  border-radius: var(--border-radius-small);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  max-width: 300px;
}

.login-tabs .el-tabs__header {
  border-bottom: none !important;
  padding: 0;
  background: transparent;
}

.login-tabs .el-tabs__nav {
  float: none;
  display: flex;
  width: 100%;
  background: transparent;
}

.login-tabs .el-tabs__item {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  color: var(--text-color);
  opacity: 0.7;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.login-tabs .el-tabs__item:hover {
  opacity: 1;
  color: var(--primary-color);
  background: rgba(19, 206, 102, 0.05);
}

.login-tabs .el-tabs__item.is-active {
  color: var(--primary-color);
  opacity: 1;
  border-bottom-color: var(--primary-color);
  background: rgba(16, 185, 129, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
}

.login-tabs .el-tabs__active-bar {
  display: none;
}

/* 角色选择器样式 - 已移至表单内部 */

/* 短信验证码输入框 - 与图片样式保持一致 */
.sms-input {
  width: 100% !important;
  border: 1px solid var(--input-border) !important;
  border-radius: var(--border-radius-small) !important;
  background: var(--input-background) !important;
  height: 45px !important;
}

.sms-input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.sms-code-btn {
  width: 120px;
  height: 48px;
  background: linear-gradient(135deg, #f8fafc, #e2e8f0);
  border: 1px solid rgba(148, 163, 184, 0.3);
  color: #475569;
  border-radius: var(--border-radius-small);
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8px;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.sms-code-btn:hover:not(:disabled) {
  background: var(--primary-gradient);
  color: white;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4);
  border-color: transparent;
}

.sms-code-btn:active:not(:disabled) {
  transform: translateY(0) scale(1.02);
  transition: all 0.1s ease;
}

.sms-code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(16, 185, 129, 0.2);
  color: var(--text-muted);
  transform: none;
  box-shadow: none;
}

/* 表单操作区 */
.form-actions {
  margin-top: 15px;
  clear: both;
}

/* 霓虹按钮 - 增强交互效果 */
.neon-button {
  width: 100%;
  height: 52px;
  background: var(--primary-gradient);
  border: none;
  border-radius: var(--border-radius);
  color: white;
  font-weight: 700;
  font-size: 18px;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: var(--button-shadow);
  display: flex;
  justify-content: center;
  align-items: center;
  letter-spacing: 1.2px;
  text-transform: uppercase;
  backdrop-filter: blur(10px);
  margin-top: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 按钮发光效果 */
.neon-button::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, 
    rgba(255, 255, 255, 0.1), 
    transparent, 
    rgba(255, 255, 255, 0.1));
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: var(--border-radius);
}

.neon-button:hover::after {
  opacity: 1;
}

.neon-button:hover {
  transform: translateY(-6px) scale(1.03);
  box-shadow: var(--button-shadow-hover);
  background: linear-gradient(135deg, #059669, #2563eb);
}

.neon-button:active {
  transform: translateY(-2px) scale(1.01);
  transition: all 0.1s ease;
}

.neon-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 40%;
  height: 100%;
  background: rgba(255, 255, 255, 0.3);
  transform: skewX(-30deg);
  transition: 0.6s;
}

.neon-button:hover::before {
  left: 150%;
}

.neon-button:active {
  transform: translateY(0);
}

.neon-button .lottie-player {
  width: 24px !important;
  height: 24px !important;
  margin-left: 5px;
}

/* 次要操作 */
.secondary-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
}

.secondary-actions .el-button--text {
  color: var(--text-secondary);
  opacity: 0.85;
  transition: all 0.3s ease;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.secondary-actions .el-button--text:hover {
  opacity: 1;
  color: var(--primary-color);
  text-decoration: none;
  transform: translateY(-1px) scale(1.03);
}

/* 验证码 - 增强交互效果 */
.identify-code {
  width: 110px;
  height: 48px;
  background: var(--input-background);
  border: 1px solid var(--input-border);
  border-radius: var(--border-radius-small);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  flex-shrink: 0;
}

.identify-code:hover {
  border-color: var(--primary-color);
  box-shadow: 0 0 20px rgba(16, 185, 129, 0.4);
  transform: translateY(-3px) scale(1.05);
}

.identify-code:active {
  transform: translateY(-1px) scale(1.02);
  transition: all 0.1s ease;
}

.identify-code::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent, rgba(16, 185, 129, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.identify-code:hover::before {
  opacity: 1;
}


/* 密码可见性切换 - 优化位置防止重叠 */
.el-input__suffix {
  color: var(--prefix-icon-color) !important;
  opacity: 0.8;
  transition: all 0.2s ease;
  z-index: 1;
  padding-right: 5px;
}

.el-input__suffix:hover {
  color: var(--primary-color) !important;
  opacity: 1;
}

/* 确保输入框之间的适当间距 */
.el-input {
  width: 100%;
}

/* 带+86前缀的手机号输入框 - 调整样式 */
.phone-prefix {
  position: relative;
  padding: 0 15px;
  color: var(--input-text-color);
  font-size: 15px;
  font-weight: 500;
  border-right: 1px solid var(--input-border);
  height: 100%;
  display: flex;
  align-items: center;
  cursor: pointer;
  z-index: 2;
}

.phone-input {
  width: calc(100% - 70px) !important;
  border: none !important;
  background: transparent !important;
  padding-left: 15px !important;
  padding-right: 15px !important;
  height: 100% !important;
  line-height: 100% !important;
}

/* 表单错误消息 */
.el-form-item__error {
  color: #ff4949 !important;
  font-size: 13px !important;
  padding-top: 5px !important;
  text-shadow: 0 0 4px rgba(255, 73, 73, 0.3);
}

/* 玻璃态弹窗 */
.glass-dialog .el-dialog__wrapper {
  backdrop-filter: blur(10px);
}

.glass-dialog .el-dialog {
  background: var(--card-gradient);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--border-radius);
  box-shadow: 0 15px 60px rgba(0, 0, 0, 0.6);
}

.glass-dialog .el-dialog__body {
  background: rgba(255, 255, 255, 0.03) !important;
  padding: 16px !important;
}

.glass-dialog .el-dialog__header {
  background: rgba(255, 255, 255, 0.03) !important;
  padding: 12px 16px !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
}

.glass-dialog .el-dialog__title {
  color: var(--text-color) !important;
  font-size: 20px !important;
  font-weight: 600 !important;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.glass-dialog .el-dialog__footer {
  background: rgba(255, 255, 255, 0.03) !important;
  padding: 12px 16px !important;
  border-top: 1px solid rgba(255, 255, 255, 0.05) !important;
}

/* 移动端响应式 */
@media (max-width: 768px) {
  .header {
    height: calc(var(--spacing-unit) * 10);
    padding: 0 calc(var(--spacing-unit) * 3);
  }

  .logo {
    height: calc(var(--spacing-unit) * 6);
    margin-right: calc(var(--spacing-unit) * 2);
  }

  .title {
    font-size: calc(var(--spacing-unit) * 2.7);
  }

  .login-container {
    padding: calc(var(--spacing-unit) * 4) calc(var(--spacing-unit) * 3);
  }

  .glass-card {
    padding: calc(var(--spacing-unit) * 6);
    max-width: 95%;
    box-shadow:
        0 10px 40px rgba(0, 0, 0, 0.5),
        0 0 15px rgba(19, 206, 102, 0.1);
  }

  .login-header h2 {
    font-size: calc(var(--spacing-unit) * 3.8);
  }

  .subtitle {
    font-size: calc(var(--spacing-unit) * 1.9);
  }

  .form-group {
    margin-bottom: 18px;
  }

  .form-input .el-input__inner {
    height: 44px !important;
    line-height: 44px !important;
    padding: 10px 12px 10px 40px !important;
    font-size: 15px;
  }

  .form-input .el-input__prefix {
    left: 12px;
    font-size: 16px;
  }

  .form-label {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-color);
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
    letter-spacing: 0.3px;
    margin-bottom: 8px; /* 统一移动端标签与输入框间距 */
  }

  /* 移动端手机号输入框容器 */
  .phone-input-container {
    height: 44px;
  }

  /* 移动端短信验证码按钮 */
  .sms-code-btn {
    height: 44px;
    font-size: 12px;
    padding: 0 8px;
    width: 100px;
  }

  /* 移动端验证码 */
  .identify-code {
    height: 44px;
    width: 100px;
  }

  /* 移动端登录方式选择器 */
  .login-method-selector {
    margin-bottom: 20px;
  }

  /* 移动端选项卡 */
  .login-tabs {
    max-width: 280px;
  }

  .login-tabs .el-tabs__item {
    font-size: 14px;
    padding: 10px 0;
  }

  /* 移动端角色选择 */
  .role-select .el-input__inner {
    height: 44px !important;
    line-height: 44px !important;
    font-size: 14px !important;
  }

  .form-actions {
    margin-top: 20px;
  }

  .neon-button {
    height: 44px;
    font-size: 15px;
  }

  .secondary-actions {
    flex-direction: column;
    align-items: center;
    margin-top: 15px;
  }

  .secondary-actions .el-button--text {
    margin: 5px 0 !important;
    font-size: 14px;
  }

  /* 移动端验证码输入框 */
  .code-input-wrapper {
    gap: 8px;
  }

  .sms-input-wrapper {
    gap: 8px;
  }
}

/* 横屏模式 */
@media (max-width: 768px) and (orientation: landscape) {
  .login-container {
    padding: calc(var(--spacing-unit) * 3);
  }

  .glass-card {
    padding: calc(var(--spacing-unit) * 4);
    max-width: 80%;
  }

  .form-actions {
    margin-top: 15px;
  }

  .secondary-actions {
    flex-direction: row;
    justify-content: space-between;
  }

  .secondary-actions .el-button--text {
    margin: 0 !important;
  }
}

/* 动画 */
@keyframes flipInY {
  from {
    transform: perspective(400px) rotate3d(0, 1, 0, 90deg);
    animation-timing-function: ease-in;
    opacity: 0;
  }
  40% {
    transform: perspective(400px) rotate3d(0, 1, 0, -20deg);
    animation-timing-function: ease-in;
  }
  60% {
    transform: perspective(400px) rotate3d(0, 1, 0, 10deg);
    opacity: 1;
  }
  80% {
    transform: perspective(400px) rotate3d(0, 1, 0, -5deg);
  }
  to {
    transform: perspective(400px);
  }
}

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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes shimmer {
  0% { background-position: -100% 0; }
  100% { background-position: 100% 0; }
}

/* 新增动画效果 */
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
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

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes bounceIn {
  0% {
    opacity: 0;
    transform: scale(0.3);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
  70% {
    transform: scale(0.9);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

/* 平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 高级加载动画 */
@keyframes pulseGlow {
  0%, 100% {
    box-shadow: 0 0 20px rgba(16, 185, 129, 0.3);
  }
  50% {
    box-shadow: 0 0 40px rgba(16, 185, 129, 0.6);
  }
}

/* 输入框聚焦时的脉冲效果 */
.form-input .el-input__inner:focus {
  animation: pulseGlow 2s ease-in-out infinite;
}

/* 按钮点击波纹效果 */
.neon-button {
  position: relative;
  overflow: hidden;
}

.neon-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.neon-button:active::before {
  width: 300px;
  height: 300px;
}

/* 表单组进入动画 */
.form-group {
  animation: slideInUp 0.6s ease-out;
  animation-fill-mode: both;
}

.form-group:nth-child(1) { animation-delay: 0.1s; }
.form-group:nth-child(2) { animation-delay: 0.2s; }
.form-group:nth-child(3) { animation-delay: 0.3s; }
.form-group:nth-child(4) { animation-delay: 0.4s; }

/* 卡片悬停时的微动画 */
.glass-card:hover .card-glow {
  animation: innerGlow 2s ease-in-out infinite;
}

/* 输入框图标动画 */
.form-input .el-input__prefix i {
  transition: all 0.3s ease;
}

.form-input .el-input__inner:focus ~ .el-input__prefix i {
  transform: scale(1.2);
  color: var(--primary-color) !important;
}

/* 验证码刷新动画 */
.identify-code {
  transition: all 0.3s ease;
}

.identify-code:hover {
  transform: scale(1.05);
  box-shadow: 0 0 20px rgba(16, 185, 129, 0.4);
}

/* 短信按钮状态动画 */
.sms-code-btn {
  position: relative;
  overflow: hidden;
}

.sms-code-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.sms-code-btn:hover::before {
  left: 100%;
}

/* 无障碍支持 */
.el-input__inner:focus {
  outline: none;
}

/* 带前缀的手机号输入框样式 - 优化布局 */
.phone-input-wrapper {
  position: relative;
}


/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

::-webkit-scrollbar-thumb {
  background: rgba(19, 206, 102, 0.3);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(19, 206, 102, 0.5);
}
</style>
