import IncomeCategoriesService from '../../services/income-categories-service'
import { ADD_ALERT, CREATE_NEWCATEGORY_STATISTICS, EDIT_CATEGORY_STATISTICS, REMOVE_INCOMEOFCATEGORY, LOAD_CATEGORIES_BREAKDOWN, LOAD_INCOME_BREAKDOWN } from '@/store/_actiontypes'

const state = {
  incomeCategories: []
}

const actions = {
  loadIncomeCategories ({ commit }) {
    return IncomeCategoriesService.getAllIncomeCategories().then(incomeCategories => {
      commit('getAllIncomeCategories', incomeCategories.totalCategories)
    })
  },
  createIncomeCategory ({ commit, dispatch }, incomeCategory) {
    return IncomeCategoriesService.createIncomeCategory(incomeCategory).then(incomeCategory => {
      commit('createIncomeCategory', incomeCategory)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Income category added successfully', color: 'success' }, { root: true })
      dispatch(`statistics/${CREATE_NEWCATEGORY_STATISTICS}`, { category: incomeCategory }, { root: true })
    })
  },
  modifyIncomeCategory ({ commit, dispatch }, incomeCategory) {
    return IncomeCategoriesService.modifyIncomeCategory(incomeCategory).then(incomeCategory => {
      commit('modifyIncomeCategory', incomeCategory)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Income category updated successfully', color: 'success' }, { root: true })
      dispatch(`statistics/${EDIT_CATEGORY_STATISTICS}`, { category: incomeCategory }, { root: true })
    })
  },
  deleteIncomeCategory ({ commit, dispatch }, categoryName) {
    IncomeCategoriesService.deleteIncomeCategory(categoryName)
      .then(() => {
        commit('deleteIncomeCategory', categoryName)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Income category deleted successfully', color: 'success' }, { root: true }) // eslint-disable-next-line
        dispatch(`income/${REMOVE_INCOMEOFCATEGORY}`, { categoryName: categoryName }, { root: true }) // eslint-disable-next-line
        dispatch(`statistics/${LOAD_CATEGORIES_BREAKDOWN}`, {}, { root: true }) // eslint-disable-next-line
        dispatch(`statistics/${LOAD_INCOME_BREAKDOWN}`, {}, { root: true })
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
    const oldCategoryName = state.incomeCategories.find(ec => ec.categoryName === incomeCategory.oldCategoryName)
    oldCategoryName.categoryName = incomeCategory.categoryName
  },
  deleteIncomeCategory (state, categoryName) {
    state.incomeCategories = state.incomeCategories.filter(ec => ec.categoryName !== categoryName)
  }
}

export const incomeCategories = {
  namespaced: true,
  state,
  actions,
  mutations
}
