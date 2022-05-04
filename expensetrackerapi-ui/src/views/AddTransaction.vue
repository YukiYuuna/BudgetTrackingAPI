<template>
  <div>
    <b-form @submit.prevent="submitTransaction" v-if="show">
      <b-form-group
        id="input-group-1" label="Date:" label-for="input-1">
        <b-form-input
          id="date"
          v-model="transaction.date"
          placeholder="Enter date"
          required
        ></b-form-input>
      </b-form-group>

      <b-form-group id="input-group-2" label="Expense Amount:" label-for="input-2">
        <b-form-input
          id="expenseAmount"
          v-model="transaction.expenseAmount"
          placeholder="Enter amount"
          required
        ></b-form-input>
      </b-form-group>

      <b-form-group id="input-group-3" label="Category:" label-for="input-3">
        <b-form-select
          id="categoryName"
          v-model="transaction.categoryName"
          :options="categories"
          required
        ></b-form-select>
      </b-form-group>

      <b-form-group id="input-group-2" label="Expense Description:" label-for="input-2">
        <b-form-input
          id="description"
          v-model="transaction.description"
          placeholder="Enter description:"
          required
        ></b-form-input>
      </b-form-group>

      <b-button type="submit" variant="primary">Submit</b-button>
      <b-button type="reset" variant="danger">Reset</b-button>
    </b-form>
    <b-card class="mt-3" header="Expense Transaction">
      <pre class="m-0">{{ transaction }}</pre>
    </b-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      transaction: {
        date: '',
        expenseAmount: '',
        categoryName: null,
        description: ''
      },
      categories: [{ text: 'Select One', value: null }, 'Groceries', 'Travel', 'House', 'Gas', 'Healthcare'],
      show: true,
      message: ''
    }
  },
  methods: {
    submitTransaction () {
      this.$store.dispatch('transactions/createTransaction', this.transaction).then(
        data => {
          this.message = data.message
          this.successful = true
          return 'success'
        },
        error => {
          this.message =
            (error.response && error.response.data) ||
            error.message ||
            error.toString()
          this.successful = false
        }
      )
    }
  }
}
</script>

<style scoped>

</style>
