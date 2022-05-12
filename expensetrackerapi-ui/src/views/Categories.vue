<template>
  <div>
    <b-table striped hover :items="totalCategories"></b-table>
    <button v-on:click="addExpenseCategories">Add Category</button>
  </div>
</template>

<script>
import ExpenseCategoriesService from '@/services/expense-categories-service'

export default {
  name: 'Categories',
  data () {
    return {
      totalCategories: [
        {
          categoryName: ''
        }
      ]
    }
  },
  mounted () {
    ExpenseCategoriesService.getAllExpenseCategories().then(
      (response) => {
        this.totalCategories = response.data.totalCategories
      }
    )
  },
  methods: {
    addExpenseCategories () {
      this.$router.push('/addCategory')
    }
  }
}
</script>

<style scoped>

</style>
