export default class IncomeTransaction {
  constructor (date, incomeAmount, categoryName, description) {
    this.date = date
    this.incomeAmount = incomeAmount
    this.categoryName = categoryName
    this.description = description
  }
}
