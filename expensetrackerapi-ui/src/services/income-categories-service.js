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

class IncomeCategoriesService {
  getAllIncomeCategories () {
    return axios.get(API_URL + 'api/income/categories', {
      headers: {
        Authorization: authHeader()
      }
    })
  }

  createIncomeCategory (incomeCategory) {
    const requestCategory = {
      categoryName: incomeCategory.categoryName
    }

    axios.post(API_URL + 'api/add/income/category', requestCategory, headers
    ).then(function (response) {
      console.log(response)
    }).catch(function (error) {
      console.log(error)
    })
  }

  modifyIncomeCategory (categoryName, modifiedCategory) {
    return axios.put(API_URL + 'api/modify/income/category', { params: { categoryName: categoryName, modifiedCategory: modifiedCategory }, headers: authHeader() })
  }

  deleteIncomeCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/income/category', { params: { categoryName: categoryName }, headers: authHeader() })
  }
}

export default new IncomeCategoriesService()
