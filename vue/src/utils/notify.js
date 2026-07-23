export const notify = (message, type = 'info') => {
  window.dispatchEvent(
    new CustomEvent('app-notify', {
      detail: { message, type },
    })
  )
}