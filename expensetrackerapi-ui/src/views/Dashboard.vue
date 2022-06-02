<template>
  <div>
    <v-container>
      <v-layout row justify-space-between>
        <v-flex xs12 md6>
          <v-card class="pa-2 mr-2" raised min-height="100%">
            <div class="blue--text px-2 py-1 text-capitalize font-weight-medium"
                 style="margin-bottom: -15px"
            >Add New Expense</div>
            <v-divider></v-divider>
            <ExpenseForm
              :expense-transaction="expenseTransaction"
              :onSubmitClick="saveExpense"
              :loading="loading"
              ref="form"
            />
          </v-card>
        </v-flex>
        <v-flex xs12 md6>
          <v-card
            :class="{'pa-2 mr-2 mt-2': $vuetify.breakpoint.smAndDown, 'pa-2 mr-2': $vuetify.breakpoint.mdAndUp}"
            tile
            min-height="100%"
            height="100%"
          >
            <div
              class="blue--text px-2 py-1 text-capitalize font-weight-medium"
              style="margin-bottom: -15px"
            >Budget (Current Month)</div>
            <v-divider></v-divider>
            <DoughnutChart
              :height="75"
              :theme="theme"
              :showLabel="true"
              :showLabelLines="true"
              :seriesData="monthlyBudget.data"
              :centerY="52"
              :pieRadiusOuter="75"
            />
            <div class="d-flex justify-space-around text-subtitle-2 px-12 mx-12">
              <div>
                <div>Total Budget</div>
                <div>{{`BGN ${monthlyBudget.totalBudget}`}}</div>
              </div>
              <div>
                <div>Total Spent</div>
                <div>{{`BGN ${monthlyBudget.totalSpent}`}}</div>
              </div>
            </div>
          </v-card>
        </v-flex>
        <v-flex xs12 md12>
          <v-container px-0 pb-0>
            <v-card class="pa-2 mr-2" tile>
              <div
                class="blue--text px-2 py-1 text-capitalize font-weight-medium"
                style="margin-bottom: -15px"
              >Budgets By Categories (Current Month)</div>
              <v-divider></v-divider>
              <div class="category-budgets">
                <div
                  v-for="budget in monthlyBudgetsByCategory"
                  :key="budget.name"
                  class="category-budgets-budget"
                >
                  <DoughnutChart
                    :titleText="budget.name"
                    :showTitle="true"
                    :height="90"
                    :titleFontSize="14"
                    :theme="theme"
                    :showTooltip="false"
                    :seriesData="budget.monthlyBudget"
                  />
                </div>
              </div>
            </v-card>
          </v-container>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>
<script>
import ExpenseForm from '@/components/ExpenseForm'
import DoughnutChart from '@/components/Charts/DoughnutChart'
import { mapGetters } from 'vuex'

export default {
  components: { ExpenseForm, DoughnutChart },
  mounted () {
    this.$store.dispatch('statistics/loadCurrentBudget')
  },
  computed: {
    ...mapGetters('statistics', ['monthlyBudget', 'yearlyExpenses', 'monthlyBudgetsByCategory'])
  },
  data () {
    return {
      loading: false,
      dateMenu: false,
      theme: 'light',
      expenseTransaction: {}
    }
  },
  methods: {
    saveExpense () {
      this.loading = true
      this.$store.dispatch('expenseTransactions/createExpenseTransaction', {
        expenseTransaction: this.expenseTransaction
      })
        .then(() => {
          this.expenseTransaction = {}
          this.$refs.form.reset()
        })
        .finally(() => {
          window.location.reload()
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>
.category-budgets {
  display: flex;
  justify-content: space-between;
  overflow: hidden;
  height: 180px;
  padding-left: 10px;
}
.category-budgets:hover {
  overflow-x: scroll;
}
.category-budgets-budget {
  width: 180px;
  flex: 0 0 auto;
}
</style>
