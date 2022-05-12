<template>
  <div class="col-md-12">
    <div class="card card-container">
      <form name="form" @submit.prevent="modifyUserInformation">
        <div v-if="!successful">
          <div class="form-group">
            <label for="username">New Username: </label>
            <input
              id="username"
              v-model="user.username"
              type="text"
              class="form-control"
              name="username"
            />
          </div>
          <div
            v-if="submitted && errors.has('username')"
            class="alert-danger"
          >{{ errors.first('username') }}
          </div>
          <div class="form-group">
            <label for="firstName">First Name: </label>
            <input
              id="firstName"
              v-model="user.firstName"
              type="text"
              class="form-control"
              name="firstName"
            />
          </div>
          <div
            v-if="submitted && errors.has('firstName')"
            class="alert-danger"
          >{{ errors.first('firstName') }}
          </div>
          <div class="form-group">
            <label for="lastName">Last Name: </label>
            <input
              id="lastName"
              v-model="user.lastName"
              type="text"
              class="form-control"
              name="lastName"
            />
          </div>
          <div
            v-if="submitted && errors.has('lastName')"
            class="alert-danger"
          >{{ errors.first('lastName') }}
          </div>
          <div class="form-group">
            <label for="email">New Email: </label>
            <input
              id="email"
              v-model="user.email"
              type="email"
              class="form-control"
              name="email"
            />
          </div>
          <div
            v-if="submitted && errors.has('email')"
            class="alert-danger"
          >{{ errors.first('email') }}
          </div>
          <div class="form-group">
            <label for="currentBudget">Current Budget: </label>
            <input
              v-model="user.currentBudget"
              type="number"
              style="text-align: center"
              class="form-control"
              name="currentBudget"
            />
            <div class="form-group">
              <button class="btn btn-primary btn-block" style="margin-top:40px; margin-bottom: -10px">Modify</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>

import User from '@/models/user'

export default {
  data () {
    return {
      user: new User('', '', '', '', 0),
      show: true,
      submitted: false,
      successful: false,
      message: ''
    }
  },
  methods: {
    modifyUserInformation () {
      this.message = ''
      this.submitted = true
      this.$store.dispatch('userModule/modifyUserInfo', this.user).then(
        data => {
          this.message = data.message
          this.successful = true
          this.$router.push('/profile')
        },
        error => {
          this.message =
            (error.response && error.response.data) ||
            error.message ||
            error.toString()
          this.successful = false
        }
      )
      this.$router.push('/profile')
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

</style>
