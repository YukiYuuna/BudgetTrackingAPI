import axios from 'axios'
import authHeader from "@/services/auth-header";

const API_URL = 'http://localhost:8080/'

class IncomeTransactionsService {
  getAllTransactions(currentPage, perPage) {
    return axios.get(API_URL + 'api/income/transactions', { params: { currentPage: currentPage, perPage: perPage }, headers: authHeader() })
  }

  getAllCategories () {
    return axios.get(API_URL + 'api/income/categories', {headers: authHeader()})
  }
  //todo
  getTransactionById () {
    return axios.get(API_URL + 'api/income/transaction/{id}', {headers: authHeader()})
  }

  getTransactionsByDate (date, currentPage, perPage) {
    return axios.get(API_URL + 'api/income/transactions/date', { params: { date: date, currentPage: currentPage, perPage: perPage }, headers: authHeader()})
  }

  getTransactionByCategory (category, currentPage, perPage) {
    return axios.get(API_URL + 'api/income/transactions/category', { params: { category: category, currentPage: currentPage, perPage: perPage }, headers: authHeader()})
  }

  createCategory (incomeCategory) {
    return axios.post(API_URL + 'api/add/income/category', { params: { incomeCategory: incomeCategory }, headers: authHeader()})
  }

  createTransaction (date, incomeAmount, categoryName, description) {
    return axios.post(API_URL + 'api/add/income/transaction', { params: { date: date, incomeAmount: incomeAmount, categoryName: categoryName, description: description }, headers: authHeader()})
  }

  modifyCategory (categoryName, modifiedCategory){
    return axios.put(API_URL + 'api/modify/income/category', { params: { categoryName: categoryName, modifiedCategory: modifiedCategory }, headers: authHeader()})
  }
  //todo
  modifyTransaction (transactionId, modifiedTransaction){
    return axios.put(API_URL + 'api/modify/income/transaction/{transactionId}', { params: { transactionId: transactionId, modifiedTransaction: modifiedTransaction }, headers: authHeader()})
  }

  deleteCategory (categoryName){
    return axios.delete(API_URL + 'api/delete/income/category', { params: { categoryName: categoryName }, headers: authHeader()})
  }

  deleteTransactionByCategory (categoryName){
    return axios.delete(API_URL + 'api/delete/income/transactions/category', { params: { categoryName: categoryName }, headers: authHeader()})
  }

  deleteTransactions (){
    return axios.delete(API_URL + 'api/delete/income/transactions', {headers: authHeader()})
  }
  //todo
  deleteTransactionById (id){
    return axios.delete(API_URL + 'api/delete/income/transaction/{id}', { params: { id: id }, headers: authHeader()})
  }
}

export default new ExpenseTransactionsService()
