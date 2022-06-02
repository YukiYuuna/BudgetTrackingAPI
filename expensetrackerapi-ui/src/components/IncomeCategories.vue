<template>
  <v-data-table
    :headers="headers"
    :items="categories"
    sort-by="name"
    :items-per-page="5"
    loading-text="Loading... Please wait"
  >
    <template v-slot:top>
      <div class="d-flex align-center pa-1 pb-2">
        <span class="blue--text font-weight-medium">Income Categories</span>
        <v-divider class="mx-2 my-1" inset vertical style="height: 20px"></v-divider>
        <v-spacer></v-spacer>
        <v-dialog v-model="dialog" max-width="500px">
          <template v-slot:activator="{ on }">
            <v-btn outlined small class="blue--text font-weight-bold" v-on="on">New Category</v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="text-h5">{{ categoryFormTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <input type="hidden" v-model="editedCategory.incomeCategoryId" />
                <v-text-field
                  class="ma-0 pa-0 form-label"
                  dense
                  v-model="editedCategory.categoryName"
                  label="Category Name"
                ></v-text-field>
                <v-text-field
                  class="ma-0 pa-0 form-label"
                  dense
                  v-model="editedCategory.color"
                  label="Color"
                >
                  <template v-slot:append>
                    <v-menu
                      v-model="menu"
                      top
                      nudge-bottom="110"
                      nudge-left="20"
                      :close-on-content-click="false"
                    >
                      <template v-slot:activator="{ on }">
                        <div :style="swatchStyle(editedCategory)" v-on="on" />
                      </template>
                      <v-card>
                        <v-card-text class="pa-0">
                          <v-color-picker
                            mode="hexa"
                            hide-mode-switch
                            v-model="editedCategory.color"
                            flat
                          />
                        </v-card-text>
                        <v-card-actions class="pa-0 pb-1 pr-1">
                          <v-spacer></v-spacer>
                          <v-btn
                            outlined
                            small
                            class="blue--text font-weight-bold"
                            @click="menu = false"
                          >Select</v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-menu>
                  </template>
                </v-text-field>
              </v-container>
            </v-card-text>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                outlined
                small
                class="blue--text font-weight-bold"
                @click="saveIncomeCategory"
                :loading="loading"
              >Save</v-btn>
              <v-btn outlined small class="blue--text font-weight-bold" @click="close">Cancel</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </div>
    </template>
    <template v-slot:item.color="{ item }">
      <v-chip
        :color="item.color"
        style="padding: 0px; height: 20px; width: 20px"
        flat
        small
        class="ml-1 mb-1"
      ></v-chip>
    </template>
    <template v-slot:item.action="{ item }">
      <v-icon small class="mr-2" @click="editIncomeCategory(item)">edit</v-icon>
      <v-icon small @click="deleteIncomeCategory(item)">delete</v-icon>
    </template>
    <template v-slot:no-data>
      <span>No Data Available</span>
    </template>
  </v-data-table>
</template>

<script>
import { mapState } from 'vuex'

export default {
  data: () => ({
    loading: false,
    dialog: false,
    menu: false,
    headers: [
      { text: 'Id', value: 'incomeCategoryId', align: ' d-none' },
      { text: 'Name', value: 'categoryName' },
      { text: 'Color', value: 'color', width: 100 },
      { text: 'Actions', value: 'action', sortable: false, width: 50 }
    ],
    editedCategory: {
      incomeCategoryId: 0,
      categoryName: '',
      color: '#1976D2FF'
    },
    defaultCategory: {
      incomeCategoryId: 0,
      categoryName: '',
      color: '#1976D2FF'
    }
  }),

  computed: {
    ...mapState({
      categories: state => state.incomeCategories.incomeCategories
    }),

    categoryFormTitle () {
      return this.editedCategory.incomeCategoryId === 0 ? 'New Category' : 'Edit Category'
    }
  },

  watch: {
    dialog (val) {
      val || this.close()
    }
  },
  methods: {
    swatchStyle (item) {
      const { color } = item
      return {
        backgroundColor: color,
        cursor: 'pointer',
        height: '20px',
        width: '20px',
        borderRadius: '50%'
      }
    },

    deleteIncomeCategory (incomeCategory) {
      confirm('Are you sure you want to delete this item?') &&
      this.$store.dispatch('incomeCategories/deleteIncomeCategory', { incomeCategoryId: incomeCategory.incomeCategoryId })
      window.location.reload()
    },

    editIncomeCategory (item) {
      this.editedCategory = Object.assign({}, item)
      this.dialog = true
    },

    close () {
      this.dialog = false
      this.editedCategory = Object.assign({}, this.defaultCategory)
    },

    saveIncomeCategory () {
      this.loading = true
      const editedIncomeCategory = this.editedCategory
      if (editedIncomeCategory.incomeCategoryId === 0) {
        this.$store.dispatch('incomeCategories/createIncomeCategory', {
          incomeCategory: editedIncomeCategory
        })
          .then(() => {
            this.close()
          })
          .finally(() => {
            window.location.reload()
            this.loading = false
          })
      } else {
        this.$store.dispatch('incomeCategories/modifyIncomeCategory', {
          incomeCategory: editedIncomeCategory
        })
          .then(() => {
            this.close()
          })
          .finally(() => {
            this.loading = false
            window.location.reload()
          })
      }
    }
  }
}
</script>

<style>
</style>
