import Vue from 'vue'
import Router from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Categories from '@/views/Categories.vue'
import Transactions from '@/views/Transactions.vue'

Vue.use(Router)

export const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },
    {
      path: '/profile',
      name: 'Profile',
      // lazy-loaded
      component: () => import('../views/Profile.vue')
    },
    {
      path: '/categories',
      name: 'Categories',
      component: Categories
    },
    {
      path: '/transactions',
      name: 'Transactions',
      component: Transactions
    },
    {
      path: '/addExpenseTransaction',
      name: 'AddExpenseTransaction',
      component: () => import('@/views/AddExpenseTransaction.vue')
    },
    {
      path: '/addIncomeTransaction',
      name: 'AddIncomeTransaction',
      component: () => import('@/views/AddIncomeTransaction.vue')
    },
    {
      path: '/addExpenseCategory',
      name: 'AddExpenseCategory',
      component: () => import('@/views/AddExpenseCategory.vue')
    },
    {
      path: '/addIncomeCategory',
      name: 'AddIncomeCategory',
      component: () => import('@/views/AddIncomeCategory.vue')
    },
    {
      path: '/modifyUser',
      name: 'ModifyUser',
      component: () => import('@/views/ModifyUser.vue')
    },
    {
      path: '/admin',
      name: 'Admin',
      // lazy-loaded
      component: () => import('../views/Admin.vue')
    }
  ]
})
