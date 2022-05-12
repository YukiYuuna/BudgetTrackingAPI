<template>
  <div class="col-md-12">
    <div class="card card-container">
      <form name="form" @submit.prevent="submitTransaction">
        <div v-if="!successful">
          <div class="form-group">
            <label for="date">Choose a date: </label>
            <b-form-datepicker
              id="date"
              v-model="transaction.date"
              class="mb-2"
            ></b-form-datepicker>
          </div>
          <div
            v-if="submitted && errors.has('date')"
            class="alert-danger"
          >{{ errors.first('date') }}
          </div>
          <div class="form-group">
            <label for="expenseAmount">Expense Amount: </label>
            <input
              id="expenseAmount"
              v-model="transaction.expenseAmount"
              type="number"
              class="form-control"
              style="text-align: center"
              name="expenseAmount"
            />
          </div>
          <div
            v-if="submitted && errors.has('expenseAmount')"
            class="alert-danger"
          >{{ errors.first('expenseAmount') }}
          </div>
          <div class="form-group">
            <label for="categoryName">Category Name: </label>
            <b-form-select
              id="categoryName"
              v-model="transaction.categoryName"
              :options="categories"
              required
            ></b-form-select>
          </div>
          <div
            v-if="submitted && errors.has('categoryName')"
            class="alert-danger"
          >{{ errors.first('categoryName') }}
          </div>
          <div class="form-group">
            <label for="description">Description: </label>
            <input
              v-model="transaction.description"
              type="text"
              class="form-control"
              name="description"
            />
            <div class="form-group">
              <button class="btn btn-primary btn-block" style="margin-top:30px">Add Transaction</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>

import ExpenseTransaction from '@/models/expense-transaction'

export default {
  data () {
    return {
      transaction: new ExpenseTransaction('', 0, '', ''),
      categories: [{ text: 'Select One', value: null }, 'Groceries', 'Travel', 'House', 'Gas', 'Healthcare'],
      show: true,
      submitted: false,
      successful: false,
      message: ''
    }
  },
  methods: {
    submitTransaction () {
      this.message = ''
      this.submitted = true
      this.$store.dispatch('transactions/createExpenseTransaction', this.transaction).then(
        data => {
          this.message = data.message
          this.successful = true
          console.log(data)
        },
        error => {
          this.message =
            (error.response && error.response.data) ||
            error.message ||
            error.toString()
          this.successful = false
        }
      )
      this.$router.push('/transactions')
    }
  }
}
</script>

<style scoped>
label {
  display: block;
  margin-top: 10px;
}

.card-container.card {
  max-width: 350px !important;
  padding: 40px 40px;
}

.card {
  background-color: #f7f7f7;
  padding: 20px 25px 30px;
  margin: 50px auto 25px;
  -moz-border-radius: 2px;
  -webkit-border-radius: 2px;
  border-radius: 2px;
  -moz-box-shadow: 0 2px 2px rgba(0, 0, 0, 0.3);
  -webkit-box-shadow: 0 2px 2px rgba(0, 0, 0, 0.3);
  box-shadow: 0 2px 2px rgba(0, 0, 0, 0.3);
}

.profile-img-card {
  width: 96px;
  height: 96px;
  margin: 0 auto 10px;
  display: block;
  -moz-border-radius: 50%;
  -webkit-border-radius: 50%;
  border-radius: 50%;
}
</style>
