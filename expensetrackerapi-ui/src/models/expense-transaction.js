export default class ExpenseTransaction {
  constructor (date, expenseAmount, categoryName, description) {
    this.date = date
    this.expenseAmount = expenseAmount
    this.categoryName = categoryName
    this.description = description
  }
}
