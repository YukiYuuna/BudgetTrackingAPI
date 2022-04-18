import axios from 'axios'
import authHeader from "@/services/auth-header";

const API_URL = 'http://localhost:8080/'

class ExpenseTransactionsService {
  getAllExpenseTransactions(currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions', { params: { currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  getAllExpenseCategories () {
    return axios.get(API_URL + 'api/expense/categories', {headers: authHeader()})
  }
  //todo
  getExpenseTransactionById () {
    return axios.get(API_URL + 'api/expense/transaction/{id}', {headers: authHeader()})
  }

  getExpenseTransactionsByDate (date, currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions/date', { params: { date: date, currentPage: currentPage, perPage: perPage }, headers: authHeader()})
  }

  getExpenseTransactionByCategory (category, currentPage, perPage) {
    return axios.get(API_URL + 'api/expense/transactions/category', { params: { category: category, currentPage: currentPage, perPage: perPage }, headers: authHeader()})
  }

  createExpenseCategory (expenseCategory) {
    return axios.post(API_URL + 'api/add/expense/category', { params: { expenseCategory: expenseCategory }, headers: authHeader()})
  }

  createExpenseTransaction (date, expenseAmount, categoryName, description) {
    return axios.post(API_URL + 'api/add/expense/transaction', { params: { date: date, expenseAmount: expenseAmount, categoryName: categoryName, description: description }, headers: authHeader()})
  }

  modifyExpenseCategory (categoryName, modifiedCategory){
    return axios.put(API_URL + 'api/modify/expense/category', { params: { categoryName: categoryName, modifiedCategory: modifiedCategory }, headers: authHeader()})
  }
  //todo
  modifyExpenseTransaction (transactionId, modifiedTransaction){
    return axios.put(API_URL + 'api/modify/expense/transaction/{transactionId}', { params: { transactionId: transactionId, modifiedTransaction: modifiedTransaction }, headers: authHeader()})
  }

  deleteExpenseCategory (categoryName){
    return axios.delete(API_URL + 'api/delete/expense/category', { params: { categoryName: categoryName }, headers: authHeader()})
  }

  deleteExpenseTransactionByCategory (categoryName){
    return axios.delete(API_URL + 'api/delete/expense/transactions/category', { params: { categoryName: categoryName }, headers: authHeader()})
  }

  deleteExpenseTransactions (){
    return axios.delete(API_URL + 'api/delete/expense/transactions', {headers: authHeader()})
  }
  //todo
  deleteExpenseTransactionById (id){
    return axios.delete(API_URL + 'api/delete/expense/transaction/{id}', { params: { id: id }, headers: authHeader()})
  }
}

export default new ExpenseTransactionsService()
