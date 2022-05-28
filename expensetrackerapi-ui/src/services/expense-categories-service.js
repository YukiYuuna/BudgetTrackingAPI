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
      console.log(response.data.totalCategories)
      return response.data
    })
  }

  createExpenseCategory (expenseCategory) {
    const requestCategory = {
      categoryName: expenseCategory.categoryName
    }

    return axios.post(API_URL + 'api/add/expense/category', requestCategory, headers
    ).then(response => {
      return response.data
    })
  }

  modifyExpenseCategory (modifiedCategory) {
    const requestCategory = {
      categoryName: modifiedCategory.categoryName
    }
    const expenseCategoryId = modifiedCategory.expenseCategoryId
    console.log(expenseCategoryId)

    return axios.put(API_URL + 'api/modify/expense/category/' + expenseCategoryId, requestCategory, headers
    ).then(response => {
      return response.data
    })
  }

  deleteExpenseCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/expense/category' + categoryName, headers)
  }
}

export default new ExpenseCategoriesService()
