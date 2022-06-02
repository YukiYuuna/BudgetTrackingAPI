import ExpenseTransactionsService from '@/services/expense-transactions-service'
import BudgetService from '@/services/budget-service'
import UserService from '@/services/user-service'
import IncomeTransactionsService from '@/services/income-transactions-service'
import map from 'lodash/map'

const state = {
  user: {},
  expenseCategoriesBreakdown: [],
  incomeCategoriesBreakdown: [],
  expenseTransactionsBreakdown: [],
  incomeTransactionsBreakdown: [],
  totalExpenseCategories: 0,
  totalIncomeCategories: 0,
  userCurBudget: 0,
  totalIncomeAmount: 0,
  totalExpenseAmount: 0
}

const actions = {
  loadCurrentBudget ({ commit }) {
    UserService.getUserInfo().then(user => {
      commit('userInfo', user)
    })
    BudgetService.getUserCurrentBudget().then(data => {
      commit('setBudgetStats', data)
    })
  },
  loadExpenseTransactionsForCurrentMonthByCategory ({ commit }) {
    const year = new Date().getFullYear()
    const month = new Date().getMonth() + 1
    ExpenseTransactionsService.getTransactionsForCurrentMonthByCategory(year, month).then(data => {
      commit('getAllExpenseCategoriesForCurrentMonth', data)
    })
    UserService.getUserInfo().then(user => { commit('userInfo', user) }
    )
  },
  loadIncomeTransactionsForCurrentMonthByCategory ({ commit }) {
    const year = new Date().getFullYear()
    const month = new Date().getMonth() + 1
    IncomeTransactionsService.getTransactionsForCurrentMonthByCategory(year, month).then(data => {
      commit('getAllIncomeCategoriesForCurrentMonth', data)
    })
    UserService.getUserInfo().then(user => { commit('userInfo', user) }
    )
  },
  loadExpenseTransactionsForCurrentMonth ({ commit }) {
    const year = new Date().getFullYear()
    const month = new Date().getMonth() + 1
    ExpenseTransactionsService.getTransactionsForCurrentMonth(year, month).then(data => {
      commit('getAllExpenseTransactionsForCurrentMonth', data)
    })
    UserService.getUserInfo().then(user => { commit('userInfo', user) }
    )
  },
  loadIncomeTransactionsForCurrentMonth ({ commit }) {
    const year = new Date().getFullYear()
    const month = new Date().getMonth() + 1
    IncomeTransactionsService.getTransactionsForCurrentMonth(year, month).then(data => {
      commit('getAllIncomeTransactionsForCurrentMonth', data)
    })
    UserService.getUserInfo().then(user => { commit('userInfo', user) }
    )
  }
}

const mutations = {
  setBudgetStats (state, data) {
    state.userCurBudget = data.currentBudget
    state.totalIncomeAmount = data.made
    state.totalExpenseAmount = data.spent
  },
  getAllExpenseCategoriesForCurrentMonth (state, data) {
    state.expenseCategoriesBreakdown = data.categoriesInfo
    state.totalExpenseCategories = data.totalCategories
  },
  getAllIncomeCategoriesForCurrentMonth (state, data) {
    state.incomeCategoriesBreakdown = data.categoriesInfo
    state.totalIncomeCategories = data.totalCategories
  },
  getAllExpenseTransactionsForCurrentMonth (state, expenseTransactionsBreakdown) {
    state.expenseTransactionsBreakdown = expenseTransactionsBreakdown
  },
  getAllIncomeTransactionsForCurrentMonth (state, incomeTransactionsBreakdown) {
    state.incomeTransactionsBreakdown = incomeTransactionsBreakdown
  },
  userInfo (state, user) {
    state.user = user
  }
}

const getters = {
  monthlyBudget: (state) => {
    const totalMade = state.totalIncomeAmount
    const totalSpent = state.totalExpenseAmount
    const totalBudget = state.user.currentBudget + totalMade - totalSpent

    return {
      data: [
        { value: totalSpent.toFixed(2), name: 'Spent', itemStyle: { color: '#2779bd' } },
        { value: (totalBudget < 0 ? 0 : totalBudget).toFixed(2), name: 'Remaining', itemStyle: { color: '#BDBDBD' } }
      ],
      totalBudget: new Intl.NumberFormat(window.navigator.language).format(state.user.currentBudget + totalMade),
      totalSpent: new Intl.NumberFormat(window.navigator.language).format(totalSpent),
      totalMade: new Intl.NumberFormat(window.navigator.language).format(totalMade)
    }
  },
  monthlyBudgetsByCategory: state => {
    const currentMonthBudgets = state.expenseCategoriesBreakdown
    const perCategoryBudget = state.userCurBudget / state.totalExpenseCategories

    return map(currentMonthBudgets, function (value, key) {
      const remaining = perCategoryBudget - value.totalAmount
      return {
        name: key,
        color: value.categoryColor,
        monthlyBudget: [
          { value: value.totalAmount, name: 'Spent', itemStyle: { color: value.categoryColor } },
          { value: (remaining < 0 ? 0 : remaining).toFixed(2), name: 'Remaining', itemStyle: { color: '#BDBDBD' } }
        ]
      }
    })
  }
}

export const statistics = {
  namespaced: true,
  state,
  actions,
  mutations,
  getters
}
