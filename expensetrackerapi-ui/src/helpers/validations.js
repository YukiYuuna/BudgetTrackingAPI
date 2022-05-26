const required = (property) => {
  return v => !!v || `${property} is required`
}

const email = () => {
  return v => v && (/^\w+([-]?\w+)*@\w+([-]?\w+)*(\.\w{2,3})+$/.test(v) || 'Must be a valid email')
}

const minValue = (property, minValue) => {
  return v => (v && parseFloat(v) >= minValue) || `${property} must be greater than ${minValue}`
}

const maxValue = (property, maxValue) => {
  return v => (v && parseFloat(v) <= maxValue) || `${property} must be less than ${maxValue}`
}

const minLength = (property, minLength) => {
  return v => (v && v.length >= minLength) || `${property} must be greater than ${minLength} characters`
}

const maxLength = (property, maxLength) => {
  return v => (v && v.length <= maxLength) || `${property} must be less than ${maxLength} characters`
}

export default {
  required,
  email,
  minValue,
  maxValue,
  minLength,
  maxLength
}
