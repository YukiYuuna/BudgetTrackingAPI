import UserService from '../../services/user-service'
import AuthService from '../../services/auth-service'
import router from '@/router/index';
import {ADD_ALERT} from "@/store/_actiontypes";

const state = {
  user: {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    currentBudget: 0
  },
  currencies: []
};

const actions = {
  login ({ commit }, username, password) {
    return AuthService.login(username, password).then(
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
        commit(`alert/${ADD_ALERT}`, { message: 'User registered successfully', color: 'success' }, { root: true });
        router.push('/login');
      }
    )
  },
  modifyUserInfo ({ commit, dispatch }, user) {
    UserService.modifyUserInformation(user).then(
      user => {
        commit('modificationFinished', user)
        dispatch(`alert/${ADD_ALERT}`, { message: 'Profile updaded successfully', color: 'success' }, { root: true });
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
    state.status.loggedIn = true
    state.user = user
  },
  loginFailure (state) {
    state.status.loggedIn = false
    state.user = null
  },
  logout (state) {
    state.status.loggedIn = false
    state.user = null
  },
  registerSuccess (state) {
    state.status.loggedIn = false
  },
  registerFailure (state) {
    state.status.loggedIn = false
  },
  modificationFinished (state, user) {
    state.user.username = user.username
    state.user.firstName = user.firstName
    state.user.lastName = user.lastName
    state.user.email = user.email
    state.user.currentBudget = user.currentBudget
  }
};

const getters = {
    nameInitials: (state) => {
      const initials = state.user.firstName.match(/\b\w/g) || []
      return ((initials.shift() || "") + (initials.pop() || "")).toUpperCase()
    }
}

export const account = {
    namespaced: true,
    state,
    actions,
    mutations,
    getters
}
