<template>
  <div>
    <v-container>
      <v-layout row justify-space-between>
        <v-flex xs12 md6>
          <v-card
            :class="{'pa-2 mr-2 mt-2': $vuetify.breakpoint.smAndDown,
             'pa-2 mr-2': $vuetify.breakpoint.mdAndUp}">
            <div
              class="blue--text px-2 py-1 text-capitalize font-weight-medium"
              style="margin-bottom: -15px"
            >General Settings
            </div>
            <v-divider></v-divider>
            <v-form class="xs12 my-1">
              <v-container>
                <v-text-field
                  label="System Name"
                  required
                  class="ma-0 pa-0 form-label"
                  v-model="settings.systemName"
                  dense
                ></v-text-field>
                <v-text-field
                  label="Currency"
                  required
                  class="ma-0 pa-0 form-label"
                  v-model="settings.currency"
                  dense
                ></v-text-field>
              </v-container>
            </v-form>
          </v-card>
        </v-flex>
        <v-flex xs12>
          <v-flex xs12>
            <v-card
              :class="{'pa-2 mr-2 mt-2': $vuetify.breakpoint.smAndDown, 'pa-2 mr-2': $vuetify.breakpoint.mdAndUp}">
              <ExpenseCategories/>
            </v-card>
          </v-flex>
        </v-flex>
        <v-flex xs12>
          <v-card
            :class="{'pa-2 mr-2 mt-2': $vuetify.breakpoint.smAndDown, 'pa-2 mr-2': $vuetify.breakpoint.mdAndUp}">
            <IncomeCategories/>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import ExpenseCategories from '@/components/ExpenseCategories'
import IncomeCategories from '@/components/IncomeCategories'

export default {
  components: {
    ExpenseCategories,
    IncomeCategories
  },
  computed: {
    ...mapState({
      user: state => state.account.user,
      currency: state => state.account.currency
    })
  },
  mounted () {
    this.settings = {
      systemName: this.user.username,
      useDarkMode: this.user.useDarkMode,
      currency: 'BGN'
    }
  },
  data: () => ({
    loading: false,
    settings: {}
  })
}
</script>

<style>
</style>
