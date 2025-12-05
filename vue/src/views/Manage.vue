<template>
  <el-container style="min-height: 100vh">

    <el-aside :width="sideWidth + 'px'" style="box-shadow: 2px 0 6px rgb(0 21 41);">
      <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" style="padding-bottom: 20px" />
    </el-aside>

    <el-container>
      <el-header style="border-bottom: 1px solid #ccc;">
        <Header :collapseBtnClass="collapseBtnClass" @asideCollapse="collapse" :user="user" />
      </el-header>

      <el-main >
<!--        表示当前页面的子路由会在 <router-view /> 里面展示-->
        <router-view @refreshUser="getUser" @userInfoUpdated="handleUserInfoUpdate" />
      </el-main>

    </el-container>
  </el-container>
</template>

<script>

import Aside from "@/components/Aside";
import Header from "@/components/Header";

export default {
  name: 'Home',
  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      logoTextShow: true,
      user: {},
      storageCheckInterval: null
    }
  },
  components: {
    Aside,
    Header
  },
  created() {
    // 从后台获取最新的User数据
    this.getUser()
    
    // 监听localStorage变化
    this.setupLocalStorageListener()
    
    // 监听自定义事件
    this.setupEventListeners()
  },
  
  beforeDestroy() {
    // 清理事件监听器
    this.cleanupEventListeners()
  },
  methods: {
    collapse() {  // 点击收缩按钮触发
      this.isCollapse = !this.isCollapse
      if (this.isCollapse) {  // 收缩
        this.sideWidth = 64
        this.collapseBtnClass = 'el-icon-s-unfold'
        this.logoTextShow = false
      } else {   // 展开
        this.sideWidth = 200
        this.collapseBtnClass = 'el-icon-s-fold'
        this.logoTextShow = true
      }
    },
    getUser() {
      let username = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).username : ""
      if (username) {
        // 从后台获取User数据
        this.request.get("/api/user/username/" + username).then(res => {
          // 重新赋值后台的最新User数据
          if (res.code === '200' || res.code === 200) {
            this.user = res.data
          } else {
            console.error('获取用户信息失败:', res.message)
          }
        }).catch(error => {
          console.error('获取用户信息异常:', error)
          // 如果获取失败，使用localStorage中的用户信息作为备用
          const localUser = JSON.parse(localStorage.getItem("user") || '{}')
          this.user = localUser
        })
      } else {
        console.warn('未找到用户名，无法获取用户信息')
      }
    },
    
    // 设置localStorage监听器
    setupLocalStorageListener() {
      // 监听localStorage变化
      window.addEventListener('storage', this.handleStorageChange)
      
      // 定期检查localStorage变化（作为备用方案）
      this.storageCheckInterval = setInterval(() => {
        this.checkLocalStorageChange()
      }, 1000)
    },
    
    // 设置事件监听器
    setupEventListeners() {
      // 监听子组件发出的事件
      this.$on('userInfoUpdated', this.handleUserInfoUpdate)
      
      // 监听全局事件总线
      if (this.$bus) {
        this.$bus.$on('userInfoUpdated', this.handleUserInfoUpdate)
      }
    },
    
    // 清理事件监听器
    cleanupEventListeners() {
      window.removeEventListener('storage', this.handleStorageChange)
      if (this.storageCheckInterval) {
        clearInterval(this.storageCheckInterval)
      }
      this.$off('userInfoUpdated', this.handleUserInfoUpdate)
      if (this.$bus) {
        this.$bus.$off('userInfoUpdated', this.handleUserInfoUpdate)
      }
    },
    
    // 处理localStorage变化
    handleStorageChange(event) {
      if (event.key === 'user' && event.newValue) {
        try {
          const newUser = JSON.parse(event.newValue)
          console.log('检测到localStorage用户信息变化:', newUser)
          this.user = newUser
        } catch (error) {
          console.error('解析localStorage用户信息失败:', error)
        }
      }
    },
    
    // 检查localStorage变化（备用方案）
    checkLocalStorageChange() {
      try {
        const currentUserStr = localStorage.getItem('user')
        if (currentUserStr) {
          const currentUser = JSON.parse(currentUserStr)
          // 比较关键字段是否有变化
          if (this.user.nickname !== currentUser.nickname || 
              this.user.avatarUrl !== currentUser.avatarUrl) {
            console.log('检测到用户信息变化，更新显示')
            this.user = currentUser
          }
        }
      } catch (error) {
        console.error('检查localStorage变化失败:', error)
      }
    },
    
    // 处理用户信息更新事件
    handleUserInfoUpdate(updatedUser) {
      console.log('收到用户信息更新事件:', updatedUser)
      console.log('更新前用户头像:', this.user.avatarUrl)
      console.log('更新后用户头像:', updatedUser.avatarUrl)
      this.user = updatedUser
      
      // 强制更新组件
      this.$forceUpdate()
    }
  }
}
</script>

