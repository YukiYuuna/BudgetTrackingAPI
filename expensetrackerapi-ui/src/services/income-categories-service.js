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

class IncomeCategoriesService {
  getAllIncomeCategories () {
    return axios.get(API_URL + 'api/income/categories', headers
    ).then(response => {
      return response.data
    })
  }

  createIncomeCategory (incomeCategory) {
    const requestCategory = {
      categoryName: incomeCategory.incomeCategory.categoryName
    }

    return axios.post(API_URL + 'api/add/income/category', requestCategory, headers
    ).then(response => {
      return response.data
    })
  }

  modifyIncomeCategory (modifiedCategory) {
    const requestCategory = {
      categoryName: modifiedCategory.incomeCategory.categoryName
    }
    const incomeCategoryId = modifiedCategory.incomeCategory.incomeCategoryId

    return axios.put(API_URL + 'api/modify/income/category' + incomeCategoryId, requestCategory, headers
    ).then(response => {
      return response.data
    })
  }

  deleteIncomeCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/income/category' + categoryName, headers)
  }
}

export default new IncomeCategoriesService()
