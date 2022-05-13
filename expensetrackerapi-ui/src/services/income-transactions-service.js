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
    return axios.get(API_URL + 'api/income/transactions', {
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

  getIncomeTransactionById (id) {
    return axios.get(API_URL + 'api/income/transaction/' + id, { headers: authHeader() })
  }

  getIncomeTransactionsByDate (date, currentPage, perPage) {
    return axios.get(API_URL + 'api/income/transactions/date', { params: { date: date, currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  getIncomeTransactionByCategory (category, currentPage, perPage) {
    return axios.get(API_URL + 'api/income/transactions/category', { params: { category: category, currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  createIncomeTransaction (transaction) {
    const requestTransaction = {
      date: transaction.date,
      incomeAmount: Number(transaction.incomeAmount),
      categoryName: transaction.categoryName,
      description: transaction.description
    }

    axios.post(API_URL + 'api/add/income/transaction', requestTransaction, headers
    ).then(function (response) {
      console.log(response)
    }).catch(function (error) {
      console.log(error)
    })
  }

  modifyIncomeTransaction (transactionId, modifiedTransaction) {
    return axios.put(API_URL + 'api/modify/income/transaction/{transactionId}', { params: { transactionId: transactionId, modifiedTransaction: modifiedTransaction }, headers: authHeader() })
  }

  deleteIncomeTransactionByCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/income/transactions/category', { params: { categoryName: categoryName }, headers: authHeader() })
  }

  deleteIncomeTransactions () {
    return axios.delete(API_URL + 'api/delete/income/transactions', { headers: authHeader() })
  }

  deleteIncomeTransactionById (id) {
    return axios.delete(API_URL + 'api/delete/income/transaction/' + id, { headers: authHeader() })
  }
}

export default new IncomeTransactionsService()
