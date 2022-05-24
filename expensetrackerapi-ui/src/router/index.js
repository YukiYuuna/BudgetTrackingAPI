import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import HomePage from '@/views/HomePage.vue'
import Expenses from '@/views/Expenses.vue'
import Settings from '@/views/Settings.vue'
import Profile from '@/views/Profile.vue'
import store from '@/store/index.js'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/', component: HomePage,
      children: [
        //HomePage's <router-view>
        { path: '/dashboard', component: Dashboard },
        { path: '/expenses', component: Expenses },
        { path: '/settings', component: Settings },
        { path: '/profile', component: Profile },
        { path: '/', component: Dashboard },
      ]
    },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '*', redirect: '/' }
  ],
  scrollBehavior (to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { x: 0, y: 0 }
    }
  }
})

router.beforeEach((to, from, next) => {
  const publicPages = ['/login', '/register'];
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = store.state.account.user !== undefined && true

  if (authRequired && !loggedIn) {
    return next('/login')
  }

  next()
})

export default router
