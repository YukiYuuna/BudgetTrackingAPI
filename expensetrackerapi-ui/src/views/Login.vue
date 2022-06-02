<template>
  <v-app>
    <v-main>
      <v-container fill-height>
        <v-layout align-center justify-center>
          <v-flex xs12 sm8 md4>
            <v-card tile>
              <v-toolbar flat color="primary" dark>
                <v-toolbar-title>Login</v-toolbar-title>
              </v-toolbar>
              <v-card-text>
                <v-form ref="loginForm">
                  <v-text-field
                    v-model="loginForm.username"
                    prepend-icon="mdi-account"
                    placeholder="Username"
                    :rules="[required('Username')]"
                    dense
                  ></v-text-field>
                  <v-text-field
                    v-model="loginForm.password"
                    placeholder="Password"
                    prepend-icon="lock"
                    :type="showPassword ? 'text' : 'password'"
                    :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append="showPassword = !showPassword"
                    :rules="[required('Password')]"
                    dense
                  ></v-text-field>
                </v-form>
              </v-card-text>
              <v-card-actions>
                <v-btn
                  small
                  outlined
                  class="primary--text"
                  @click.native="handleRegisterClick"
                >Register</v-btn>
                <v-spacer></v-spacer>
                <v-btn
                  small
                  outlined
                  class="primary--text"
                  @click="handleLoginSubmit"
                  :loading="loading"
                >Login</v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { mapState } from 'vuex'
import validations from '@/helpers/validations'
import router from '@/router/index'

export default {
  data () {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      showPassword: false,
      ...validations
    }
  },
  computed: {
    ...mapState({
      loading: state => state.loader.loading
    })
  },
  created () {
    // reset theme
    this.$vuetify.theme.dark = false
    // reset login status
    this.$store.dispatch('account/logout')
    this.$store.dispatch('expenseCategories/logout')
    this.$store.dispatch('incomeCategories/logout')
    this.$store.dispatch('expenseTransactions/logout')
    this.$store.dispatch('incomeTransactions/logout')
  },
  methods: {
    handleLoginSubmit () {
      if (!this.$refs.loginForm.validate()) return

      if (this.loginForm.username && this.loginForm.password) {
        this.$store.dispatch('account/login', this.loginForm)
          .then(() => {
            this.$router.push('/')
          })
          .finally(() => {
            window.location.reload()
            this.loading = false
          })
      }
    },
    handleRegisterClick () {
      router.push('/register')
    }
  }
}
</script>

<style>
</style>
