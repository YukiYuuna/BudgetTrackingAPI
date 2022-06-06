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

class BudgetService {
  getUserCurrentBudget (currentPage, perPage) {
    return axios.get(API_URL + 'api/user/current/budget', headers
    ).then(response => {
      return response.data
    })
  }
}

export default new BudgetService()
