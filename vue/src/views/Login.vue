<template>
  <div class="login-page" :class="{ 'dark-mode': isDarkMode }">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-blob blob-1"></div>
      <div class="bg-blob blob-2"></div>
      <div class="bg-blob blob-3"></div>
      <div class="bg-grid"></div>
    </div>

    <!-- 主容器 -->
    <div class="login-main">
      <!-- 左侧品牌展示区 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-logo">
            <div class="logo-icon">
              <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M32 4C32 4 12 18 12 36C12 47 21 56 32 56C43 56 52 47 52 36C52 18 32 4 32 4Z" fill="url(#leafGrad)" opacity="0.9"/>
                <path d="M32 14V48" stroke="white" stroke-width="2" stroke-linecap="round" opacity="0.8"/>
                <path d="M32 24C24 24 20 28 20 32" stroke="white" stroke-width="2" stroke-linecap="round" opacity="0.7"/>
                <path d="M32 32C40 32 44 28 44 24" stroke="white" stroke-width="2" stroke-linecap="round" opacity="0.7"/>
                <path d="M32 40C26 38 22 36 22 34" stroke="white" stroke-width="1.5" stroke-linecap="round" opacity="0.6"/>
                <defs>
                  <linearGradient id="leafGrad" x1="12" y1="4" x2="52" y2="56" gradientUnits="userSpaceOnUse">
                    <stop stop-color="#34d399"/>
                    <stop offset="0.5" stop-color="#10b981"/>
                    <stop offset="1" stop-color="#059669"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
          </div>
          <h1 class="brand-title">厚朴花开</h1>
          <p class="brand-subtitle">智慧农业 · 精准种植 · 数据驱动</p>
          <div class="brand-features">
            <div class="feature-item">
              <div class="feature-icon">
                <i class="el-icon-data-line"></i>
              </div>
              <div class="feature-text">
                <h4>数据监测</h4>
                <p>实时监测农田环境数据</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <i class="el-icon-bug"></i>
              </div>
              <div class="feature-text">
                <h4>病虫害识别</h4>
                <p>AI智能识别病虫害</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <i class="el-icon-sunny"></i>
              </div>
              <div class="feature-text">
                <h4>气象服务</h4>
                <p>精准天气预报预警</p>
              </div>
            </div>
          </div>
        </div>
        <div class="brand-footer">
          <p> 智慧农业平台</p>
        </div>
      </div>

      <!-- 右侧登录表单区 -->
      <div class="form-section">
        <div class="form-wrapper">
          <!-- 主题切换 -->
          <div class="theme-toggle">
            <el-tooltip :content="isDarkMode ? '切换到亮色模式' : '切换到暗色模式'" placement="left">
              <div class="theme-btn" @click="toggleTheme">
                <i :class="isDarkMode ? 'el-icon-sunny' : 'el-icon-moon'"></i>
              </div>
            </el-tooltip>
          </div>

          <div class="form-card">
            <div class="form-header">
              <h2>欢迎回来</h2>
              <p>登录您的账户以继续</p>
            </div>

            <!-- 登录方式切换 -->
            <div class="login-tabs">
              <div
                class="tab-item"
                :class="{ active: loginType === 'username' }"
                @click="loginType = 'username'">
                <i class="el-icon-user"></i>
                <span>账号登录</span>
              </div>
              <div
                class="tab-item"
                :class="{ active: loginType === 'phone' }"
                @click="loginType = 'phone'">
                <i class="el-icon-phone"></i>
                <span>手机登录</span>
              </div>
            </div>

            <!-- 用户名登录表单 -->
            <el-form
              v-if="loginType === 'username'"
              :model="usernameFormData"
              :rules="usernameRules"
              ref="usernameForm"
              class="login-form">

              <el-form-item prop="username" class="form-item">
                <el-input
                    v-model="usernameFormData.username"
                    class="lp-input"
                    placeholder="请输入用户名"
                    prefix-icon="el-icon-user">
                </el-input>
              </el-form-item>

              <el-form-item prop="password" class="form-item">
                <el-input
                    v-model="usernameFormData.password"
                    type="password"
                    class="lp-input"
                    placeholder="请输入密码"
                    show-password
                    prefix-icon="el-icon-lock">
                </el-input>
              </el-form-item>

              <el-form-item prop="code" class="form-item">
                <div class="captcha-row">
                  <el-input
                      v-model="usernameFormData.code"
                      class="lp-input captcha-input"
                      placeholder="请输入验证码"
                      prefix-icon="el-icon-key">
                  </el-input>
                  <div class="captcha-img" @click="refreshCode" title="点击刷新验证码">
                    <Identify :identifyCode="identifyCode" />
                    <i class="el-icon-refresh refresh-icon"></i>
                  </div>
                </div>
              </el-form-item>

              <!-- 角色选择卡片 -->
              <div class="form-item">
                <div class="role-select-label">
                  <i class="el-icon-s-custom"></i>
                  <span>选择您的身份</span>
                </div>
                <div class="role-card-grid">
                  <div
                      v-for="role in roleList"
                      :key="role.value"
                      class="role-card"
                      :class="{ active: usernameFormData.role === role.value }"
                      :style="{ '--role-color': role.color }"
                      @click="selectRole(role.value, 'username')">
                    <div class="role-card-icon">
                      <i :class="role.icon"></i>
                    </div>
                    <div class="role-card-info">
                      <div class="role-card-name">{{ role.name }}</div>
                      <div class="role-card-desc">{{ role.desc }}</div>
                    </div>
                    <div v-if="usernameFormData.role === role.value" class="role-card-check">
                      <i class="el-icon-check"></i>
                    </div>
                  </div>
                </div>
              </div>
            </el-form>

            <!-- 手机号登录表单 -->
            <el-form
              v-if="loginType === 'phone'"
              :model="phoneFormData"
              :rules="phoneRules"
              ref="phoneForm"
              class="login-form">

              <el-form-item prop="phone" class="form-item">
                <div class="phone-input-wrap">
                <div class="phone-prefix">
                  <span class="country-code">+86</span>
                  <i class="el-icon-arrow-down"></i>
                </div>
                <el-input
                    v-model="phoneFormData.phone"
                    class="lp-input phone-input-el"
                    placeholder="请输入手机号">
                </el-input>
                </div>
              </el-form-item>

              <el-form-item prop="smsCode" class="form-item">
                <div class="sms-row">
                  <el-input
                      v-model="phoneFormData.smsCode"
                      class="lp-input sms-input-el"
                      placeholder="请输入短信验证码"
                      prefix-icon="el-icon-message">
                  </el-input>
                  <el-button
                      class="sms-btn"
                      :class="{ disabled: countdown > 0 }"
                      :disabled="countdown > 0"
                      @click="sendSmsCode">
                    {{ countdown > 0 ? `${countdown}s 后重发` : '获取验证码' }}
                  </el-button>
                </div>
              </el-form-item>

              <!-- 角色选择卡片 -->
              <div class="form-item">
                <div class="role-select-label">
                  <i class="el-icon-s-custom"></i>
                  <span>选择您的身份</span>
                </div>
                <div class="role-card-grid">
                  <div
                      v-for="role in roleList"
                      :key="role.value"
                      class="role-card"
                      :class="{ active: phoneFormData.role === role.value }"
                      :style="{ '--role-color': role.color }"
                      @click="selectRole(role.value, 'phone')">
                    <div class="role-card-icon">
                      <i :class="role.icon"></i>
                    </div>
                    <div class="role-card-info">
                      <div class="role-card-name">{{ role.name }}</div>
                      <div class="role-card-desc">{{ role.desc }}</div>
                    </div>
                    <div v-if="phoneFormData.role === role.value" class="role-card-check">
                      <i class="el-icon-check"></i>
                    </div>
                  </div>
                </div>
              </div>
            </el-form>

            <!-- 登录按钮 -->
            <button
              class="login-btn"
              :class="{ loading: loading }"
              @click="login">
              <span v-if="!loading" class="btn-text">登 录</span>
              <span v-else class="btn-loading">
                <i class="el-icon-loading"></i>
                登录中...
              </span>
            </button>

            <!-- 辅助链接 -->
            <div class="form-links">
              <el-button type="text" class="link-btn" @click="$router.push('/register')">
                注册账号
              </el-button>
              <el-button type="text" class="link-btn" @click="handlePass">
                忘记密码？
              </el-button>
            </div>

            <!-- 分割线 -->
            <div class="divider">
              <span>其他登录方式</span>
            </div>

<!--            &lt;!&ndash; 社交登录 &ndash;&gt;
            <div class="social-login">
              <div class="social-item" title="微信登录">
                <svg viewBox="0 0 24 24" fill="currentColor" class="social-icon wechat">
                  <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.047c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 0 1-.023-.156.49.49 0 0 1 .201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-6.656-6.088V8.89c-.135-.01-.27-.027-.407-.03zm-2.53 3.274c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.969-.982z"/>
                </svg>
              </div>
              <div class="social-item" title="QQ登录">
                <svg viewBox="0 0 24 24" fill="currentColor" class="social-icon qq">
                  <path d="M21.395 15.035a39.548 39.548 0 0 0-.803-2.264l-1.079-2.695c.001-.032.014-.396.014-.396 0-4.636-3.023-8.398-7.527-8.398S4.473 5.044 4.473 9.68c0 .239.02.612.02.612l-1.113 2.783a40.543 40.543 0 0 0-.747 2.132c-.123.411-.232.783-.29 1.07-.172.864.083 1.181.395 1.181.272 0 .594-.278.791-.465.079-.075.165-.158.255-.237-.038.397-.061.796-.061 1.196 0 1.739.47 3.359 1.34 4.708.29.45.625.868 1.001 1.238-.218.178-.476.382-.771.604-.886.665-1.458 1.373-1.458 2.072 0 1.391 2.888 2.294 6.315 2.294s6.315-.903 6.315-2.294c0-.7-.572-1.407-1.458-2.072-.295-.222-.553-.426-.771-.604.376-.37.711-.788 1.001-1.238.87-1.349 1.34-2.969 1.34-4.708 0-.4-.023-.799-.061-1.196.09.079.176.162.255.237.197.187.519.465.791.465.312 0 .567-.317.395-1.181-.058-.287-.167-.659-.29-1.07z"/>
                </svg>
              </div>
              <div class="social-item" title="微博登录">
                <svg viewBox="0 0 24 24" fill="currentColor" class="social-icon weibo">
                  <path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.739 5.443zm7.41-12.086c-.257-.069-.435-.108-.302-.47.231-.624.256-1.166.009-1.567-.46-.746-1.745-.666-3.233.057 0 0-.466.305-.352-.258.22-1.086.086-2.014-.421-2.562-1.153-1.242-4.203.048-6.812 2.882C4.005 8.302 2 11.038 2 13.285 2 17.873 6.268 21 11.281 21c6.361 0 10.638-3.72 10.638-6.668 0-1.775-1.5-2.794-4.411-4.095zM18.5 6.5c-.276 0-.5-.224-.5-.5 0-1.93-1.57-3.5-3.5-3.5-.276 0-.5-.224-.5-.5s.224-.5.5-.5c2.485 0 4.5 2.015 4.5 4.5 0 .276-.224.5-.5.5zm3 1.5c-.276 0-.5-.224-.5-.5 0-3.59-2.91-6.5-6.5-6.5-.276 0-.5-.224-.5-.5s.224-.5.5-.5c4.142 0 7.5 3.358 7.5 7.5 0 .276-.224.5-.5.5z"/>
                </svg>
              </div>
            </div>-->
          </div>

          <p class="copyright"> 智慧农业平台 · All Rights Reserved</p>
        </div>
      </div>
    </div>

    <!-- 找回密码弹窗 -->
    <el-dialog
        title="找回密码"
        :visible.sync="dialogFormVisible"
        width="420px"
        custom-class="lp-reset-dialog"
        :close-on-click-modal="false">
      <div class="reset-content">
        <p class="reset-tip">请输入您的用户名和手机号，我们将为您重置密码</p>
        <el-form :model="pass" label-width="80px" size="medium">
          <el-form-item label="用户名">
            <el-input v-model="pass.username" placeholder="请输入用户名" prefix-icon="el-icon-user"></el-input>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="pass.phone" placeholder="请输入手机号" prefix-icon="el-icon-phone"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" class="lp-cancel-btn">取消</el-button>
        <el-button type="primary" @click="passwordBack" class="lp-confirm-btn">重置密码</el-button>
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
      loginType: 'username',
      user: {
        username: '',
        password: '',
        code: ''
      },
      usernameFormData: {
        username: '',
        password: '',
        code: '',
        role: ''
      },
      phoneUser: {
        phone: '',
        smsCode: ''
      },
      phoneFormData: {
        phone: '',
        smsCode: '',
        role: ''
      },
      selectedRole: '',
      pass: {},
      code: '',
      dialogFormVisible: false,
      identifyCode: '',
      identifyCodes: '3456789ABCDEFGHGKMNPQRSTUVWXY',
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
      },
      phoneRules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        smsCode: [
          { required: true, message: '请输入短信验证码', trigger: 'blur' },
          { min: 4, max: 6, message: '验证码长度为4-6位', trigger: 'blur' }
        ]
      },
      countdown: 0,
      isDarkMode: false,
      loading: false,
      // 【改造】角色只保留 3 种；value 必须与 sys_role.flag、sys_user.role 完全一致
      roleList: [
        { value: 'ROLE_ADMIN',     name: '系统管理员', icon: 'el-icon-s-tools', desc: '拥有全部菜单与功能权限', color: '#f59e0b' },
        { value: 'ROLE_WAREHOUSE', name: '仓库管理员', icon: 'el-icon-box',     desc: '入库/库存/盘点/出库',    color: '#10b981' },
        { value: 'ROLE_SALES',     name: '销售业务员', icon: 'el-icon-sell',    desc: '客户管理/销售订单',      color: '#3b82f6' },
      ]
    }
  },
  components: {Identify},
  mounted() {
    resetRouter()
    this.refreshCode()
  },
  methods: {
    login() {
      this.loading = true
      if (this.loginType === 'username') {
        this.$refs['usernameForm'].validate((valid) => {
          if (valid) {
            if (!this.usernameFormData.role) {
              this.$message.warning('请选择您的身份')
              this.loading = false
              return
            }
            this.handleUsernameLogin()
          } else {
            this.loading = false
          }
        })
      } else {
        this.$refs['phoneForm'].validate((valid) => {
          if (valid) {
            if (!this.phoneFormData.role) {
              this.$message.warning('请选择您的身份')
              this.loading = false
              return
            }
            this.handlePhoneLogin()
          } else {
            this.loading = false
          }
        })
      }
    },
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
    handlePhoneLogin() {
      const loginData = {
        phone: this.phoneFormData.phone,
        code: this.phoneFormData.smsCode,
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
    handleLoginSuccess(res) {
      if(res.code === '200' || res.code === 200) {
        localStorage.setItem("user", JSON.stringify(res.data))
        localStorage.setItem("menus", JSON.stringify(res.data.menus))
        setRoutes()
        this.$router.push("/")
        this.$message.success("登录成功")
      } else {
        this.$message.error(res.msg)
        if (this.loginType === 'username') {
          this.refreshCode()
        }
      }
    },
    handleLoginError(error) {
      let errorMessage = '登录失败，请稍后重试'
      if (error.response) {
        const response = error.response
        if (response.status === 401) {
          errorMessage = '认证失败，请检查用户名和密码'
        } else if (response.status === 403) {
          errorMessage = '访问被拒绝，请联系管理员'
        } else if (response.status === 404) {
          errorMessage = '服务未找到，请检查网络连接'
        } else if (response.status === 500) {
          errorMessage = '服务器内部错误，请稍后重试'
        }
        if (response.data) {
          if (response.data.message) errorMessage = response.data.message
          else if (response.data.msg) errorMessage = response.data.msg
          else if (response.data.error) errorMessage = response.data.error
          else if (typeof response.data === 'string') errorMessage = response.data
        }
      } else if (error.data) {
        if (error.data.message) errorMessage = error.data.message
        else if (error.data.msg) errorMessage = error.data.msg
        else if (error.data.error) errorMessage = error.data.error
        else if (typeof error.data === 'string') errorMessage = error.data
      } else if (error.request) {
        errorMessage = '网络连接失败，请检查网络连接或后端服务是否运行'
      } else if (error.message) {
        errorMessage = error.message
      }
      this.$message.error(errorMessage)
      if (this.loginType === 'username') {
        this.refreshCode()
      }
    },
    refreshCode() {
      this.identifyCode = ''
      this.makeCode(this.identifyCodes, 4)
    },
    makeCode(o, l) {
      for (let i = 0; i < l; i++) {
        this.identifyCode += this.identifyCodes[Math.floor(Math.random() * (this.identifyCodes.length))]
      }
    },
    sendSmsCode() {
      if (!/^1[3-9]\d{9}$/.test(this.phoneFormData.phone)) {
        this.$message.error('请输入正确的手机号码')
        return
      }
      this.request.post(`/api/user/sendCode?phone=${this.phoneFormData.phone}`)
          .then(res => {
            if (res.code === '200' || res.code === 200) {
              this.$message.success('短信验证码已发送')
              this.startCountdown()
            } else {
              this.$message.error(res.msg || '发送失败，请稍后重试')
            }
          })
          .catch(() => {
            this.$message.error('发送失败，请稍后重试')
          })
    },
    startCountdown() {
      this.countdown = 60
      const timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    },
    handlePass() {
      this.dialogFormVisible = true
      this.pass = {}
    },
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
    toggleTheme() {
      this.isDarkMode = !this.isDarkMode
      document.body.classList.toggle('dark-mode', this.isDarkMode)
    },
    selectRole(role, type) {
      if (type === 'username') {
        this.usernameFormData.role = role
      } else {
        this.phoneFormData.role = role
      }
    },
  }
}
</script>

<style>
/* ===== 登录页面样式 - 全局作用域使用 .login-page 前缀 */

.login-page {
  --lp-primary: #10b981;
  --lp-primary-light: #34d399;
  --lp-primary-dark: #059669;
  --lp-primary-gradient: linear-gradient(135deg, #10b981 0%, #059669 100%);
  --lp-accent-gradient: linear-gradient(135deg, #10b981 0%, #3b82f6 100%);

  --lp-bg-primary: #f8fafc;
  --lp-bg-secondary: #ffffff;
  --lp-bg-card: rgba(255, 255, 255, 0.92);

  --lp-text-primary: #1e293b;
  --lp-text-secondary: #64748b;
  --lp-text-muted: #94a3b8;

  --lp-border-color: #e2e8f0;
  --lp-border-focus: #10b981;

  --lp-shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.05);
  --lp-shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);
  --lp-shadow-lg: 0 20px 60px rgba(0, 0, 0, 0.12);
  --lp-shadow-primary: 0 4px 20px rgba(16, 185, 129, 0.3);

  --lp-radius-sm: 8px;
  --lp-radius-md: 12px;
  --lp-radius-lg: 20px;

  min-height: 100vh;
  background: var(--lp-bg-primary);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  transition: background 0.3s ease;
  font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, sans-serif;
}

.login-page.dark-mode {
  --lp-bg-primary: #0f172a;
  --lp-bg-secondary: #1e293b;
  --lp-bg-card: rgba(30, 41, 59, 0.92);
  --lp-text-primary: #f1f5f9;
  --lp-text-secondary: #94a3b8;
  --lp-text-muted: #64748b;
  --lp-border-color: #334155;
  --lp-shadow-lg: 0 20px 60px rgba(0, 0, 0, 0.5);
}

/* 背景装饰 */
.login-page .bg-decoration {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  overflow: hidden;
  pointer-events: none;
}

.login-page .bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: lp-blob-float 20s ease-in-out infinite;
}

