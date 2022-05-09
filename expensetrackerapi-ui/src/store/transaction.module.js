import ExpenseTransactionsService from '../services/expense-transactions-service'

export const transactions = {
  namespaced: true,
  state: {
    transaction: {
      date: '',
      expenseAmount: 0,
      incomeAmount: 0,
      categoryName: '',
      description: ''
    }
  },
  mutations: {
    creationSucceeded (state) {
      state.transaction.date = ''
      state.transaction.expenseAmount = 0
      state.transaction.incomeAmount = 0
      state.transaction.categoryName = ''
      state.transaction.description = ''
    },
    creationFailed (state) {
      state.transaction.date = ''
      state.transaction.expenseAmount = 0
      state.transaction.incomeAmount = 0
      state.transaction.categoryName = ''
      state.transaction.description = ''
    }

  },
  actions: {
    createTransaction ({ commit }, transaction) {
      ExpenseTransactionsService.createExpenseTransaction(transaction, () =>
        transaction => {
          commit('creationSucceeded')
          return Promise.resolve(transaction)
        },
      error => {
        commit('creationFailed')
        return Promise.reject(error)
      }
      )
    }

  },
  getters: {}
}
