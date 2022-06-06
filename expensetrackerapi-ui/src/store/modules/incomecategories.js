import IncomeCategoriesService from '../../services/income-categories-service'
import { ADD_ALERT } from '@/store/_actiontypes'

const state = {
  incomeCategories: []
}

const actions = {
  logout ({ commit }) {
    commit('logout')
  },
  loadIncomeCategories ({ commit }) {
    return IncomeCategoriesService.getAllIncomeCategories().then(incomeCategories => {
      commit('getAllIncomeCategories', incomeCategories.totalCategories)
    })
  },
  createIncomeCategory ({ commit, dispatch }, incomeCategory) {
    return IncomeCategoriesService.createIncomeCategory(incomeCategory).then(incomeCategory => {
      commit('createIncomeCategory', incomeCategory)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Income category added successfully', color: 'success' }, { root: true })
    })
  },
  modifyIncomeCategory ({ commit, dispatch }, incomeCategory) {
    return IncomeCategoriesService.modifyIncomeCategory(incomeCategory).then(incomeCategory => {
      commit('modifyIncomeCategory', incomeCategory)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Income category updated successfully', color: 'success' }, { root: true })
    })
  },
  deleteIncomeCategory ({ commit, dispatch }, incomeCategoryId) {
    IncomeCategoriesService.deleteIncomeCategory(incomeCategoryId)
      .then(() => {
        commit('deleteIncomeCategory', incomeCategoryId)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Income category deleted successfully', color: 'success' }, { root: true }) // eslint-disable-next-line
      })
  }
}

const mutations = {
  getAllIncomeCategories (state, incomeCategories) {
    state.incomeCategories = incomeCategories
  },
  createIncomeCategory (state, incomeCategory) {
    state.incomeCategories.push(incomeCategory)
  },
  modifyIncomeCategory (state, incomeCategory) {
    const oldCategoryName = state.incomeCategories.find(ic => ic.categoryName === incomeCategory.oldCategoryName)
    oldCategoryName.categoryName = incomeCategory.categoryName
  },
  deleteIncomeCategory (state, incomeCategoryId) {
    state.incomeCategories = state.incomeCategories.filter(ic => ic.incomeCategoryId !== incomeCategoryId)
  },
  logout (state) {
    state.incomeCategories = []
  }
}

export const incomeCategories = {
  namespaced: true,
  state,
  actions,
  mutations
}