.login-page .blob-1 {
  width: 500px; height: 500px;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.3), rgba(59, 130, 246, 0.2));
  top: -200px; right: -150px;
}

.login-page .blob-2 {
  width: 400px; height: 400px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.25), rgba(16, 185, 129, 0.2));
  bottom: -150px; left: -100px;
  animation-delay: -7s;
}

.login-page .blob-3 {
  width: 350px; height: 350px;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.2), rgba(16, 185, 129, 0.15));
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}

@keyframes lp-blob-float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

.login-page .bg-grid {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.08) 1px, transparent 1px);
  background-size: 50px 50px;
}

/* 主容器 */
.login-page .login-main {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 1100px;
  display: flex;
  background: var(--lp-bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--lp-radius-lg);
  box-shadow: var(--lp-shadow-lg);
  border: 1px solid var(--lp-border-color);
  overflow: hidden;
  min-height: 620px;
  animation: lp-fade-in-up 0.6s ease-out;
}

@keyframes lp-fade-in-up {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 左侧品牌区 */
.login-page .brand-section {
  flex: 1;
  background: var(--lp-accent-gradient);
  padding: 50px 45px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
  min-width: 0;
}

.login-page .brand-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: lp-brand-shine 15s ease-in-out infinite;
}

@keyframes lp-brand-shine {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(180deg); }
}

