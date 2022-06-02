import axios from 'axios'
import authHeader from '@/services/auth-header'

const API_URL = 'http://localhost:8080/'
const headers = {
  withCredentials: false,
  headers: {
    Authorization: authHeader(),
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*'
  }
}

class ExpenseCategoriesService {
  getAllExpenseCategories () {
    return axios.get(API_URL + 'api/expense/categories', headers
    ).then(response => {
      return response.data
    })
  }

  createExpenseCategory (expenseCategory) {
    const requestCategory = {
      categoryName: expenseCategory.expenseCategory.categoryName,
      color: expenseCategory.expenseCategory.color
    }

    return axios.post(API_URL + 'api/add/expense/category', requestCategory, headers
    ).then(response => {
      return response.data
    })
  }

  modifyExpenseCategory (modifiedCategory) {
    const requestCategory = {
      categoryName: modifiedCategory.expenseCategory.categoryName,
      color: modifiedCategory.expenseCategory.color
    }
    const expenseCategoryId = modifiedCategory.expenseCategory.expenseCategoryId

    return axios.put(API_URL + 'api/modify/expense/category/' + expenseCategoryId, requestCategory, headers
    ).then(response => {
      return response.data
    })
  }

  deleteExpenseCategory (expenseCategoryId) {
    const id = expenseCategoryId.expenseCategoryId

    return axios.delete(API_URL + 'api/delete/expense/category/' + id, headers
    ).then(response => {
      return response.data
    })
  }
}

export default new ExpenseCategoriesService()
