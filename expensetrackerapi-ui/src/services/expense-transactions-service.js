import axios from 'axios'
import authHeader from '@/services/auth-header'

const API_URL = 'http://localhost:8080/'
var headers = {
  withCredentials: false,
  headers: {
    Authorization: authHeader(),
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*'
  }
}

class ExpenseTransactionsService {
  getAllExpenseTransactions (currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions', headers
    ).then(response => {
      return response.data
    })
  }

  getTransactionById (id) {
    return axios.get(API_URL + 'api/expense/transaction/' + id, headers
    ).then(response => {
      return response.data
    })
  }

  getTransactionsByDate (date) {
    return axios.get(API_URL + 'api/expense/transactions/date?date=' + date, headers
    ).then(response => {
      return response.data
    })
  }

  getTransactionByCategory (category) {
    return axios.get(API_URL + 'api/expense/transactions/category?category' + category, headers
    ).then(response => {
      return response.data
    })
  }

  createExpenseTransaction (transaction) {
    const requestTransaction = {
      date: transaction.date,
      expenseAmount: Number(transaction.expenseAmount),
      categoryName: transaction.categoryName,
      description: transaction.description
    }

    return axios.post(API_URL + 'api/add/expense/transaction', requestTransaction, headers)
      .then(response => {
      return response.data
    })
  }

  modifyExpenseTransaction (id, modifiedTransaction) {
    const requestTransaction = {
      date: modifiedTransaction.date,
      expenseAmount: Number(modifiedTransaction.expenseAmount),
      categoryName: modifiedTransaction.categoryName,
      description: modifiedTransaction.description
    }
    return axios.put(API_URL + 'api/modify/expense/transaction/' + id, requestTransaction, headers)
      .then(response => {
      return response.data
    })
  }

  deleteExpenseTransactionByCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/expense/transactions/category?categoryName=' + categoryName, headers)
  }

  deleteExpenseTransactionById (id) {
    return axios.delete(API_URL + 'api/delete/expense/transaction/' + id, headers)
  }
}

export default new ExpenseTransactionsService()
