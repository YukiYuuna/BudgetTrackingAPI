import axios from 'axios'

const API_URL = 'http://localhost:8080/'

class AuthService {
  login (username, password) {
    return axios
      .get(API_URL + 'api/login', {
        params: {
          username: username,
          password: password
        }
      })
      .then(response => {
        localStorage.setItem('user', JSON.stringify(response.data))
        console.log(response)
        return response.data
      }, (error) => {
        console.log(error)
      })
  }

  logout () {
    localStorage.removeItem('user')
  }

  register (user) {
    return axios.post(API_URL + 'api/register', {
      username: user.username,
      password: user.password,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      currentBudget: user.currentBudget
    })
  }
}

export default new AuthService()
