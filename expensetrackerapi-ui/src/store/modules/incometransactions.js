import IncomeTransactionsService from '../../services/income-transactions-service'
import { ADD_ALERT, EDIT_STATISTICS } from '@/store/_actiontypes'

const state = {
  incomeTransactions: []
}

const actions = {
  logout ({ commit }) {
    commit('logout')
  },
  loadIncomeTransactions ({ commit }) {
    IncomeTransactionsService.getAllIncomeTransactions().then(incomeTransactions => {
      commit('getAllIncomeTransactions', incomeTransactions)
    })
  },
  createIncomeTransaction ({ commit, dispatch }, incomeTransaction) {
    return IncomeTransactionsService.createIncomeTransaction(incomeTransaction).then(incomeTransaction => {
      commit('createIncomeTransaction', incomeTransaction)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Income Transaction added successfully', color: 'success' }, { root: true })
      dispatch(`statistics/${EDIT_STATISTICS}`, { income: incomeTransaction, operation: 'create' }, { root: true })
    })
  },
  modifyIncomeTransaction ({ commit, dispatch }, incomeTransaction) {
    return IncomeTransactionsService.modifyIncomeTransaction(incomeTransaction).then(incomeTransaction => {
      commit('modifyIncomeTransaction', incomeTransaction)
      dispatch(`alert/${ADD_ALERT}`, { message: 'Income Transaction updated successfully', color: 'success' }, { root: true })
      dispatch(`statistics/${EDIT_STATISTICS}`, { income: incomeTransaction, operation: 'edit' }, { root: true })
    })
  },
  deleteIncomeTransaction ({ commit, dispatch }, incomeTransactionId) {
    IncomeTransactionsService.deleteIncomeTransactionById(incomeTransactionId)
      .then(() => {
        const newIncomeTransactions = state.incomeTransactions.filter(et => et.incomeTransactionId === incomeTransactionId)[0]
        commit('deleteIncomeTransaction', incomeTransactionId)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Income Transaction deleted successfully', color: 'success' }, { root: true })
        dispatch(`statistics/${EDIT_STATISTICS}`, { income: newIncomeTransactions, operation: 'remove' }, { root: true })
      })
  },
  deleteIncomeTransactionsByCategory ({ commit, dispatch }, categoryName) {
    IncomeTransactionsService.deleteIncomeTransactionByCategory(categoryName)
      .then(() => {
        const newIncomeTransactions = state.incomeTransactions.filter(et => et.categoryName === categoryName)[0]
        commit('deleteAllIncomeTransactionByCategory', categoryName)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Income Transactions have been deleted successfully', color: 'success' }, { root: true })
        dispatch(`statistics/${EDIT_STATISTICS}`, { income: newIncomeTransactions, operation: 'remove' }, { root: true })
      })
  }
}

const mutations = {
  getAllIncomeTransactions (state, incomeTransactions) {
    state.incomeTransactions = incomeTransactions
  },
  createIncomeTransaction (state, incomeTransaction) {
    state.incomeTransactions.push(incomeTransaction)
  },
  modifyIncomeTransaction (state, incomeTransactionId, incomeTransaction) {
    const incomeTransactionUpdated = state.incomeTransactions.find(it => it.incomeTransactionId === incomeTransactionId)
    incomeTransactionUpdated.name = incomeTransaction.name
    incomeTransactionUpdated.description = incomeTransaction.description
    incomeTransactionUpdated.budget = incomeTransaction.budget
    incomeTransactionUpdated.colourHex = incomeTransaction.colourHex
  },
  deleteIncomeTransaction (state, incomeTransactionId) {
    state.incomeTransactions = state.incomeTransactions.filter(it => it.incomeTransactionId !== incomeTransactionId)
  },
  deleteAllIncomeTransactionByCategory (state, categoryName) {
    state.incomeTransactions = state.incomeTransactions.filter(it => it.categoryName !== categoryName)
  },
  logout (state) {
    state.incomeTransactions = []
  }
}

export const incomeTransactions = {
  namespaced: true,
  state,
  actions,
  mutations
}
