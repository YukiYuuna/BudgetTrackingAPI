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

class IncomeTransactionsService {
  getAllIncomeTransactions (currentPage, perPage) {
    return axios.get(API_URL + 'api/income/transactions', headers
    ).then(response => {
      return response.data.transactions
    })
  }

  getIncomeTransactionById (id) {
    return axios.get(API_URL + 'api/income/transaction/' + id, headers
    ).then(response => {
      return response.data
    })
  }

  getIncomeTransactionsByDate (date, currentPage, perPage) {
    return axios.get(API_URL + 'api/income/transactions/date' + date, headers
    ).then(response => {
      return response.data
    })
  }

  getTransactionsForCurrentMonth (year, month) {
    return axios.get(API_URL + 'api/income/transactions/current/' + year + '/' + month, headers
    ).then(response => {
      return response.data
    })
  }

  getTransactionsForCurrentMonthByCategory (year, month) {
    return axios.get(API_URL + 'api/income/transactions/year/' + year + '/' + month, headers
    ).then(response => {
      return response.data
    })
  }

  getIncomeTransactionByCategory (category, currentPage, perPage) {
    return axios.get(API_URL + 'api/income/transactions/category' + category, headers
    ).then(response => {
      return response.data
    })
  }

  createIncomeTransaction (transaction) {
    const requestTransaction = {
      date: transaction.incomeTransaction.date,
      incomeAmount: Number(transaction.incomeTransaction.incomeAmount),
      categoryName: transaction.incomeTransaction.categoryName,
      description: transaction.incomeTransaction.description
    }

    return axios.post(API_URL + 'api/add/income/transaction', requestTransaction, headers)
      .then(response => {
        return response.data
      })
  }

  modifyIncomeTransaction (transaction) {
    const requestTransaction = {
      date: transaction.incomeTransaction.date,
      incomeAmount: Number(transaction.incomeTransaction.incomeAmount),
      categoryName: transaction.incomeTransaction.categoryName,
      description: transaction.incomeTransaction.description
    }

    const incomeTransactionId = transaction.incomeTransaction.incomeTransactionId

    return axios.put(API_URL + 'api/modify/income/transaction/' + incomeTransactionId, requestTransaction, headers)
      .then(response => {
        return response.data
      })
  }

  deleteIncomeTransactionByCategory (categoryName) {
    const incomeCategoryName = categoryName.categoryName

    return axios.delete(API_URL + 'api/delete/income/transactions/category' + incomeCategoryName, headers)
  }

  deleteIncomeTransactionById (id) {
    const incomeTransactionId = id.incomeTransactionId

    return axios.delete(API_URL + 'api/delete/income/transaction/' + incomeTransactionId, headers).then(response => {
      return response.data
    })
  }
}

export default new IncomeTransactionsService()
