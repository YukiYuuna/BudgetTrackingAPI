<template>
  <v-form class="mt-1" ref="expenseform">
    <v-container>
      <v-row>
        <v-col cols="12" md="6" class="py-0 ma-0 my-3">
          <input type="hidden" v-model="expenseTransaction.expenseTransactionId" />
          <v-autocomplete
            v-model="expenseTransaction.categoryName"
            :items="expenseCategories"
            item-text="categoryName"
            item-value="categoryName"
            placeholder="Category"
            class="ma-0 pa-0 form-label"
            dense
            offset-x
            :rules="[required('Category')]"
          ></v-autocomplete>
        </v-col>
        <v-col cols="12" md="6" class="py-0 ma-0 my-3">
          <v-menu v-model="dateMenu" :close-on-content-click="false" max-width="290">
            <template v-slot:activator="{ on }">
              <v-text-field
                v-model="expenseTransaction.date"
                placeholder="Date"
                readonly
                v-on="on"
                class="ma-0 pa-0 form-label"
                dense
                :rules="[required('Date')]"
              ></v-text-field>
            </template>
            <v-date-picker v-model="expenseTransaction.date" @change="dateMenu = false" :no-title="true"></v-date-picker>
          </v-menu>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="6" class="py-0 ma-0">
          <v-text-field
            v-model="expenseTransaction.expenseAmount"
            placeholder="Amount"
            required
            class="ma-0 pa-0 form-label"
            dense
            :rules="[required('Amount'), minValue('Amount', 0.01)]"
          ></v-text-field>
        </v-col>
      </v-row>
      <v-textarea
        v-model="expenseTransaction.description"
        placeholder="Description"
        :auto-grow="true"
        required
        clearable
        class="ma-0 pa-0 form-label my-3"
        dense
      ></v-textarea>
      <v-row class="justify-end">
        <v-btn
          outlined
          small
          class="blue--text font-weight-bold"
          :loading="loading"
          @click="() => { if (!this.$refs.expenseform.validate()) return; onSubmitClick(); }"
        >Submit</v-btn>
        <v-btn
          v-if="showCloseButton"
          outlined
          small
          class="ml-2 blue--text font-weight-bold"
          @click="onCloseClick"
        >Close</v-btn>
      </v-row>
    </v-container>
  </v-form>
</template>

<script>
import { mapState } from 'vuex'
import validations from '@/helpers/validations'

export default {
  props: {
    expenseTransaction: {
      type: Object
    },
    expenseCategory: {
      type: Object
    },
    showCloseButton: {
      type: Boolean,
      default: false
    },
    onSubmitClick: {
      type: Function
    },
    onCloseClick: {
      type: Function
    },
    loading: {
      type: Boolean
    }
  },
  data () {
    return {
      dateMenu: false,
      ...validations
    }
  },
  computed: {
    ...mapState('expenseCategories', ['expenseCategories'])
  },
  methods: {
    reset () {
      this.$refs.expenseform.reset()
    }
  }
}
</script>

<style>
</style>
