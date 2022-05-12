<template>
  <div>
    <b-table striped hover :items="transactions"></b-table>
    <button v-on:click="addExpenseTransaction">Add Transaction</button>
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
  mounted () {
    ExpenseTransactionsService.getAllExpenseTransactions().then(
      (response) => {
        this.transactions = response.data.transactions
      }
    )
  },
  methods: {
    addExpenseTransaction () {
      this.$router.push('/addTransaction')
    }
  }
}
</script>

<style scoped>

</style>
