import ExpenseCategoriesService from '../../services/expense-categories-service'
import { ADD_ALERT, CREATE_NEWCATEGORY_STATISTICS, EDIT_CATEGORY_STATISTICS, REMOVE_EXPENSESOFCATEGORY, LOAD_CATEGORIES_BREAKDOWN, LOAD_EXPENSES_BREAKDOWN } from '@/store/_actiontypes'

const state = {
  expenseCategories: []
}

const actions = {
  loadExpenseCategories ({ commit }) {
    return ExpenseCategoriesService.getAllExpenseCategories().then(expenseCategories => {
      console.log(expenseCategories.totalCategories)
      commit('getAllExpenseCategories', expenseCategories.totalCategories)
    })
  },
  createExpenseCategory ({ commit, dispatch }, expenseCategory) {
    return ExpenseCategoriesService.createExpenseCategory(expenseCategory).then(expenseCategory => {
      commit('createExpenseCategory', expenseCategory)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Expense category added successfully', color: 'success' }, { root: true })
      dispatch(`statistics/${CREATE_NEWCATEGORY_STATISTICS}`, { category: expenseCategory }, { root: true })
    })
  },
  modifyExpenseCategory ({ commit, dispatch }, categoryName, expenseCategory) {
    return ExpenseCategoriesService.modifyExpenseCategory(categoryName, expenseCategory).then(expenseCategory => {
      commit('modifyExpenseCategory', expenseCategory)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Expense category updated successfully', color: 'success' }, { root: true })
      dispatch(`statistics/${EDIT_CATEGORY_STATISTICS}`, { category: expenseCategory }, { root: true })
    })
  },
  deleteExpenseCategory ({ commit, dispatch }, categoryName) {
    ExpenseCategoriesService.deleteExpenseCategory(categoryName)
      .then(() => {
        commit('deleteExpenseCategory', categoryName)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Expense category deleted successfully', color: 'success' }, { root: true })
        dispatch(`expenses/${REMOVE_EXPENSESOFCATEGORY}`, { categoryName: categoryName }, { root: true })
        dispatch(`statistics/${LOAD_CATEGORIES_BREAKDOWN}`, {}, { root: true })
        dispatch(`statistics/${LOAD_EXPENSES_BREAKDOWN}`, {}, { root: true })
      })
  }
}

const mutations = {
  getAllExpenseCategories (state, expenseCategories) {
    state.expenseCategories = expenseCategories
    console.log(state.expenseCategories)
  },
  createExpenseCategory (state, expenseCategory) {
    state.expenseCategories.push(expenseCategory)
  },
  modifyExpenseCategory (state, expenseCategory) {
    const expenseCategoryUpdated = state.expenseCategories.find(ec => ec.categoryName === expenseCategory.categoryName)
    expenseCategoryUpdated.name = expenseCategory.name
    expenseCategoryUpdated.description = expenseCategory.description
    expenseCategoryUpdated.budget = expenseCategory.budget
    expenseCategoryUpdated.colourHex = expenseCategory.colourHex
  },
  deleteExpenseCategory (state, categoryName) {
    state.expenseCategories = state.expenseCategories.filter(ec => ec.categoryName !== categoryName)
  }
}

export const expenseCategories = {
  namespaced: true,
  state,
  actions,
  mutations
}
