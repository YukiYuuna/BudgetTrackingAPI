<template>
  <div>
    <v-container>
      <v-layout row justify-space-between>
        <v-flex xs12>
          <v-layout row justify-space-between>
            <v-flex xs12 md4 my-3>
              <v-card class="pa-2 mr-2" flat min-height="340px" height="100%">
                <div
                  class="blue--text px-2 py-1 text-capitalize font-weight-medium"
                >General Settings</div>
                <v-divider></v-divider>
                <v-form class="xs12 my-1">
                  <v-container>
                    <v-switch
                      class="ma-0 pa-0 form-label"
                      color="grey"
                      label="Dark Theme"
                      dense
                      v-model="settings.useDarkMode"
                    ></v-switch>
                    <v-row class="justify-end">
                      <v-btn
                        outlined
                        small
                        class="blue--text font-weight-bold"
                        @click="handleSubmit"
                        :loading="loading"
                      >Submit</v-btn>
                    </v-row>
                  </v-container>
                </v-form>
              </v-card>
            </v-flex>
            <v-flex xs12 md8 my-3>
              <v-card
                :class="{'pa-2 mr-2 mt-2': $vuetify.breakpoint.smAndDown, 'pa-2 mr-2': $vuetify.breakpoint.mdAndUp}"
                flat
                min-height="340px"
                height="100%"
              >
              </v-card>
            </v-flex>
          </v-layout>
        </v-flex>

        <v-flex xs12>
          <v-container px-0 pb-0>
            <v-layout row justify-space-between>
              <v-flex xs12 >
                <v-card class="pa-2 mr-2 my-3" flat min-height="340px" height="100%">
                  <ExpenseCategories />
                </v-card>
              </v-flex>
            </v-layout>
          </v-container>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import ExpenseCategories from '@/components/ExpenseCategories'

export default {
  components: {
    ExpenseCategories
  },
  data: () => ({
    loading: false,
    settings: {},
  })
}
</script>

<style>
</style>
