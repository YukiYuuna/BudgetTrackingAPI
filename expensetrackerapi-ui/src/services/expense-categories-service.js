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

class ExpenseCategoriesService {
  getAllExpenseCategories () {
    return axios.get(API_URL + 'api/expense/categories', {
      headers: {
        Authorization: authHeader()
      }
    })
  }

  createExpenseCategory (expenseCategory) {
    const requestCategory = {
      categoryName: expenseCategory.categoryName
    }

    axios.post(API_URL + 'api/add/expense/category', requestCategory, headers
    ).then(function (response) {
      console.log(response)
    }).catch(function (error) {
      console.log(error)
    })
  }

  modifyExpenseCategory (categoryName, modifiedCategory) {
    return axios.put(API_URL + 'api/modify/expense/category', { params: { categoryName: categoryName, modifiedCategory: modifiedCategory }, headers: authHeader() })
  }

  deleteExpenseCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/expense/category', { params: { categoryName: categoryName }, headers: authHeader() })
  }
}

export default new ExpenseCategoriesService()
