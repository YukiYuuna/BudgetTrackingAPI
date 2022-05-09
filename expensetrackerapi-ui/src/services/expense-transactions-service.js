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
    return axios.get(API_URL + 'api/expense/transactions',
      {
        params:
          {
            currentPage: currentPage,
            perPage: perPage
          },
        headers: {
          Authorization: authHeader()
        }
      }).then(response => {
      return response
    }, (error) => {
      console.log(error)
    })
  }

  getTransactionById (id) {
    return axios.get(API_URL + 'api/expense/transaction/' + id, { headers: authHeader() })
  }

  getTransactionsByDate (date, currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions/date', { params: { date: date, currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  getTransactionByCategory (category, currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions/category', { params: { category: category, currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  createExpenseTransaction (transaction) {
    const requestTransaction = {
      date: transaction.date,
      expenseAmount: Number(transaction.expenseAmount),
      categoryName: transaction.categoryName,
      description: transaction.description
    }

    axios.post(API_URL + 'api/add/expense/transaction', requestTransaction, headers
    ).then(function (response) {
      console.log(response)
    }).catch(function (error) {
      console.log(error)
    })
  }

  modifyExpenseTransaction (transactionId, modifiedTransaction) {
    return axios.put(API_URL + 'api/modify/expense/transaction/{transactionId}', { params: { transactionId: transactionId, modifiedTransaction: modifiedTransaction }, headers: authHeader() })
  }

  deleteExpenseTransactionByCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/expense/transactions/category', { params: { categoryName: categoryName }, headers: authHeader() })
  }

  deleteExpenseTransactions () {
    return axios.delete(API_URL + 'api/delete/expense/transactions', { headers: authHeader() })
  }

  deleteExpenseTransactionById (id) {
    return axios.delete(API_URL + 'api/delete/expense/transaction/' + id, { headers: authHeader() })
  }
}

export default new ExpenseTransactionsService()
