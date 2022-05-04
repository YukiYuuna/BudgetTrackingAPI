import axios from 'axios'
import authHeader from '@/services/auth-header'

const API_URL = 'http://localhost:8080/'
class IncomeCategoriesService {

  getAllIncomeCategories () {
    return axios.get(API_URL + 'api/income/categories', { headers: authHeader() })
  }

  createIncomeCategory (incomeCategory) {
    return axios.post(API_URL + 'api/add/income/category', { params: { incomeCategory: incomeCategory }, headers: authHeader() })
  }

  modifyIncomeCategory (categoryName, modifiedCategory) {
    return axios.put(API_URL + 'api/modify/income/category', { params: { categoryName: categoryName, modifiedCategory: modifiedCategory }, headers: authHeader() })
  }

  deleteIncomeCategory (categoryName) {
    return axios.delete(API_URL + 'api/delete/income/category', { params: { categoryName: categoryName }, headers: authHeader() })
  }
}

export default new IncomeCategoriesService()
