<template>
  <div style="line-height: 60px; display: flex;">
    <div style="flex: 1;">
      <span :class="collapseBtnClass" style="cursor: pointer; font-size: 18px" @click="collapse"></span>

      <el-breadcrumb separator="/" style="display: inline-block; margin-left: 10px">
        <el-breadcrumb-item :to="'/'">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentPathName }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <el-dropdown style="width: 150px; cursor: pointer; font-size: 1.3em; text-align: right">
      <div style="display: inline-block">
        <img :src="getAvatarUrl(user.avatarUrl)" alt=""
             style="width: 30px; border-radius: 50%; position: relative; top: 10px; right: 5px"
             @error="handleAvatarError">
        <span>{{ user.nickname }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
      </div>
      <el-dropdown-menu slot="dropdown" style="width: 100px; text-align: center">
        <el-dropdown-item style="font-size: 14px; padding: 5px 0">
          <router-link to="/password">修改密码</router-link>
        </el-dropdown-item>
        <el-dropdown-item style="font-size: 14px; padding: 5px 0">
          <router-link to="/person">个人信息</router-link>
        </el-dropdown-item>
        <el-dropdown-item style="font-size: 14px; padding: 5px 0">
          <div style="text-decoration: none"  @click="logout">退出</div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: "Header",
  props: {
    collapseBtnClass: String,
    user: Object
  },
  computed: {
    currentPathName () {
      return this.$store.state.currentPathName;　　//需要监听的数据
    }
  },
  data() {
    return {

    }
  },
  methods: {
    collapse() {
      // this.$parent.$parent.$parent.$parent.collapse()  // 通过4个 $parent 找到父组件，从而调用其折叠方法
      this.$emit("asideCollapse")
    },
    logout() {
      this.$store.commit("logout")
      this.$message.success("退出成功")
    },
    
    // 获取头像URL
    getAvatarUrl(avatarUrl) {
      if (!avatarUrl) return ''
      
      // 如果已经是完整的URL，直接返回
      if (avatarUrl.startsWith('http')) {
        return avatarUrl
      }
      
      // 如果是相对路径，添加域名
      if (avatarUrl.startsWith('/')) {
        return window.location.origin + avatarUrl
      }
      
      // 其他情况直接返回
      return avatarUrl
    },
    
    // 处理头像加载错误
    handleAvatarError(event) {
      console.error('头像加载失败:', event.target.src)
      // 设置默认头像或隐藏图片
      event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAiIGhlaWdodD0iMzAiIHZpZXdCb3g9IjAgMCAzMCAzMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTUiIGN5PSIxNSIgcj0iMTUiIGZpbGw9IiNEOUQ5RDkiLz4KPHBhdGggZD0iTTE1IDEwQzE3LjIwOTEgMTAgMTkgMTEuNzkwOSAxOSAxNEMxOSAxNi4yMDkxIDE3LjIwOTEgMTggMTUgMThDMTIuNzkwOSAxOCAxMSAxNi4yMDkxIDExIDE0QzExIDExLjc5MDkgMTIuNzkwOSAxMCAxNSAxMFoiIGZpbGw9IiM5OTk5OTkiLz4KPHBhdGggZD0iTTE1IDIwQzEwLjU4MTcgMjAgNyAyMi41ODE3IDcgMjciIHN0cm9rZT0iIzk5OTk5OSIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiLz4KPC9zdmc+'
    }
  }
}
</script>

<style scoped>
</style>
