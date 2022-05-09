import UserService from '../services/user-service'

export const userModule = {
  namespaced: true,
  state: {
    user: {
      username: '',
      firstName: '',
      lastName: '',
      email: '',
      currentBudget: 0
    }
  },
  mutations: {
    modificationFinished (state) {
      state.user.username = ''
      state.user.firstName = ''
      state.user.lastName = ''
      state.user.email = ''
      state.user.currentBudget = 0
    }
  },
  actions: {
    modifyUserInfo ({ commit }, user) {
      UserService.modifyUserInformation(user).then(
        user => {
          commit('modificationFinished')
          return Promise.resolve(user)
        },
        error => {
          commit('modificationFinished')
          return Promise.reject(error)
        }
      )
    }

  },
  getters: {}
}
