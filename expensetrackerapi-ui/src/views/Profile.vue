<template>
  <div class="container rounded bg-white mt-5 mb-5">
    <div class="row">
      <div class="col-md-5 border-right">
        <div class="d-flex flex-column align-items-center text-center p-3 py-5">
          <img class="rounded-circle mt-5" width="150px" src="https://st3.depositphotos.com/15648834/17930/v/600/depositphotos_179308454-stock-illustration-unknown-person-silhouette-glasses-profile.jpg">
          <span class="font-weight-bold"> {{ firstName }}</span>
          <span class="text-black-50">{{ email }}</span>
          <span> </span>
        </div>
      </div>
      <div class="col-md-5 border-right">
        <div class="p-3 py-5">
          <h4>My Profile Info:</h4>
          <div class="row mt-2">
            <div class="col-md-12">
              <label class="labels">Username:</label>
              <span class="userInfo">{{ username }}</span>
            </div>
            <div class="col-md-12">
              <label class="labels">First Name:</label>
              <span class="userInfo">{{ firstName }}</span>
            </div>
            <div class="col-md-12">
              <label class="labels">Last Name:</label>
              <span class="userInfo">{{ lastName }}</span>
            </div>
            <div class="col-md-12">
              <label class="labels">Email:</label>
              <span class="userInfo">{{ email }}</span>
            </div>
            <div class="col-md-12">
              <label class="labels">Current Budget:</label>
              <span class="userInfo">{{ currentBudget }}</span>
            </div>
          </div>
          <div class="mt-5 text-center">
            <button class="btn btn-primary profile-button" type="button" v-on:click="modifyUserInformation">Modify</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
h4 {
  display:inline-block;
  margin-bottom: 20px;
}
.userInfo{
  margin-left: 10px;
}
button {
  height: 25%;
  width:30%;
}
</style>

<script>

import UserService from '@/services/user-service'

export default {
  name: 'Profile',
  data () {
    return {
      username: '',
      firstName: '',
      lastName: '',
      email: '',
      currentBudget: 0
    }
  },
  mounted () {
    UserService.getUserInfo().then(
      response => {
        this.username = response.data.username
        this.firstName = response.data.firstName
        this.lastName = response.data.lastName
        this.email = response.data.email
        this.currentBudget = response.data.currentBudget
      },
      error => {
        this.content =
          (error.response && error.response.data) ||
          error.message ||
          error.toString()
      }
    )
  },
  methods: {
    modifyUserInformation () {
      this.$router.push('/modifyUser')
    }
  }
}

</script>