.login-page .brand-content {
  position: relative;
  z-index: 1;
}

.login-page .brand-logo {
  margin-bottom: 24px;
}

.login-page .logo-icon {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  animation: lp-logo-bounce 3s ease-in-out infinite;
}

@keyframes lp-logo-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.login-page .logo-icon svg {
  width: 42px;
  height: 42px;
}

.login-page .brand-title {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px 0;
  letter-spacing: 2px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.login-page .brand-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 40px 0;
  letter-spacing: 0.5px;
}

.login-page .brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.login-page .feature-item {
  display: flex;
  align-items: center;
  gap: 14px;
  transition: transform 0.3s ease;
}

.login-page .feature-item:hover {
  transform: translateX(6px);
}

.login-page .feature-icon {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-page .feature-text h4 {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 3px 0;
}

.login-page .feature-text p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
  margin: 0;
}

.login-page .brand-footer {
  position: relative;
  z-index: 1;
}

.login-page .brand-footer p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  margin: 0;
}

/* 右侧表单区 */
.login-page .form-section {
  flex: 1;
  padding: 50px 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  min-width: 0;
}

.login-page .theme-toggle {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

.login-page .theme-btn {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: var(--lp-bg-primary);
  border: 1px solid var(--lp-border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: var(--lp-text-secondary);
  font-size: 16px;
}

.login-page .theme-btn:hover {
  background: var(--lp-primary);
  color: #fff;
  border-color: var(--lp-primary);
  transform: rotate(180deg);
}

.login-page .form-wrapper {
  width: 100%;
  max-width: 380px;
}

.login-page .form-header {
  text-align: center;
  margin-bottom: 28px;
}

.login-page .form-header h2 {
  font-size: 26px;
  font-weight: 700;
  color: var(--lp-text-primary);
  margin: 0 0 6px 0;
}

.login-page .form-header p {
  font-size: 13px;
  color: var(--lp-text-secondary);
  margin: 0;
}

/* 登录方式切换 */
.login-page .login-tabs {
  display: flex;
  background: var(--lp-bg-primary);
  border-radius: var(--lp-radius-md);
  padding: 4px;
  margin-bottom: 24px;
  border: 1px solid var(--lp-border-color);
}

.login-page .tab-item {
  flex: 1;
  padding: 9px 14px;
  text-align: center;
  border-radius: var(--lp-radius-sm);
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 13px;
  font-weight: 500;
  color: var(--lp-text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  user-select: none;
}

.login-page .tab-item:hover {
  color: var(--lp-text-primary);
}

.login-page .tab-item.active {
  background: var(--lp-bg-secondary);
  color: var(--lp-primary);
  box-shadow: var(--lp-shadow-sm);
  font-weight: 600;
}

/* 表单 */
.login-page .login-form {
  width: 100%;
}

.login-page .form-item {
  margin-bottom: 18px;
}

/* 输入框 */
.login-page .lp-input .el-input__inner {
  background: var(--lp-bg-primary) !important;
  border: 1.5px solid var(--lp-border-color) !important;
  border-radius: var(--lp-radius-md) !important;
  padding: 0 15px 0 40px !important;
  height: 48px !important;
  line-height: 48px !important;
  color: var(--lp-text-primary) !important;
  font-size: 14px !important;
  transition: all 0.25s ease !important;
  box-shadow: none !important;
}

.login-page .lp-input .el-input__inner:focus {
  border-color: var(--lp-primary) !important;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1) !important;
  background: var(--lp-bg-secondary) !important;
}

.login-page .lp-input .el-input__prefix {
  left: 12px !important;
  color: var(--lp-text-muted) !important;
  font-size: 16px !important;
  transition: color 0.25s ease !important;
}

.login-page .lp-input .el-input__inner:focus + .el-input__prefix,
.login-page .lp-input:focus-within .el-input__prefix {
  color: var(--lp-primary) !important;
}

.login-page .lp-input .el-input__inner::placeholder {
  color: var(--lp-text-muted) !important;
}

.login-page .lp-input .el-input__suffix {
  color: var(--lp-text-muted) !important;
}

.login-page .lp-input .el-input__suffix:hover {
  color: var(--lp-primary) !important;
}

/* 验证码行 */
.login-page .captcha-row {
  display: flex;
  gap: 10px;
  align-items: stretch;
}

.login-page .captcha-input {
  flex: 1;
}

.login-page .captcha-img {
  width: 110px;
  height: 48px;
  border-radius: var(--lp-radius-md);
  background: var(--lp-bg-primary);
  border: 1.5px solid var(--lp-border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.login-page .captcha-img:hover {
  border-color: var(--lp-primary);
  transform: translateY(-2px);
  box-shadow: var(--lp-shadow-sm);
}

.login-page .captcha-img .refresh-icon {
  position: absolute;
  right: 5px;
  top: 5px;
  font-size: 11px;
  color: var(--lp-text-muted);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.login-page .captcha-img:hover .refresh-icon {
  opacity: 1;
}

/* 手机号输入 */
.login-page .phone-input-wrap {
  display: flex;
  align-items: stretch;
  border: 1.5px solid var(--lp-border-color);
  border-radius: var(--lp-radius-md);
  background: var(--lp-bg-primary);
  height: 48px;
  overflow: hidden;
  transition: all 0.25s ease;
}

.login-page .phone-input-wrap:focus-within {
  border-color: var(--lp-primary);
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.login-page .phone-prefix {
  padding: 0 12px;
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--lp-text-primary);
  font-size: 13px;
  font-weight: 500;
  border-right: 1.5px solid var(--lp-border-color);
  cursor: pointer;
  transition: color 0.25s ease;
}

.login-page .phone-prefix:hover {
  color: var(--lp-primary);
}

.login-page .phone-prefix .country-code {
  font-weight: 600;
}

.login-page .phone-prefix .el-icon-arrow-down {
  font-size: 11px;
  color: var(--lp-text-muted);
}

.login-page .phone-input-el {
  flex: 1;
}

.login-page .phone-input-el .el-input__inner {
  border: none !important;
  background: transparent !important;
  padding-left: 12px !important;
  height: 100% !important;
  line-height: 48px !important;
  box-shadow: none !important;
}

/* 短信验证码 */
.login-page .sms-row {
  display: flex;
  gap: 10px;
}

.login-page .sms-input-el {
  flex: 1;
}

.login-page .sms-btn {
  width: 115px;
  height: 48px;
  border-radius: var(--lp-radius-md) !important;
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0) !important;
  border: 1.5px solid var(--lp-border-color) !important;
  color: var(--lp-text-secondary) !important;
  font-size: 12px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
  padding: 0 !important;
  flex-shrink: 0;
}

.login-page .sms-btn:hover:not(.disabled):not(:disabled) {
  background: var(--lp-primary-gradient) !important;
  color: #fff !important;
  border-color: var(--lp-primary) !important;
  transform: translateY(-2px);
  box-shadow: var(--lp-shadow-primary);
}

.login-page .sms-btn.disabled,
.login-page .sms-btn:disabled {
  opacity: 0.6 !important;
  cursor: not-allowed !important;
}

/* 角色选择标签 */
.login-page .role-select-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--lp-text-primary);
  margin-bottom: 12px;
}

.login-page .role-select-label i {
  color: var(--lp-primary);
}

/* 角色卡片网格 */
.login-page .role-card-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.login-page .role-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: var(--lp-bg-primary);
  border: 1.5px solid var(--lp-border-color);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s ease;
  overflow: hidden;
}

.login-page .role-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--role-color);
  opacity: 0;
  transition: opacity 0.25s ease;
}

