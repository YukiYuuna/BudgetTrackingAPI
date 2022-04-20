<template>
  <div>
    Username: {{username}}
    <div v-for="(item, index) in totalCategories" :key="index">
      <input v-model="item.categoryName">
      </div>
    <button v-on:click="getAllExpenseCategories">Show Expense Categories</button>
  </div>
</template>

<script>
import ExpenseTransactionsService from '../services/expense-transactions-service.js'

export default {
  name: 'Categories',
  data () {
    return {
      username: '',
      totalCategories: [
        {
          expenseCategoryId: 0,
          categoryName: ''
        }
      ]
    }
  },
  methods: {
    getAllExpenseCategories () {
      ExpenseTransactionsService.getAllExpenseCategories().then(
        (response) => {
          console.log(response)
          this.username = response.data.username
          this.totalCategories = response.data.totalCategories
        }
      )
    }
  }
}
</script>

<style scoped>

</style>
