import AuthService from '../services/auth-service'
import ExpenseTransactionsService from '../services/expense-transactions-service'
import IncomeTransactionsService from '../services/income-transactions-service'
const user = JSON.parse(localStorage.getItem('user'))

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
      state.transaction = null
    },
    creationFailed (state) {
      state.transaction = null
    }

  },
  actions: {
    createTransaction ({ commit }, transaction) {
      return ExpenseTransactionsService.createExpenseTransaction(
        transaction.date,
        transaction.expenseAmount,
        transaction.categoryName,
        transaction.description).then(
          response => {
            commit('creationSucceeded')
            return Promise.resolve(response.data)
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
