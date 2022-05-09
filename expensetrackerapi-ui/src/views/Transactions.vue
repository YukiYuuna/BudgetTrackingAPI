<template>
  <div>
    <b-table striped hover :items="transactions"></b-table>
    <button v-on:click="getAllExpenseTransactions">Load Expense Transactions</button>
    <button v-on:click="addExpenseTransaction">Add Expense</button>
  </div>
</template>

<script>
import ExpenseTransactionsService from '../services/expense-transactions-service.js'

export default {
  name: 'Transactions',
  data () {
    return {
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
          this.transactions = response.data.transactions
        }
      )
    },
    addExpenseTransaction () {
      this.$router.push('/addTransaction')
    }
  }
}
</script>

<style scoped>

</style>
