import Vue from 'vue'
import Vuex from 'vuex'
import { auth } from './auth.module'
import { categories } from './category.module'
import { allTransactions } from './transaction.module'
import { userModule } from './user.module'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {},
  getters: {},
  mutations: {},
  actions: {},
  modules: {
    auth,
    userModule,
    allTransactions,
    categories
  }
})