.login-page .role-card:hover {
  border-color: var(--role-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.login-page .role-card:hover::before {
  opacity: 0.5;
}

.login-page .role-card.active {
  border-color: var(--role-color);
  background: color-mix(in srgb, var(--role-color) 8%, var(--lp-bg-secondary));
  box-shadow: 0 4px 16px color-mix(in srgb, var(--role-color) 20%, transparent);
}

.login-page .role-card.active::before {
  opacity: 1;
}

.login-page .role-card-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: color-mix(in srgb, var(--role-color) 15%, transparent);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.25s ease;
}

.login-page .role-card-icon i {
  font-size: 18px;
  color: var(--role-color);
}

.login-page .role-card.active .role-card-icon {
  background: var(--role-color);
}

.login-page .role-card.active .role-card-icon i {
  color: white;
}

.login-page .role-card-info {
  flex: 1;
  min-width: 0;
}

.login-page .role-card-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--lp-text-primary);
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.login-page .role-card-desc {
  font-size: 11px;
  color: var(--lp-text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.login-page .role-card-check {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--role-color);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: role-check-pop 0.3s ease-out;
}

.login-page .role-card-check i {
  font-size: 10px;
  color: white;
  font-weight: bold;
}

@keyframes role-check-pop {
  0% { transform: scale(0); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

/* 角色选择下拉（保留旧类名兼容） */
.login-page .lp-select .el-input__inner {
  background: var(--lp-bg-primary) !important;
  border: 1.5px solid var(--lp-border-color) !important;
  border-radius: var(--lp-radius-md) !important;
  padding: 0 35px 0 15px !important;
  height: 48px !important;
  line-height: 48px !important;
  color: var(--lp-text-primary) !important;
  font-size: 14px !important;
  transition: all 0.25s ease !important;
}

.login-page .lp-select .el-input__inner:hover {
  border-color: var(--lp-primary) !important;
}

.login-page .lp-select .el-input__inner:focus {
  border-color: var(--lp-primary) !important;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1) !important;
}

/* 登录按钮 */
.login-page .login-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: var(--lp-radius-md);
  background: var(--lp-primary-gradient);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 3px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: var(--lp-shadow-primary);
  margin-top: 6px;
}

.login-page .login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s ease;
}

