import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";


Vue.use(VueRouter)

// 在routes数组中移除dashboard路由，因为它已经在manageRoute.children中定义
// 修改前
const routes = [
{
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
  {
    path: '/mall/points-history',
    name: '积分历史',
    component: () => import('../views/mall/MallPointsHistory.vue')
  },
  {
    path: '/mall/points-tasks',
    name: '积分任务',
    component: () => import('../views/mall/PointsTasks.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/detection',
    name: '目标检测',
    component: () => import('../views/Detection.vue')
  },
  {
    path: '/test',
    name: 'TestPage',
    component: () => import('../views/TestPage.vue')
  },
  {
    path: '/aiChat',
    name: 'AI聊天',
    component: () => import('../views/AiChat.vue')
  },
  // 移除独立的dashboard路由定义，因为它已经在manageRoute.children中定义
  {
    path: '/expert',
    name: '专家咨询',
    component: () => import('../views/ExpertConsultation.vue')
  },
  {
    path: '/farmland',
    name: '农田管理',
    component: () => import('../views/Farmland.vue')
  },
  {
    path: '/farmlandInfo',
    name: '农田详情',
    component: () => import('../views/FarmlandInfo.vue')
  },
  {
    path: '/im',
    name: '即时通讯',
    component: () => import('../views/Im.vue')
  },
  {
    path: '/menu',
    name: '菜单管理',
    component: () => import('../views/Menu.vue')
  },
  {
    path: '/notice',
    name: '通知管理',
    component: () => import('../views/Notice.vue')
  },
  {
    path: '/mall',
    name: '积分商城',
    component: () => import('../views/mall/MallIndex.vue')
  },
  {
    path: '/mall-user',
    name: '用户商城',
    component: () => import('../views/mall/MallUser.vue')
  },
  {
    path: '/mall-admin',
    name: '商城管理',
    component: () => import('../views/mall/MallAdmin.vue')
  },
  {
    path: '/purchase',
    name: '采购管理',
    component: () => import('../views/Purchase.vue')
  },
  {
    path: '/role',
    name: '角色管理',
    component: () => import('../views/Role.vue')
  },
  {
    path: '/sales',
    name: '销售管理',
    component: () => import('../views/Sales.vue')
  },
  {
    path: '/statistic',
    name: '统计分析',
    component: () => import('../views/Statistic.vue')
  },
  {    path: '/user',    name: '用户管理',    component: () => import('../views/User.vue')  },  {    path: '/user/:username/:action',    name: '用户操作',    component: () => import('../views/User.vue')  },
  {
    path: '/warningdashboard',
    name: '预警仪表盘',
    component: () => import('../views/WarningDashboard.vue')
  },
  {
    path: '/warningmanagement',
    name: '预警管理',
    component: () => import('../views/WarningManagement.vue')
  },
  {
    path: '/pestmonitoring',
    name: '虫害监测',
    component: () => import('../views/PestMonitoring.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

// 重置路由
export const resetRouter = () => {
  router.matcher = new VueRouter({
    mode: 'history',
    routes
  })
}

// 注意：刷新页面会导致页面路由重置
export const setRoutes = () => {
  const storeMenus = localStorage.getItem("menus");
  if (storeMenus) {
    // 拼装动态路由
    const manageRoute = { path: '/', name: 'Manage', component: () => import('../views/Manage.vue'), redirect: "/home", children: [
        { path: 'home', name: '首页', component: () => import('../views/Home.vue')},
        { path: 'person', name: '个人信息', component: () => import('../views/PersonalInfo.vue')},
        { path: 'personalSettings', name: '个人设置', component: () => import('../views/PersonalSettings.vue')},
        { path: 'password', name: '修改密码', component: () => import('../views/Password.vue')},
        { path: 'map', name: '地图可视化', component: () => import('../components/MapView.vue')},
        { path: 'dashboard', name: '环境监测', component: () => import('../views/Dashboard.vue')},
        { path: 'detection', name: '目标检测系统', component: () => import('../views/Detection.vue')},
        { path: 'debug', name: '调试菜单', component: () => import('../components/DebugMenu.vue')},
        { path: 'aiChat', name: 'AI聊天', component: () => import('../views/AiChat.vue')},
        { path: 'expert', name: '专家咨询', component: () => import('../views/ExpertConsultation.vue')},
        { path: 'farmland', name: '农田管理', component: () => import('../views/Farmland.vue')},
        { path: 'farmlandInfo', name: '农田详情', component: () => import('../views/FarmlandInfo.vue')},
        { path: 'im', name: '即时通讯', component: () => import('../views/Im.vue')},
        { path: 'menu', name: '菜单管理', component: () => import('../views/Menu.vue')},
        { path: 'notice', name: '通知管理', component: () => import('../views/Notice.vue')},
        { path: 'purchase', name: '采购管理', component: () => import('../views/Purchase.vue')},
        { path: 'role', name: '角色管理', component: () => import('../views/Role.vue')},
        { path: 'sales', name: '销售管理', component: () => import('../views/Sales.vue')},
        { path: 'statistic', name: '统计分析', component: () => import('../views/Statistic.vue')},
        { path: 'user', name: '用户管理', component: () => import('../views/User.vue')},
        { path: 'warningdashboard', name: '预警仪表盘', component: () => import('../views/WarningDashboard.vue')},
        { path: 'warningmanagement', name: '预警管理', component: () => import('../views/WarningManagement.vue')},
        { path: 'pestmonitoring', name: '虫害监测', component: () => import('../views/PestMonitoring.vue')}
      ] }
    
    // 修复：确保解析菜单数据时处理可能的拼写错误
    let menus = [];
    try {
      menus = JSON.parse(storeMenus);
      // 修复菜单数据中的拼写错误
      menus = menus.map(menu => {
        // 修复路径中的拼写错误
        if (menu.path && menu.path.includes('dashbord')) {
          menu.path = menu.path.replace('dashbord', 'dashboard');
        }
        // 修复页面路径中的拼写错误
        if (menu.pagePath === 'Dashbord') {
          menu.pagePath = 'Dashboard';
        }
        // 递归处理子菜单
        if (menu.children) {
          menu.children = menu.children.map(child => {
            if (child.path && child.path.includes('dashbord')) {
              child.path = child.path.replace('dashbord', 'dashboard');
            }
            if (child.pagePath === 'Dashbord') {
              child.pagePath = 'Dashboard';
            }
            return child;
          });
        }
        return menu;
      });
    } catch (e) {
      console.error('Error parsing menus from localStorage:', e);
      menus = [];
    }

    // ======= 添加积分商城菜单 =======
    const mallMenu = {
      id: 101,
      name: "积分商城",
      icon: "el-icon-shopping-bag",
      path: "/mall",
      pagePath: "mall/Mall",
      children: [
        {
          id: 1011,
          name: "商品列表",
          path: "/mall",
          pagePath: "mall/Mall",
          icon: "el-icon-goods"
        },
        {
          id: 1012,
          name: "兑换记录",
          path: "/mall/orders",
          pagePath: "mall/MallOrders",
          icon: "el-icon-document"
        },
        {
          id: 1013,
          name: "积分历史",
          path: "/mall/points-history",
          pagePath: "mall/MallPointsHistory",
          icon: "el-icon-coins"
        },
        {
          id: 1014,
          name: "积分任务",
          path: "/mall/points-tasks",
          pagePath: "mall/PointsTasks",
          icon: "el-icon-star-on"
        }
      ]
    };

    // 先检查是否已存在积分商城菜单，避免重复添加
    if (!menus.some(item => item.id === mallMenu.id)) {
      menus.push(mallMenu);
    }

    // ======= 临时添加病虫害管理菜单 (请在实际菜单数据来源处修改) =======
    const pestManagementMenu = {
      id: 99, // 临时ID，请确保在实际数据中ID唯一
      name: "病虫害管理",
      icon: "el-icon-bug",
      path: null,
      children: [
        {
          id: 991, // 临时ID，请确保在实际数据中ID唯一
          name: "虫害监测",
          path: "/pestmonitoring",
          pagePath: "PestMonitoring",
          icon: "el-icon-search"
        },
        {
          id: 992, // 临时ID，请确保在实际数据中ID唯一
          name: "预警管理",
          path: "/warningmanagement",
          pagePath: "WarningManagement",
          icon: "el-icon-warning"
        },
        {
          id: 993, // 临时ID，请确保在实际数据中ID唯一
          name: "预警仪表盘",
          path: "/warningdashboard",
          pagePath: "WarningDashboard",
          icon: "el-icon-data-analysis"
        }
      ]
    };

    // 添加目标检测系统菜单
    const detectionMenu = {
      id: 100,
      name: "智能检测",
      icon: "el-icon-camera",
      path: null,
      children: [
        {
          id: 1001,
          name: "目标检测系统",
          path: "/detection",
          pagePath: "Detection",
          icon: "el-icon-view"
        }
      ]
    };

    // 先检查是否已存在这些菜单，避免重复添加
    if (!menus.some(item => item.id === pestManagementMenu.id)) {
      menus.push(pestManagementMenu);
    }
    if (!menus.some(item => item.id === detectionMenu.id)) {
      menus.push(detectionMenu);
    }
    // ===============================================================

    // ======= 将更新后的菜单数据存回 localStorage =======
    localStorage.setItem("menus", JSON.stringify(menus)); // 更新 localStorage 中的菜单数据
    // ================================================

    menus.forEach(item => {
      if (item.path && item.pagePath) {  // 确保path和pagePath都存在
        // 检查是否已经存在同名路由，避免重复添加
        const existingRoute = router.getRoutes().find(route => route.name === item.name);
        if (!existingRoute) {
          // 处理嵌套路径的情况
          if (item.pagePath.includes('/')) {
            // 对于包含路径分隔符的pagePath，直接使用
            let itemMenu = { path: item.path, name: item.name, component: () => import('../views/' + item.pagePath + '.vue')}
            manageRoute.children.push(itemMenu)
          } else {
            // 保留路径中的斜杠，确保与菜单配置匹配
            let itemMenu = { path: item.path, name: item.name, component: () => import('../views/' + item.pagePath + '.vue')}
            manageRoute.children.push(itemMenu)
          }
        }
      } else if(item.children && item.children.length) {
        item.children.forEach(subItem => {
          if (subItem.path && subItem.pagePath) {  // 确保path和pagePath都存在
             // 检查是否已经存在同名路由
            const existingRoute = router.getRoutes().find(route => route.name === subItem.name);
            if (!existingRoute) {
              // 处理嵌套路径的情况
              if (subItem.pagePath.includes('/')) {
                // 对于包含路径分隔符的pagePath，直接使用
                let itemMenu = { path: subItem.path, name: subItem.name, component: () => import('../views/' + subItem.pagePath + '.vue')}
                manageRoute.children.push(itemMenu)
              } else {
                // 保留路径中的斜杠，确保与菜单配置匹配
                let itemMenu = { path: subItem.path, name: subItem.name, component: () => import('../views/' + subItem.pagePath + '.vue')}
                manageRoute.children.push(itemMenu)
              }
            }
          }
        })
      }
    })

    // 获取当前的路由对象名称数组
    const currentRouteNames = router.getRoutes().map(v => v.name)
    if (!currentRouteNames.includes('Manage')) {
      // 动态添加到现在的路由对象中去
      router.addRoute(manageRoute)
    }
  }
}

// 每次刷新页面都要重新设置路由，否则路由就会被重置
setRoutes()

// 白名单路径，不需要登录即可访问
const whiteList = ['/login', '/register', '/404']

// 添加404路由
const notFoundRoute = {
  path: '/404',
  name: 'NotFound',
  component: () => import('../views/NotFound.vue')
}

// 检查404路由是否已存在
if (!routes.some(route => route.path === '/404')) {
  routes.push(notFoundRoute)
}

router.beforeEach((to, from, next) => {
  localStorage.setItem("currentPathName", to.name)  // 设置当前的路由名称
  store.commit("setPath")
  
  // 检查用户是否已登录
  const isLoggedIn = !!localStorage.getItem("user")
  
  // 如果访问的不是白名单路径且未登录，则重定向到登录页
  if (!whiteList.includes(to.path) && !isLoggedIn) {
    next("/login")
  } 
  // 如果已登录且访问的是登录页或注册页，则重定向到首页
  else if (isLoggedIn && (to.path === '/login' || to.path === '/register')) {
    // 修复：如果从登录页/注册页重定向，直接跳转到dashboard而不是home
    next({ path: '/dashboard', replace: true })
  }
  // 修复：处理重定向链中的拼写错误
  else if (to.path === '/dashbord') {
    // 重定向到正确拼写的路径
    next({ path: '/dashboard', replace: true })
  }
  // 检查路由是否存在 - 修复无限重定向问题
  else if (!to.matched.length) {
    next({ path: '/404', replace: true })
  } else {
    next()
  }
})

export default router
