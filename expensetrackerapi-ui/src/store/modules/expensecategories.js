import ExpenseCategoriesService from '../../services/expense-categories-service'
import { ADD_ALERT } from '@/store/_actiontypes'

const state = {
  expenseCategories: []
}

const actions = {
  logout ({ commit }) {
    commit('logout')
  },
  loadExpenseCategories ({ commit }) {
    return ExpenseCategoriesService.getAllExpenseCategories().then(expenseCategories => {
      commit('getAllExpenseCategories', expenseCategories.totalCategories)
    })
  },
  createExpenseCategory ({ commit, dispatch }, expenseCategory) {
    return ExpenseCategoriesService.createExpenseCategory(expenseCategory).then(expenseCategory => {
      commit('createExpenseCategory', expenseCategory)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Expense category added successfully', color: 'success' }, { root: true })
    })
  },
  modifyExpenseCategory ({ commit, dispatch }, expenseCategory) {
    return ExpenseCategoriesService.modifyExpenseCategory(expenseCategory).then(expenseCategory => {
      commit('modifyExpenseCategory', expenseCategory)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Expense category updated successfully', color: 'success' }, { root: true })
    })
  },
  deleteExpenseCategory ({ commit, dispatch }, expenseCategoryId) {
    console.log(expenseCategoryId)
    ExpenseCategoriesService.deleteExpenseCategory(expenseCategoryId)
      .then(() => {
        commit('deleteExpenseCategory', expenseCategoryId)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Expense category deleted successfully', color: 'success' }, { root: true }) // eslint-disable-next-line
      })
  }
}

const mutations = {
  getAllExpenseCategories (state, expenseCategories) {
    state.expenseCategories = expenseCategories
  },
  createExpenseCategory (state, expenseCategory) {
    state.expenseCategories.push(expenseCategory)
  },
  modifyExpenseCategory (state, expenseCategory) {
    const oldCategoryName = state.expenseCategories.find(ec => ec.categoryName === expenseCategory.oldCategoryName)
    oldCategoryName.categoryName = expenseCategory.categoryName
  },
  deleteExpenseCategory (state, expenseCategoryId) {
    state.expenseCategories = state.expenseCategories.filter(ec => ec.expenseCategoryId !== expenseCategoryId)
  },
  logout (state) {
    state.expenseCategories = []
  }
}

export const expenseCategories = {
  namespaced: true,
  state,
  actions,
  mutations
}
