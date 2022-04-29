<template>
  <div>
    <b-table striped hover :items="transactions"></b-table>
    <button v-on:click="getAllExpenseTransactions">Load Expense Transactions</button>
  </div>
</template>

<script>
import ExpenseTransactionsService from '../services/expense-transactions-service.js'

export default {
  name: 'Transactions',
  data () {
    return {
      username: '',
      transactions: [
        {
          date: '',
          expenseAmount: '',
          categoryName: '',
          description: ''
        }
      ]
    }
  },
  methods: {
    getAllExpenseTransactions () {
      ExpenseTransactionsService.getAllExpenseTransactions().then(
        (response) => {
          console.log(response)
          this.username = response.data.username
          this.transactions = response.data.transactions
        }
      )
    }
  }
}
</script>

<style scoped>

</style>
