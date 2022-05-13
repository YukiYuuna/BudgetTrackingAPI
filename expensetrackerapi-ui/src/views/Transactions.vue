<template>
  <div>
    <div class="bd-example">
      <button style="margin-right: 15px" class="categoryType" v-on:click="getExpenseTransactions">Expense</button>
      <button class="categoryType" v-on:click="getIncomeTransactions">Income</button>
    </div>
    <div v-if="expenseTransaction">
      <b-table striped hover :items="totalExpenseTransactions"></b-table>
      <button v-on:click="addExpenseTransaction">Add Transaction</button>
    </div>
    <div v-else-if="incomeTransaction">
      <b-table striped hover :items="totalIncomeTransactions"></b-table>
      <button v-on:click="addIncomeTransaction">Add Transaction</button>
    </div>
  </div>
</template>

<script>
import ExpenseTransactionsService from '../services/expense-transactions-service.js'
import IncomeTransactionsService from '../services/income-transactions-service.js'

export default {
  name: 'Transactions',
  props: {
    expenseTransaction: Boolean,
    incomeTransaction: Boolean
  },
  data () {
    return {
      totalExpenseTransactions: [
        {
          date: '',
          expenseAmount: '',
          categoryName: '',
          description: ''
        }
      ],
      totalIncomeTransactions: [
        {
          date: '',
          incomeAmount: '',
          categoryName: '',
          description: ''
        }
      ]
    }
  },
  mounted () {
    ExpenseTransactionsService.getAllExpenseTransactions().then(
      (response) => {
        this.expenseTransaction = true
        this.incomeTransaction = false
        this.totalExpenseTransactions = response.data.transactions
      })
  },
  methods: {
    addExpenseTransaction () {
      this.$router.push('/addExpenseTransaction')
    },
    addIncomeTransaction () {
      this.$router.push('/addIncomeTransaction')
    },
    getExpenseTransactions () {
      ExpenseTransactionsService.getAllExpenseTransactions().then(
        (response) => {
          this.expenseTransaction = true
          this.incomeTransaction = false
          this.totalExpenseTransactions = response.data.transactions
        }
      )
    },
    getIncomeTransactions () {
      IncomeTransactionsService.getAllIncomeTransactions().then(
        (response) => {
          this.expenseTransaction = false
          this.incomeTransaction = true
          this.totalIncomeTransactions = response.data.transactions
        }
      )
    }
  }
}
</script>

<style scoped>
.bd-example {
  padding: 1.5rem;
  margin-right: 0;
  margin-left: 0;
  border-width: 0.2rem;
}
.categoryType {
  transition-duration: 0.4s;
  width: 20%;
  height: 40%;
  border-radius: 6px;
  background: white;
}

.categoryType:hover {
  width: 20%;
  height: 40%;
  border-radius: 8px;
  background: #3366ff;
}
</style>
