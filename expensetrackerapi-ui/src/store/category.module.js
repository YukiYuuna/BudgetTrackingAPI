import ExpenseCategoriesService from '@/services/expense-categories-service'

export const categories = {
  namespaced: true,
  state: {
    categories: {
      categoryName: ''
    }
  },
  mutations: {
    creationSucceeded (state) {
      state.categories.categoryName = ''
    },
    creationFailed (state) {
      state.categories.categoryName = ''
    }
  },
  actions: {
    createExpenseCategory ({ commit }, category) {
      ExpenseCategoriesService.createExpenseCategory(category).then(
        category => {
          commit('creationSucceeded')
          return Promise.resolve(category)
        },
        error => {
          commit('creationFailed')
          return Promise.reject(error)
        }
      )
    }

  },
  getters: {}
}
