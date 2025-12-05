<template>
  <el-menu :default-openeds="opens" style="min-height: 100%; overflow-x: hidden"
           background-color="rgb(62, 115, 97)"
           text-color="#fff"
           active-text-color="#797979"
           :collapse-transition="false"
           :collapse="isCollapse"
           router
  >
    <div style="height: 60px; line-height: 60px; text-align: center">
      <img src="../assets/logo.png" alt="" style="width: 50px; position: relative; top: 5px;">
      <b style="color: white; margin-left: 3px" v-show="logoTextShow">酸枣种植管理平台</b>
    </div>
    <div v-for="item in menus" :key="item.id">
      <div v-if="item.path">
        <el-menu-item :index="item.path">
          <i :class="item.icon"></i>
          <span slot="title">{{ item.name }}</span>
        </el-menu-item>
      </div>
      <div v-else>
        <el-submenu :index="item.id + ''">
          <template slot="title">
            <i :class="item.icon"></i>
            <span slot="title">{{ item.name }}</span>
          </template>
          <div  v-for="subItem in item.children" :key="subItem.id">
            <el-menu-item :index="subItem.path">
              <i :class="subItem.icon"></i>
              <span slot="title">{{ subItem.name }}</span>
            </el-menu-item>
          </div>
        </el-submenu>
      </div>
    </div>
  </el-menu>
</template>

<script>
export default {
  name: "Aside",
  props: {
    isCollapse: Boolean,
    logoTextShow: Boolean
  },
  data() {
    // 默认菜单数据，确保即使localStorage为空，侧边栏也能显示菜单项
    const defaultMenus = [
      {
            id: 1,
            name: "首页",
            icon: "el-icon-s-home",
            path: "/home",
            pagePath: "Home"
          },
          { 
            id: 0,
            name: "环境监测", 
            icon: "el-icon-data-analysis",
            path: "/dashboard",
            pagePath: "Dashboard"
          },
      {
        id: 2,
        name: "个人信息",
        icon: "el-icon-user",
        path: "/person",
        pagePath: "Person"
      },
      {
        id: 3,
        name: "修改密码",
        icon: "el-icon-lock",
        path: "/password",
        pagePath: "Password"
      },
      {
        id: 4,
        name: "地图可视化",
        icon: "el-icon-map-location",
        path: "/map",
        pagePath: "MapView"
      },
      {
        id: 7,
        name: "积分商城",
        icon: "el-icon-shopping-bag",
        path: "/mall",
        pagePath: "MallIndex"
      },
      {
        id: 5,
        name: "智能检测",
        icon: "el-icon-camera",
        path: null,
        children: [
          {
            id: 51,
            name: "目标检测系统",
            path: "/detection",
            pagePath: "Detection",
            icon: "el-icon-view"
          }
        ]
      },
      {
        id: 6,
        name: "病虫害管理",
        icon: "el-icon-bug",
        path: null,
        children: [
          {
            id: 61,
            name: "虫害监测",
            path: "/pestmonitoring",
            pagePath: "PestMonitoring",
            icon: "el-icon-search"
          },
          {
            id: 62,
            name: "预警管理",
            path: "/warningmanagement",
            pagePath: "WarningManagement",
            icon: "el-icon-warning"
          }
        ]
      }
    ];

    // 从localStorage获取菜单数据，如果没有则使用默认数据
    const storedMenus = localStorage.getItem("menus") ? JSON.parse(localStorage.getItem("menus")) : defaultMenus;
    
    // 如果使用了默认数据，将其存入localStorage以便路由设置能正确读取
    if (!localStorage.getItem("menus")) {
      localStorage.setItem("menus", JSON.stringify(defaultMenus));
    }

    return {
      menus: storedMenus,
      opens: storedMenus.map(v => v.id + '')
    }
  },
  mounted() {
    // 确保路由已经设置
    this.$nextTick(() => {
      const event = new Event('storage');
      window.dispatchEvent(event);
    });
  }
}
</script>

<style scoped>
.el-menu-item.is-active {
  background-color: rgb(38, 52, 69) !important;
}
.el-menu-item:hover {
  background-color: rgb(38, 52, 69) !important;
}

.el-submenu__title:hover {
  background-color: rgb(38, 52, 69) !important;
}
/*解决收缩菜单文字不消失问题*/
.el-menu--collapse span {
  visibility: hidden;
}
</style>
