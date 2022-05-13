<template>
  <div class="col-md-12">
    <div class="card card-container">
      <form name="form" @submit.prevent="submitTransaction">
        <div v-if="!successful">
          <div class="form-group">
            <label for="date">Choose a date: </label>
            <b-form-datepicker
              id="date"
              v-model="incomeTransaction.date"
              class="mb-2"
            ></b-form-datepicker>
          </div>
          <div
            v-if="submitted && errors.has('date')"
            class="alert-danger"
          >{{ errors.first('date') }}
          </div>
          <div class="form-group">
            <label for="incomeAmount">Income Amount: </label>
            <input
              id="incomeAmount"
              v-model="incomeTransaction.incomeAmount"
              type="number"
              class="form-control"
              style="text-align: center"
              name="incomeAmount"
            />
          </div>
          <div
            v-if="submitted && errors.has('incomeAmount')"
            class="alert-danger"
          >{{ errors.first('incomeAmount') }}
          </div>
          <div class="form-group">
            <label for="categoryName">Category Name: </label>
            <b-form-select
              id="categoryName"
              style="text-align: center"
              v-model="incomeTransaction.categoryName"
              :options="incomeCategories"
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
              v-model="incomeTransaction.description"
              style="text-align: center"
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

import IncomeTransaction from '@/models/income-transaction'

export default {
  data () {
    return {
      incomeTransaction: new IncomeTransaction('', 0, '', ''),
      incomeCategories: [{ text: 'Select One', value: null }, 'Salary', 'Gifts'],
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
      this.$store.dispatch('allTransactions/createIncomeTransaction', this.incomeTransaction).then(
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
