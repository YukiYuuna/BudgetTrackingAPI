import Vue from 'vue'
import Vuex from 'vuex'
import { auth } from './auth.module'
import { categories } from './category.module'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {},
  getters: {},
  mutations: {},
  actions: {},
  modules: {
    auth,
    categories
  }
})
