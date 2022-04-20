import axios from 'axios'
import authHeader from '@/services/auth-header'

const API_URL = 'http://localhost:8080/'

class ExpenseTransactionsService {
  getAllTransactions (currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions', { params: { currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  getAllExpenseCategories () {
    return axios.get(API_URL + 'api/expense/categories', { headers: authHeader() })
  }

  getTransactionById () {
    return axios.get(API_URL + 'api/expense/transaction/{id}', { headers: authHeader() })
  }

  getTransactionsByDate (date, currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions/date', { params: { date: date, currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  getTransactionByCategory (category, currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions/category', { params: { category: category, currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  createCategory (expenseCategory) {
    return axios.post(API_URL + 'api/add/expense/category', { params: { expenseCategory: expenseCategory }, headers: authHeader() })
  }

  createTransaction (date, expenseAmount, categoryName, description) {
    return axios.post(API_URL + 'api/add/expense/transaction', { params: { date: date, expenseAmount: expenseAmount, categoryName: categoryName, description: description }, headers: authHeader() })
  }

  modifyCategory (categoryName, modifiedCategory) {
    return axios.put(API_URL + 'api/modify/expense/category', { params: { categoryName: categoryName, modifiedCategory: modifiedCategory }, headers: authHeader() })
  }

  modifyTransaction (transactionId, modifiedTransaction) {
    return axios.put(API_URL + 'api/modify/expense/transaction/{transactionId}', { params: { transactionId: transactionId, modifiedTransaction: modifiedTransaction }, headers: authHeader() })
  }

  deleteCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/expense/category', { params: { categoryName: categoryName }, headers: authHeader() })
  }

  deleteTransactionByCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/expense/transactions/category', { params: { categoryName: categoryName }, headers: authHeader() })
  }

  deleteTransactions () {
    return axios.delete(API_URL + 'api/delete/expense/transactions', { headers: authHeader() })
  }

  deleteTransactionById (id) {
    return axios.delete(API_URL + 'api/delete/expense/transaction/{id}', { params: { id: id }, headers: authHeader() })
  }
}

export default new ExpenseTransactionsService()
