import ExpenseTransactionsService from '../../services/expense-transactions-service'
import { ADD_ALERT, EDIT_STATISTICS } from '@/store/_actiontypes'

const state = {
  expenseTransactions: []
}

const actions = {
  loadExpenseTransactions ({ commit }) {
    ExpenseTransactionsService.getAllExpenseTransactions().then(expenseTransactions => {
      commit('getAllExpenseTransactions', expenseTransactions)
    })
  },
  createExpenseTransaction ({ commit, dispatch }, expenseTransaction) {
    return ExpenseTransactionsService.createExpenseTransaction(expenseTransaction).then(expenseTransaction => {
      commit('createExpenseTransaction', expenseTransaction)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Expense Transaction added successfully', color: 'success' }, { root: true })
      dispatch(`statistics/${EDIT_STATISTICS}`, { expense: expenseTransaction, operation: 'create' }, { root: true })
    })
  },
  modifyExpenseTransaction ({ commit, dispatch }, expenseTransaction) {
    return ExpenseTransactionsService.modifyExpenseTransaction(expenseTransaction).then(expenseTransaction => {
      commit('modifyExpenseTransaction', expenseTransaction)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Expense Transaction updated successfully', color: 'success' }, { root: true })
      dispatch(`statistics/${EDIT_STATISTICS}`, { expense: expenseTransaction, operation: 'edit' }, { root: true })
    })
  },
  deleteExpenseTransaction ({ commit, dispatch }, expenseTransactionId) {
    ExpenseTransactionsService.deleteExpenseTransactionById(expenseTransactionId)
      .then(() => {
        const newExpenseTransactions = state.expenseTransactions.filter(et => et.expenseTransactionId === expenseTransactionId)[0]
        commit('deleteExpenseTransaction', expenseTransactionId)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Expense Transaction deleted successfully', color: 'success' }, { root: true })
        dispatch(`statistics/${EDIT_STATISTICS}`, { expense: newExpenseTransactions, operation: 'remove' }, { root: true })
      })
  },
  deleteExpenseTransactionsByCategory ({ commit, dispatch }, categoryName) {
    ExpenseTransactionsService.deleteExpenseTransactionByCategory(categoryName)
      .then(() => {
        const newExpenseTransactions = state.expenseTransactions.filter(et => et.categoryName === categoryName)[0]
        commit('deleteAllExpenseTransactionByCategory', categoryName)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Expense Transactions have been deleted successfully', color: 'success' }, { root: true })
        dispatch(`statistics/${EDIT_STATISTICS}`, { expense: newExpenseTransactions, operation: 'remove' }, { root: true })
      })
  }
}

const mutations = {
  getAllExpenseTransactions (state, expenseTransactions) {
    state.expenseTransactions = expenseTransactions
  },
  createExpenseTransaction (state, expenseTransaction) {
    state.expenseTransactions.push(expenseTransaction)
  },
  modifyExpenseTransaction (state, expenseTransactionId, expenseTransaction) {
    const expenseTransactionUpdated = state.expenseTransactions.find(et => et.expenseTransactionId === expenseTransactionId)
    expenseTransactionUpdated.name = expenseTransaction.name
    expenseTransactionUpdated.description = expenseTransaction.description
    expenseTransactionUpdated.budget = expenseTransaction.budget
    expenseTransactionUpdated.colourHex = expenseTransaction.colourHex
  },
  deleteExpenseTransaction (state, expenseTransactionId) {
    state.expenseTransactions = state.expenseTransactions.filter(et => et.expenseTransactionId !== expenseTransactionId)
  },
  deleteAllExpenseTransactionByCategory (state, categoryName) {
    state.expenseTransactions = state.expenseTransactions.filter(et => et.categoryName !== categoryName)
  }
}

export const expenseTransactions = {
  namespaced: true,
  state,
  actions,
  mutations
}
