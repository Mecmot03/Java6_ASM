export const getAuthStorageItem = (key) =>
  localStorage.getItem(key) ?? sessionStorage.getItem(key)

export const clearAuthStorage = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('user')
}
