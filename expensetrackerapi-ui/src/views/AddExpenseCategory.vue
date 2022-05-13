<template>
  <div class="col-md-12">
    <div class="card card-container">
      <form name="form" @submit.prevent="submitCategory">
        <div v-if="!successful">
          <div class="form-group">
            <label for="categoryName">Category Name: </label>
            <input
              id="categoryName"
              v-model="category.categoryName"
              required
            />
            <div
              v-if="message.message"
              class="alert"
              :class="successful ? 'alert-success' : 'alert-danger'"
            >{{ message.message }}
          </div>
          </div>
          <div class="form-group">
            <button class="btn btn-primary btn-block" style="margin-top:30px">Add Category</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>

import ExpenseCategory from '@/models/expense-category'

export default {
  data () {
    return {
      category: new ExpenseCategory(''),
      show: true,
      submitted: false,
      successful: false,
      message: ''
    }
  },
  methods: {
    submitCategory () {
      this.message = ''
      this.submitted = true
      this.$store.dispatch('categories/createExpenseCategory', this.category).then(
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
      this.$router.push('/categories')
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
