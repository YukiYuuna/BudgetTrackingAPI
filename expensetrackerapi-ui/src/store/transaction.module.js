import IncomeTransactionsService from '../services/income-transactions-service'
import ExpenseTransactionsService from '../services/expense-transactions-service'

export const allTransactions = {
  namespaced: true,
  state: {
    expenseTransaction: {
      date: '',
      expenseAmount: 0,
      categoryName: '',
      description: ''
    },
    incomeTransaction: {
      date: '',
      incomeAmount: 0,
      categoryName: '',
      description: ''
    }
  },
  mutations: {
    expenseCreationSucceeded (state) {
      state.expenseTransaction.date = ''
      state.expenseTransaction.expenseAmount = 0
      state.expenseTransaction.categoryName = ''
      state.expenseTransaction.description = ''
    },
    expenseCreationFailed (state) {
      state.expenseTransaction.date = ''
      state.expenseTransaction.expenseAmount = 0
      state.expenseTransaction.categoryName = ''
      state.expenseTransaction.description = ''
    },
    incomeCreationSucceeded (state) {
      state.incomeTransaction.date = ''
      state.incomeTransaction.incomeAmount = 0
      state.incomeTransaction.categoryName = ''
      state.incomeTransaction.description = ''
    },
    incomeCreationFailed (state) {
      state.incomeTransaction.date = ''
      state.incomeTransaction.incomeAmount = 0
      state.incomeTransaction.categoryName = ''
      state.incomeTransaction.description = ''
    }
  },
  actions: {
    createExpenseTransaction ({ commit }, expenseTransaction) {
      ExpenseTransactionsService.createExpenseTransaction(expenseTransaction, () =>
        expenseTransaction => {
          commit('expenseCreationSucceeded')
          return Promise.resolve(expenseTransaction)
        },
      error => {
        commit('expenseCreationFailed')
        return Promise.reject(error)
      }
      )
    },
    createIncomeTransaction ({ commit }, incomeTransaction) {
      IncomeTransactionsService.createIncomeTransaction(incomeTransaction, () =>
        incomeTransaction => {
          commit('incomeCreationSucceeded')
          return Promise.resolve(incomeTransaction)
        },
      error => {
        commit('incomeCreationFailed')
        return Promise.reject(error)
      }
      )
    }
  },
  getters: {}
}
