<template>
  <div>
    <b-table striped hover :items="transactions"></b-table>
    <button v-on:click="getAllExpenseTransactions">Load Expense Transactions</button>
    <button v-on:click="addExpenseTransaction">Add Expense</button>
    <div class="form-group">
      <div v-if="message.message" class="alert alert-danger" role="alert">{{ message.message }}</div>
    </div>
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
      ],
      message: ''
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
    },
    addExpenseTransaction () {
      this.$router.push('/addTransaction')
    }
  }
}
</script>

<style scoped>

</style>
