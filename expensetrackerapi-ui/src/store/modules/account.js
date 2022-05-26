import UserService from '../../services/user-service'
import AuthService from '../../services/auth-service'
import router from '@/router/index'
import { ADD_ALERT } from '@/store/_actiontypes'

const state = {
  user: {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    currentBudget: 0
  },
  currencies: []
}

const actions = {
  login ({ commit }, loginForm) {
    return AuthService.login(loginForm).then(
      user => {
        commit('loginSuccess', user)
      })
      .catch(() => {
        commit('loginFailure')
      }
      )
  },
  logout ({ commit }) {
    AuthService.logout()
    commit('logout')
  },
  register ({ commit }, user) {
    return AuthService.register(user).then(
      () => {
        commit(`alert/${ADD_ALERT}`, { message: 'User registered successfully', color: 'success' }, { root: true })
        router.push('/login')
      }
    )
  },
  modifyUserInfo ({ commit, dispatch }, user) {
    UserService.modifyUserInformation(user).then(
      user => {
        commit('modificationFinished', user)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Profile updated successfully', color: 'success' }, { root: true })
      },
      error => {
        commit('modificationFinished')
        return Promise.reject(error)
      }
    )
  },
  getUserInfo ({ commit, dispatch }) {
    UserService.getUserInfo().then(
      user => {
        commit('modificationFinished', user)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Profile extracted successfully', color: 'success' }, { root: true })
      },
      error => {
        commit('modificationFinished')
        return Promise.reject(error)
      }
    )
  }
}

const mutations = {
  loginSuccess (state, user) {
    state.user = user
  },
  loginFailure (state) {
    state.user = {}
  },
  logout (state) {
    state.user = {}
  },
  modificationFinished (state, user) {
    state.user.username = user.username
    state.user.firstName = user.firstName
    state.user.lastName = user.lastName
    state.user.email = user.email
    state.user.currentBudget = user.currentBudget
  }
}

const getters = {
  nameInitials: (state) => {
    const initials = (state.user.firstName + ' ' + state.user.lastName).match(/\b\w/g) || []
    return ((initials.shift() || '') + (initials.pop() || '')).toUpperCase()
  }
}

export const account = {
  namespaced: true,
  state,
  actions,
  mutations,
  getters
}
