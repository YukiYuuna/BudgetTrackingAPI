<template>
  <div>
    <div class="bd-example">
      <button style="margin-right: 15px" class="categoryType" v-on:click="getExpenseCategories">Expense</button>
      <button class="categoryType" v-on:click="getIncomeCategories">Income</button>
    </div>
    <div v-if="expenseCategory">
      <b-table striped hover :items="totalExpenseCategories"></b-table>
      <button v-on:click="addExpenseCategory">Add Category</button>
    </div>
    <div v-else-if="incomeCategory">
      <b-table striped hover :items="totalIncomeCategories"></b-table>
      <button v-on:click="addIncomeCategory">Add Category</button>
    </div>
  </div>
</template>

<script>
import ExpenseCategoriesService from '@/services/expense-categories-service'
import IncomeCategoriesService from '@/services/income-categories-service'

export default {
  name: 'Categories',
  props: {
    expenseCategory: Boolean,
    incomeCategory: Boolean
  },
  data () {
    return {
      totalExpenseCategories: [
        {
          categoryName: ''
        }
      ],
      totalIncomeCategories: [
        {
          categoryName: ''
        }
      ]
    }
  },
  mounted () {
    ExpenseCategoriesService.getAllExpenseCategories().then(
      (response) => {
        this.expenseCategory = true
        this.incomeCategory = false
        this.totalExpenseCategories = response.data.totalCategories
      }
    )
  },
  methods: {
    addExpenseCategory () {
      this.$router.push('/addExpenseCategory')
    },
    addIncomeCategory () {
      this.$router.push('/addIncomeCategory')
    },
    getIncomeCategories () {
      IncomeCategoriesService.getAllIncomeCategories().then(
        (response) => {
          this.expenseCategory = false
          this.incomeCategory = true
          this.totalIncomeCategories = response.data.totalCategories
        }
      )
    },
    getExpenseCategories () {
      ExpenseCategoriesService.getAllExpenseCategories().then(
        (response) => {
          this.expenseCategory = true
          this.incomeCategory = false
          this.totalExpenseCategories = response.data.totalCategories
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
