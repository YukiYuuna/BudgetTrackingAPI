import ExpenseTransactionsService from '../../services/expense-transactions-service'
import { LOAD_EXPENSES, CREATE_EXPENSE, EDIT_EXPENSE, REMOVE_EXPENSE, ADD_ALERT, EDIT_STATISTICS, REMOVE_EXPENSESOFTYPE, REMOVE_EXPENSESOFCATEGORY } from '@/store/_actiontypes'
import { SET_EXPENSES, ADD_EXPENSE, UPDATE_EXPENSE, DELETE_EXPENSE, DELETE_EXPENSESOFTYPE, DELETE_EXPENSESOFCATEGORY } from '@/store/_mutationtypes'
import sumBy from 'lodash/sumBy'
import groupBy from 'lodash/groupBy'
import map from 'lodash/map'
import orderBy from 'lodash/orderBy'


const state = {
  expenseTransactions: {
    expenseTransactionId: 0,
    date: '',
    expenseAmount: 0,
    categoryName: '',
    description: ''
  }
}

const actions = {
  loadExpenseTransactions ({ commit }) {
    ExpenseTransactionsService.getAllExpenseTransactions().then(expenseTransactions => {
      commit('getAllExpenseTransactions', expenseTransactions)
    })
  },
  createExpenseTransaction ({ commit, dispatch }, expenseTransaction) {
    return ExpenseTransactionsService.createExpenseTransaction(expenseTransaction).then(expenseTransaction => {
      commit('createExpenseTransaction', expenseTransaction);
      dispatch(`alert/${ADD_ALERT}`, { message: 'Expense Transaction added successfully', color: 'success' }, { root: true });
      // TODO: why expense: expenseTransaction ??
      dispatch(`statistics/${EDIT_STATISTICS}`, { expense: expenseTransaction, operation: 'create' }, { root: true });
    })
  },
  modifyExpenseTransaction ({ commit, dispatch }, id, expenseTransaction) {
    return ExpenseTransactionsService.modifyExpenseTransaction(id, expenseTransaction).then(expenseTransaction => {
      commit('modifyExpenseTransaction', expenseTransaction);
      dispatch(`alert/${ADD_ALERT}`, { message: 'Expense Transaction updated successfully', color: 'success' }, { root: true });
      dispatch(`statistics/${EDIT_STATISTICS}`, { expense: expenseTransaction, operation: 'edit' }, { root: true });
    })
  },
  deleteExpenseTransaction ({ commit, dispatch }, expenseTransactionId) {
    ExpenseTransactionsService.deleteExpenseTransactionById(expenseTransactionId)
      .then(() => {
        var newExpenseTransactions = state.expenseTransactions.filter(et => et.expenseTransactionId === expenseTransactionId)[0];
        commit('deleteExpenseTransaction', id);
        dispatch(`alert/${ADD_ALERT}`, { message: 'Expense Transaction deleted successfully', color: 'success' }, { root: true });
        dispatch(`statistics/${EDIT_STATISTICS}`, { expense: newExpenseTransactions, operation: 'remove' }, { root: true });
      })
  },
  deleteExpenseTransactionsByCategory ({ commit, dispatch }, categoryName) {
    ExpenseTransactionsService.deleteExpenseTransactionByCategory(categoryName)
      .then(() => {
        var newExpenseTransactions = state.expenseTransactions.filter(et => et.categoryName === categoryName)[0];
        commit('deleteAllExpenseTransactionByCategory', categoryName);
        dispatch(`alert/${ADD_ALERT}`, { message: 'Expense Transactions have been deleted successfully', color: 'success' }, { root: true });
        dispatch(`statistics/${EDIT_STATISTICS}`, { expense: newExpenseTransactions, operation: 'remove' }, { root: true });
      })
  }
}

const mutations = {
  getAllExpenseTransactions (state, expenseTransactions) {
    state.expenseTransactions = expenseTransactions;
  },
  createExpenseTransaction (state, expenseTransaction) {
    state.expenseTransactions.push(expenseTransaction)
  },
  modifyExpenseTransaction (state, expenseTransactionId, expenseTransaction) {
    let expenseTransactionUpdated = state.expenseTransactions.find(et => et.expenseTransactionId === expenseTransactionId)
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

const getters = {
  overallSpent: state => {
    const overallSpent = new Intl.NumberFormat(window.navigator.language).format(sumBy(state.expenseTransactions, "value").toFixed(2));
    return `BGN ${overallSpent}`
  },

  mostSpentBy: state => {
    return state.expenseTransactions.length <= 0 ? 'N/A' : orderBy(
      map(
        groupBy(state.expenseTransactions, (e) => {
          return e.type
        }), (type, id) => ({
          type: id,
          value: sumBy(type, 'value')
        })), ['value'], ['desc'])[0].type;
  },
  mostSpentOn: state => {
    return state.expenseTransactions.length <= 0 ? 'N/A' : orderBy(
      map(
        groupBy(state.expenseTransactions, (e) => {
          return e.category
        }), (category, id) => ({
          category: id,
          value: sumBy(category, 'value')
        })), ['value'], ['desc'])[0].category;
  },
  spentThisYear: state => {
    const currentYear = new Date().getFullYear();
    const spentThisYear = new Intl.NumberFormat(window.navigator.language).format(sumBy(state.expenseTransactions.filter((o) => {
      return new Date(o.date).getFullYear() === currentYear;
    }), "value").toFixed(2));
    return `BGN ${spentThisYear}`
  }
}

export const expenseTransactions = {
  namespaced: true,
  state,
  actions,
  mutations
};
