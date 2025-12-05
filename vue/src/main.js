import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/gloable.css'
import request from "@/utils/request";
import 'animate.css/animate.min.css'
// 导入lottie-player组件
import '@lottiefiles/lottie-player'

Vue.config.productionTip = false

Vue.use(ElementUI)

Vue.prototype.request=request
Vue.prototype.$http=request

// 添加事件总线
Vue.prototype.$bus = new Vue()
new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')
