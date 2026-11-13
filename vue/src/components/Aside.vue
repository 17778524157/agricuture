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
      <b style="color: white; margin-left: 3px" v-show="logoTextShow">厚朴花开</b>
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
    // 【改造】兜底菜单：仅保留所有人都能访问的"公共菜单"。
    //   正常登录后侧边栏永远使用后端按角色(sys_role_menu)返回的 menus；
    //   这里只在 localStorage 异常为空时兜底，不能再放 用户管理/库存/订单 等
    //   任何业务菜单，以免在兜底情况下绕过角色隔离。
    const defaultMenus = [
      {
        id: 1,
        name: "首页",
        icon: "el-icon-s-home",
        path: "/home",
        pagePath: "Home"
      },
      {
        id: 2,
        name: "个人信息",
        icon: "el-icon-user",
        path: "/person",
        pagePath: "PersonalInfo"
      },
      {
        id: 3,
        name: "修改密码",
        icon: "el-icon-lock",
        path: "/password",
        pagePath: "Password"
      }
    ];

    // 【改造】侧边栏菜单直接来自 localStorage（登录成功时写入的、后端按角色过滤好的菜单）
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
