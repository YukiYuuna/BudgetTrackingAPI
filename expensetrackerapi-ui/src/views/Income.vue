<template>
  <div>
    <v-container>
      <v-layout row>
        <v-flex xs12>
          <v-data-table :headers=headers :items=incomeTransactions sort-by='date' class="elevation-1">
            <template v-slot:top>
              <div class="d-flex align-center pa-4">
                <span class="blue--text font-weight-medium">Income</span>
                <v-divider class="mx-2 my-1" inset vertical style="height: 20px"></v-divider>
                <v-spacer></v-spacer>
                <v-dialog v-model="dialog" max-width="650px">
                  <template v-slot:activator="{ on }">
                    <v-btn outlined small class="blue--text font-weight-bold" v-on="on">New Income</v-btn>
                  </template>
                  <v-card>
                    <v-card-title>
                      <span class="text-h5">{{ formTitle }}</span>
                    </v-card-title>

                    <v-card-text>
                      <IncomeForm
                        :income-transaction="editedIncomeTransaction"
                        :showCloseButton="true"
                        :onCloseClick="close"
                        :onSubmitClick="saveIncome"
                        :loading="loading"
                        ref="form"
                      />
                    </v-card-text>
                  </v-card>
                </v-dialog>
              </div>
            </template>
            <template v-slot:item.value="{ item }">
              <span>{{ `BGN ${item.value.toFixed(2)}` }}</span>
            </template>
            <template v-slot:item.action="{ item }">
              <v-icon small class="mr-2" @click="editIncome(item)">edit</v-icon>
              <v-icon small @click="deleteIncome(item)">delete</v-icon>
            </template>
          </v-data-table>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import IncomeForm from '@/components/IncomeForm'

export default {
  components: { IncomeForm },
  data () {
    return {
      loading: false,
      dialog: false,
      headers: [
        { text: 'Id', value: 'id', align: ' d-none' },
        { text: 'Value', value: 'incomeAmount' },
        { text: 'Date', value: 'date' },
        { text: 'Category', value: 'categoryName' },
        { text: 'Description', value: 'description' },
        { text: 'Actions', value: 'action', sortable: false, width: 50 }
      ],
      editedIncomeTransaction: {
        incomeTransactionId: 0,
        date: '',
        incomeAmount: '',
        categoryName: '',
        description: ''
      },
      defaultIncomeTransaction: {
        incomeTransactionId: 0,
        date: '',
        incomeAmount: '',
        categoryName: '',
        description: ''
      }
    }
  },
  computed: {
    ...mapState({
      incomeTransactions: state => state.incomeTransactions.incomeTransactions,
      user: state => state.account.user
    }),

    formTitle () {
      return this.editedIncomeTransaction.incomeTransactionId === 0 ? 'New Transaction' : 'Edit Transaction'
    }
  },
  watch: {
    dialog (val) {
      val || this.close()
    }
  },
  methods: {
    editIncome (incomeTransaction) {
      this.editedIncomeTransaction = Object.assign({}, incomeTransaction)
      this.dialog = true
      window.location.reload()
    },

    deleteIncome (incomeTransaction) {
      confirm('Are you sure you want to delete this item?') &&
      this.$store.dispatch('incomeTransactions/deleteIncomeTransaction', { incomeTransactionId: incomeTransaction.incomeTransactionId })
      window.location.reload()
    },

    close () {
      this.dialog = false
      this.editedIncomeTransaction = Object.assign({}, this.defaultIncomeTransaction)
      this.$refs.form.reset()
      window.location.reload()
    },

    saveIncome () {
      const editedIncomeTransaction = this.editedIncomeTransaction
      this.loading = true
      if (editedIncomeTransaction.incomeTransactionId === 0) {
        this.$store.dispatch('incomeTransactions/createIncomeTransaction', {
          incomeTransaction: editedIncomeTransaction
        })
          .then(() => {
            this.close()
          })
          .finally(() => {
            window.location.reload()
            this.loading = false
          })
      } else {
        this.$store.dispatch('incomeTransactions/modifyIncomeTransaction', {
          id: editedIncomeTransaction.incomeTransactionId,
          incomeTransaction: editedIncomeTransaction
        })
          .then(() => {
            this.close()
          })
          .finally(() => {
            window.location.reload()
            this.loading = false
          })
      }
    }
  }
}
</script>

<style>
</style>
