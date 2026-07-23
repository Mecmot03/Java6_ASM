let nextConfirmId = 0
const pendingConfirms = new Map()

export const confirmDialog = (message, title = 'Xác nhận') => {
  return new Promise((resolve) => {
    const id = ++nextConfirmId
    pendingConfirms.set(id, resolve)

    window.dispatchEvent(
      new CustomEvent('app-confirm', {
        detail: { id, title, message },
      })
    )
  })
}

export const resolveConfirmDialog = (id, confirmed) => {
  const resolve = pendingConfirms.get(id)
  if (!resolve) {
    return
  }

  pendingConfirms.delete(id)
  resolve(Boolean(confirmed))
}