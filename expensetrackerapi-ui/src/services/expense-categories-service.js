import axios from 'axios'
import authHeader from '@/services/auth-header'

const API_URL = 'http://localhost:8080/'
class ExpenseCategoriesService {
  getAllExpenseCategories () {
    return axios.get(API_URL + 'api/expense/categories', {
      headers: {
        Authorization: authHeader()
      }
    })
  }

  createExpenseCategory (expenseCategory) {
    return axios.post(API_URL + 'api/add/expense/category', { params: { expenseCategory: expenseCategory }, headers: authHeader() })
  }

  modifyExpenseCategory (categoryName, modifiedCategory) {
    return axios.put(API_URL + 'api/modify/expense/category', { params: { categoryName: categoryName, modifiedCategory: modifiedCategory }, headers: authHeader() })
  }

  deleteExpenseCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/expense/category', { params: { categoryName: categoryName }, headers: authHeader() })
  }
}

export default new ExpenseCategoriesService()
