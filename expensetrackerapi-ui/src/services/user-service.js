import axios from 'axios'
import authHeader from './auth-header'

const API_URL = 'http://localhost:8080/'

class UserService {
  getUserInfo () {
    return axios.get(API_URL + 'api/user', { headers: authHeader() })
  }

  getUserById (id) {
    return axios.get(API_URL + 'api/user/id', { params: { id: id }, headers: authHeader() })
  }

  getAllUsers () {
    return axios.get(API_URL + 'api/users', { headers: authHeader() })
  }

  createNewRole (roleName) {
    return axios.post(API_URL + 'api/user/save/role', { params: { roleName: roleName }, headers: authHeader() })
  }

  addRoleToUser (roleName) {
    return axios.post(API_URL + 'api/user/add/role', { params: { roleName: roleName }, headers: authHeader() })
  }

  modifyUserInformation (user) {
    return axios.put(API_URL + 'api/user/modify', { params: { user: user }, headers: authHeader() })
  }

  deleteUserFromApp () {
    return axios.delete(API_URL + 'api/user/delete', { headers: authHeader() })
  }
}

export default new UserService()
