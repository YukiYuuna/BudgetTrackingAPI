<template>
  <div>
    <v-container>
      <v-layout row>
        <v-flex xs12>
          <v-data-table :headers=headers :items=expenseTransactions sort-by='date' class="elevation-1">
            <template v-slot:top>
              <div class="d-flex align-center pa-4">
                <span class="blue--text font-weight-medium">Expenses</span>
                <v-divider class="mx-2 my-1" inset vertical style="height: 20px"></v-divider>
                <v-spacer></v-spacer>
                <v-dialog v-model="dialog" max-width="650px">
                  <template v-slot:activator="{ on }">
                    <v-btn outlined small class="blue--text font-weight-bold" v-on="on">New Expense</v-btn>
                  </template>
                  <v-card>
                    <v-card-title>
                      <span class="text-h5">{{ formTitle }}</span>
                    </v-card-title>

                    <v-card-text>
                      <ExpenseForm
                        :expenseTransaction="editedExpenseTransaction"
                        :showCloseButton="true"
                        :onCloseClick="close"
                        :onSubmitClick="saveExpense"
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
              <v-icon small class="mr-2" @click="editExpense(item)">edit</v-icon>
              <v-icon small @click="deleteExpense(item)">delete</v-icon>
            </template>
          </v-data-table>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import ExpenseForm from '@/components/ExpenseForm'

export default {
  components: { ExpenseForm },
  data () {
    return {
      loading: false,
      dialog: false,
      headers: [
        { text: 'Id', value: 'id', align: ' d-none' },
        { text: 'Value', value: 'expenseAmount' },
        { text: 'Date', value: 'date' },
        { text: 'Category', value: 'categoryName' },
        { text: 'Description', value: 'description' },
        { text: 'Actions', value: 'action', sortable: false, width: 50 }
      ],
      editedExpenseTransaction: {
        expenseTransactionId: 0,
        date: '',
        expenseAmount: '',
        categoryName: '',
        description: ''
      },
      defaultExpenseTransaction: {
        expenseTransactionId: 0,
        date: '',
        expenseAmount: '',
        categoryName: '',
        description: ''
      }
    }
  },
  computed: {
    ...mapState({
      expenseTransactions: state => state.expenseTransactions.expenseTransactions,
      user: state => state.account.user
    }),

    formTitle () {
      return this.editedExpenseTransaction.expenseTransactionId === 0 ? 'New Transaction' : 'Edit Transaction'
    }
  },
  watch: {
    dialog (val) {
      val || this.close()
    }
  },
  methods: {
    editExpense (expenseTransaction) {
      this.editedExpenseTransaction = Object.assign({}, expenseTransaction)
      this.dialog = true
      window.location.reload()
    },

    deleteExpense (expenseTransaction) {
      console.log(expenseTransaction.expenseTransactionId)
      confirm('Are you sure you want to delete this item?') &&
      this.$store.dispatch('expenseTransactions/deleteExpenseTransaction', { expenseTransactionId: expenseTransaction.expenseTransactionId })
      window.location.reload()
    },

    close () {
      this.dialog = false
      this.editedExpenseTransaction = Object.assign({}, this.defaultExpenseTransaction)
      this.$refs.form.reset()
    },

    saveExpense () {
      const editedExpenseTransaction = this.editedExpenseTransaction
      this.loading = true
      console.log(editedExpenseTransaction)
      if (editedExpenseTransaction.expenseTransactionId === 0) {
        this.$store.dispatch('expenseTransactions/createExpenseTransaction', {
          expenseTransaction: editedExpenseTransaction
        })
          .then(() => {
            this.close()
          })
          .finally(() => {
            window.location.reload()
            this.loading = false
          })
      } else {
        this.$store.dispatch('expenseTransactions/modifyExpenseTransaction', {
          expenseTransaction: editedExpenseTransaction
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
