import {
  LOAD_CATEGORIES_BREAKDOWN,
  LOAD_EXPENSES_BREAKDOWN,
  EDIT_STATISTICS,
  CREATE_NEWCATEGORY_STATISTICS,
  EDIT_CATEGORY_STATISTICS,
  DELETE_CATEGORY_STATISTICS
} from '@/store/_actiontypes'
import {
  UPDATE_STATISTICS, ADD_NEWCATEGORY_STATISTICS, UPDATE_CATEGORY_STATISTICS, REMOVE_CATEGORY_STATISTICS
} from '@/store/_mutationtypes'
import sumBy from 'lodash/sumBy'
import groupBy from 'lodash/groupBy'
import forEach from 'lodash/forEach'
import uniq from 'lodash/uniq'
import ExpenseTransactionsService from '@/services/expense-transactions-service'
import UserService from '@/services/user-service'
import IncomeTransactionsService from '@/services/income-transactions-service'
import map from 'lodash/map'

const state = {
  user: {},
  expenseCategoriesBreakdown: [],
  incomeCategoriesBreakdown: [],
  expenseTransactionsBreakdown: [],
  incomeTransactionsBreakdown: []
}

const actions = {
  loadExpenseTransactionsForCurrentMonthByCategory ({ commit }) {
    const year = new Date().getFullYear()
    const month = new Date().getMonth() + 1
    ExpenseTransactionsService.getTransactionsForCurrentMonthByCategory(year, month).then(data => {
      commit('getAllExpenseCategoriesForCurrentMonth', data.categoriesInfo)
    })
    UserService.getUserInfo().then(user => { commit('userInfo', user) }
    )
  },
  loadIncomeTransactionsForCurrentMonthByCategory ({ commit }) {
    const year = new Date().getFullYear()
    const month = new Date().getMonth() + 1
    IncomeTransactionsService.getTransactionsForCurrentMonthByCategory(year, month).then(data => {
      commit('getAllIncomeCategoriesForCurrentMonth', data.categoriesInfo)
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
  },
  [EDIT_STATISTICS] ({ commit, dispatch }, { expense, operation }) {
    // when editing an expense (which is most likey not done often), reload the state
    if (operation === 'edit') {
      dispatch(LOAD_CATEGORIES_BREAKDOWN)
      dispatch(LOAD_EXPENSES_BREAKDOWN)
    } else {
      commit(UPDATE_STATISTICS, { expense, operation })
    }
  },
  [CREATE_NEWCATEGORY_STATISTICS] ({ commit }, { category }) {
    commit(ADD_NEWCATEGORY_STATISTICS, category)
  },
  [EDIT_CATEGORY_STATISTICS] ({ commit }, { category }) {
    commit(UPDATE_CATEGORY_STATISTICS, category)
  },
  [DELETE_CATEGORY_STATISTICS] ({ commit }, { categoryId }) {
    commit(REMOVE_CATEGORY_STATISTICS, categoryId)
  }
}

const mutations = {
  getAllExpenseCategoriesForCurrentMonth (state, expenseCategoriesInfo) {
    state.expenseCategoriesBreakdown = expenseCategoriesInfo
  },
  getAllIncomeCategoriesForCurrentMonth (state, incomeCategoriesInfo) {
    state.incomeCategoriesBreakdown = incomeCategoriesInfo
  },
  getAllExpenseTransactionsForCurrentMonth (state, expenseTransactionsBreakdown) {
    state.expenseTransactionsBreakdown = expenseTransactionsBreakdown
  },
  getAllIncomeTransactionsForCurrentMonth (state, incomeTransactionsBreakdown) {
    state.incomeTransactionsBreakdown = incomeTransactionsBreakdown
  },
  userInfo (state, user) {
    state.user = user
  },
  [UPDATE_STATISTICS] (state, payload) {
    const currentmonth = new Date().getMonth() + 1
    const expenseDate = new Date(payload.expense.date)
    const expenseMonth = expenseDate.getMonth() + 1

    // if the expense is for current month
    if (expenseMonth === currentmonth) {
      // check if there is a entry for the current month for the category and update it, if not create a new entry
      const currentmonthData = state.expenseCategoriesBreakdown.filter((o) => { return o.month === currentmonth })
      const category = currentmonthData.filter((o) => { return o.name === payload.expense.category })
      if (category[0]) {
        if (payload.operation === 'create') {
          category[0].spent += payload.expense.value
        } else {
          category[0].spent -= payload.expense.value
        }
      } else if (payload.operation === 'create') {
        state.expenseCategoriesBreakdown.push({
          budget: payload.expense.categoryBudget,
          colour: payload.expense.categoryColour,
          id: payload.expense.categoryId,
          month: currentmonth,
          name: payload.expense.category,
          spent: payload.expense.value

        })
      }
    }

    // check if there is a entry for the current category and month and update it, if not create a new entry
    const expensebreakdown = state.expenseTransactionsBreakdown.filter((o) => { return o.month === expenseMonth && o.categoryName === payload.expense.category })
    if (expensebreakdown[0]) {
      if (payload.operation === 'create') {
        expensebreakdown[0].spent += payload.expense.value
      } else {
        expensebreakdown[0].spent -= payload.expense.value
      }
    } else if (expenseDate.getYear() === new Date().getYear() && payload.operation === 'create') {
      state.expenseTransactionsBreakdown.push({
        categoryColour: payload.expense.categoryColour,
        categoryName: payload.expense.category,
        month: expenseMonth,
        spent: payload.expense.value

      })
    }
  },
  [ADD_NEWCATEGORY_STATISTICS] (state, category) {
    const months = uniq(state.expenseCategoriesBreakdown.map(c => c.month))
    forEach(months, (value, key) => {
      state.expenseCategoriesBreakdown.push({
        budget: category.budget,
        colour: category.colourHex,
        id: category.id,
        month: value,
        name: category.name,
        spent: 0

      })
    })
  },
  [UPDATE_CATEGORY_STATISTICS] (state, category) {
    const categories = state.expenseCategoriesBreakdown.filter((o) => { return o.id === category.id })
    forEach(categories, (value, key) => {
      value.name = category.name
      value.budget = category.budget
      value.colour = category.colourHex
    })

    const expenses = state.expenseTransactionsBreakdown.filter((o) => { return o.id === category.id })
    forEach(expenses, (value, key) => {
      value.categoryName = category.name
      value.categoryColour = category.colourHex
    })
  },
  [REMOVE_CATEGORY_STATISTICS] (state, categoryId) {
    state.expenseCategoriesBreakdown = state.expenseCategoriesBreakdown.filter((o) => { return o.id !== categoryId })
    state.expenseTransactionsBreakdown = state.expenseTransactionsBreakdown.filter((o) => { return o.id !== categoryId })
  }
}

const getters = {
  monthlyBudget: (state) => {
    const totalMade = state.incomeTransactionsBreakdown.totalAmount
    const totalSpent = state.expenseTransactionsBreakdown.totalAmount
    const totalBudget = state.user.currentBudget + totalMade
    const remaining = totalBudget - totalSpent

    return {
      data: [
        { value: totalSpent.toFixed(2), name: 'Spent', itemStyle: { color: '#2779bd' } },
        { value: (remaining < 0 ? 0 : remaining).toFixed(2), name: 'Remaining', itemStyle: { color: '#BDBDBD' } }
      ],
      totalBudget: new Intl.NumberFormat(window.navigator.language).format(totalBudget),
      totalSpent: new Intl.NumberFormat(window.navigator.language).format(totalSpent),
      totalMade: new Intl.NumberFormat(window.navigator.language).format(totalMade)
    }
  },
  monthlyBudgetsByCategory: state => {
    const totalBudget = state.user.currentBudget + state.incomeTransactionsBreakdown.totalAmount
    var currentMonthBudgets = state.expenseCategoriesBreakdown
    console.log(currentMonthBudgets)

    const perCategoryBudget = totalBudget / currentMonthBudgets.totalCategories
    console.log(perCategoryBudget)

    return map(currentMonthBudgets, function (value, key) {
      console.log(value)
      const remaining = perCategoryBudget - value.totalAmount
      return {
        name: key,
        colour: 'blue',
        monthlyBudget: [
          { value: value.totalAmount, name: 'Spent', itemStyle: { color: '#2196F3' } },
          { value: (remaining < 0 ? 0 : remaining).toFixed(2), name: 'Remaining', itemStyle: { color: '#BDBDBD' } }
        ]
      }
    })
  },
  yearlyExpenses: state => {
    const months = ['Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec']

    const groupedByMonths = groupBy(state.expenseTransactionsBreakdown, (e) => { return e.month })
    const yearlyExpenses = {
      xAxisData: [],
      data: []
    }

    forEach(groupedByMonths, (value, key) => {
      yearlyExpenses.xAxisData.push(months[Number(key) - 1])
      yearlyExpenses.data.push(sumBy(value, 'spent').toFixed(0))
    })
    return yearlyExpenses
  }
}

export const statistics = {
  namespaced: true,
  state,
  actions,
  mutations,
  getters
}