.login-page .login-btn:hover:not(.loading) {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
}

.login-page .login-btn:hover:not(.loading)::before {
  left: 100%;
}

.login-page .login-btn:active {
  transform: translateY(-1px);
}

.login-page .login-btn.loading {
  opacity: 0.8;
  cursor: not-allowed;
}

.login-page .btn-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-page .btn-loading .el-icon-loading {
  font-size: 16px;
  animation: lp-spin 1s linear infinite;
}

@keyframes lp-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 表单链接 */
.login-page .form-links {
  display: flex;
  justify-content: space-between;
  margin-top: 14px;
  font-size: 12px;
}

.login-page .link-btn {
  color: var(--lp-text-secondary) !important;
  padding: 0 !important;
  font-size: 13px !important;
  transition: color 0.25s ease !important;
}

.login-page .link-btn:hover {
  color: var(--lp-primary) !important;
}

/* 分割线 */
.login-page .divider {
  display: flex;
  align-items: center;
  margin: 24px 0;
  color: var(--lp-text-muted);
  font-size: 12px;
}

.login-page .divider::before,
.login-page .divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--lp-border-color);
}

.login-page .divider span {
  padding: 0 14px;
}

/* 社交登录 */
.login-page .social-login {
  display: flex;
  justify-content: center;
  gap: 18px;
}

