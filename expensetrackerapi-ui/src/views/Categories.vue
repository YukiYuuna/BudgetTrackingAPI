<template>
  <div>
    <b-table striped hover :items="totalCategories"></b-table>
    <button v-on:click="getAllExpenseCategories">Load Expense Categories</button>
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
