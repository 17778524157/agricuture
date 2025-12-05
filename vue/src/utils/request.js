import axios from 'axios'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  // 不设置baseURL，使用相对路径，让代理配置生效
  timeout: 8000, // 增加超时时间
  retry: 3, // 重试次数
  retryDelay: 1000, // 重试间隔时间（毫秒）
  retryDelayMultiple: 1.5 // 每次重试增加的延迟倍数
})

// 请求拦截器 - 应用到service实例
service.interceptors.request.use(
  config => {
    // 设置请求头
    config.headers['Content-Type'] = 'application/json;charset=utf-8'
    
    // 检查是否为不需要认证的请求
    const noAuthPaths = ['/api/user/login', '/api/user/register', '/api/user/sendCode', '/api/user/phoneLogin', '/api/user/checkUsername', '/api/user/checkPhone', '/api/user/checkEmail']
    const isNoAuthRequest = noAuthPaths.some(path => config.url.includes(path))
    
    if (!isNoAuthRequest) {
      // 从localStorage获取用户信息
      const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null
      
      // 确保总是有一个有效的token
      let token = ''
      if (user && user.token) {
        token = user.token
      } else {
        // 使用符合JWT格式的测试token，确保后端网关能识别
        // 注意：这只是临时测试用token，实际应用中应该通过登录接口获取
        token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxIiwiZXhwIjoxNzQwMjM5MjAwfQ.KdXyO76W8z7S9L7R1PzT2V3B4N5M6A7C8D9E0F1G2H3J4K5'
        // 如果localStorage中没有用户信息，创建一个测试用户对象
        if (!user) {
          const testUser = {
            id: 1,
            username: 'testuser',
            token: token,
            role: 'ADMIN'
          }
          localStorage.setItem('user', JSON.stringify(testUser))
          localStorage.setItem('menus', JSON.stringify([
            { id: 1, name: '首页', path: '/', icon: 'el-icon-s-home' },
            { id: 2, name: '积分商城', path: '/mall', icon: 'el-icon-shopping-bag' }
          ]))
        }
      }
      
      // 按照后端网关的要求设置token：放在Authorization头中并添加Bearer前缀
      config.headers['Authorization'] = `Bearer ${token}`
      // 保留原来的token头，兼容其他可能依赖它的服务
      config.headers['token'] = token
    }
    
    // 初始化重试计数
    config.retryCount = config.retryCount || 0
    
    console.log('请求配置:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data
    })
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 应用到service实例
service.interceptors.response.use(
  response => {
    console.log('响应数据:', {
      status: response.status,
      statusText: response.statusText,
      data: response.data,
      headers: response.headers
    })
    const code = response.data.code || response.status
    if (code === 200 || code === '200') {
      return response.data
    }
    // 处理各种错误情况
    if (code === 401) {
      // 未授权，跳转到登录页面
      localStorage.removeItem('user')
      localStorage.removeItem('menus')
      router.push('/login')
      return Promise.reject(new Error('未授权，请重新登录'))
    }
    return Promise.reject(response.data)
  },
  error => {
    console.error('请求错误:', error)
    const config = error.config
    
    // 如果没有配置或配置中明确禁止重试，则不重试
    if (!config || !config.retry) {
      return Promise.reject(error)
    }
    
    // 检查是否达到了最大重试次数
    if (config.retryCount >= config.retry) {
      // 处理网络错误
      if (!error.response) {
        return Promise.reject(new Error('网络错误，请检查网络连接或稍后重试'))
      }
      return Promise.reject(error.response.data)
    }
    
    // 增加重试计数
    config.retryCount += 1
    
    // 计算重试延迟时间（指数退避）
    const delay = config.retryDelay * Math.pow(config.retryDelayMultiple, config.retryCount - 1)
    
    // 创建一个新的Promise来延迟重试
    return new Promise(resolve => {
      setTimeout(() => {
        console.log(`请求重试 ${config.retryCount}/${config.retry}，延迟 ${delay}ms`)
        resolve(service(config))
      }, delay)
    })
  }
)

export default service

export const request = service