.login-page .social-item {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1.5px solid var(--lp-border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: var(--lp-bg-primary);
}

.login-page .social-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--lp-shadow-md);
}

.login-page .social-icon {
  width: 20px;
  height: 20px;
}

.login-page .social-icon.wechat { color: #07c160; }
.login-page .social-icon.qq { color: #12b7f5; }
.login-page .social-icon.weibo { color: #e6162d; }

.login-page .social-item:hover .social-icon.wechat { color: #06ad56; }
.login-page .social-item:hover .social-icon.qq { color: #0da5dc; }
.login-page .social-item:hover .social-icon.weibo { color: #cc1428; }

/* 版权信息 */
.login-page .copyright {
  text-align: center;
  font-size: 11px;
  color: var(--lp-text-muted);
  margin-top: 26px;
}

/* 弹窗 */
.lp-reset-dialog .el-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.lp-reset-dialog .el-dialog__header {
  padding: 22px 24px 0 !important;
  border-bottom: none !important;
}

.lp-reset-dialog .el-dialog__title {
  font-size: 18px !important;
  font-weight: 600 !important;
}

.lp-reset-dialog .el-dialog__body {
  padding: 14px 24px 20px !important;
}

.login-page .reset-tip {
  font-size: 13px;
  color: var(--lp-text-secondary);
  margin: 0 0 16px 0;
  padding: 10px 14px;
  background: var(--lp-bg-primary);
  border-radius: 8px;
  border-left: 3px solid var(--lp-primary);
}

.lp-reset-dialog .el-dialog__footer {
  padding: 14px 24px 22px !important;
  border-top: 1px solid var(--lp-border-color) !important;
  text-align: center !important;
}

.login-page .lp-cancel-btn {
  width: 110px;
  border-radius: 8px !important;
}

.login-page .lp-confirm-btn {
  width: 110px;
  border-radius: 8px !important;
  background: var(--lp-primary-gradient) !important;
  border: none !important;
}

/* 响应式 */
@media (max-width: 960px) {
  .login-page .login-main {
    flex-direction: column;
    max-width: 460px;
    min-height: auto;
  }

  .login-page .brand-section {
    padding: 36px 30px;
  }

  .login-page .brand-features,
  .login-page .brand-footer {
    display: none;
  }

  .login-page .brand-title {
    font-size: 26px;
  }

  .login-page .logo-icon {
    width: 56px;
    height: 56px;
  }

  .login-page .logo-icon svg {
    width: 32px;
    height: 32px;
  }

  .login-page .form-section {
    padding: 36px 30px;
  }

  .login-page .copyright {
    display: none;
  }
}

@media (max-width: 480px) {
  .login-page {
    padding: 0;
  }

  .login-page .login-main {
    border-radius: 0;
    min-height: 100vh;
  }

  .login-page .brand-section {
    padding: 28px 22px;
  }

  .login-page .brand-title {
    font-size: 22px;
    margin-bottom: 4px;
  }

  .login-page .brand-subtitle {
    font-size: 13px;
    margin-bottom: 0;
  }

  .login-page .form-section {
    padding: 28px 22px;
  }

  .login-page .form-header h2 {
    font-size: 22px;
  }

  .login-page .tab-item {
    font-size: 12px;
    padding: 8px 10px;
  }

  .login-page .role-card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
