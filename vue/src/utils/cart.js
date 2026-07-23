const GUEST_CART_KEY = 'asm_guest_cart'

const readStorage = () => {
  const saved = localStorage.getItem(GUEST_CART_KEY)
  if (!saved) {
    return []
  }

  try {
    const parsed = JSON.parse(saved)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    localStorage.removeItem(GUEST_CART_KEY)
    return []
  }
}

const writeStorage = (items) => {
  if (!items.length) {
    localStorage.removeItem(GUEST_CART_KEY)
    return
  }

  localStorage.setItem(GUEST_CART_KEY, JSON.stringify(items))
}

const resolveProductId = (product) => product?.id ?? product?.Id
const resolveProductName = (product) => product?.name ?? product?.Name ?? 'Sản phẩm'
const resolveProductImage = (product) => product?.image ?? product?.Image ?? ''
const resolveProductPrice = (product) => Number(product?.price ?? product?.Price ?? 0)

export const loadGuestCart = () => readStorage()

export const getGuestCartCount = () =>
  readStorage().reduce((sum, item) => sum + (Number(item.quantity) || 0), 0)

export const addGuestCartItem = (product, quantity = 1) => {
  const productId = resolveProductId(product)
  if (productId == null) {
    return []
  }

  const currentItems = readStorage()
  const safeQuantity = Math.max(1, Number(quantity) || 1)
  const price = resolveProductPrice(product)
  const existingIndex = currentItems.findIndex((item) => String(item.productId) === String(productId))

  if (existingIndex >= 0) {
    const mergedQuantity = (Number(currentItems[existingIndex].quantity) || 0) + safeQuantity
    currentItems[existingIndex] = {
      ...currentItems[existingIndex],
      quantity: mergedQuantity,
      subTotal: price * mergedQuantity,
    }
  } else {
    currentItems.push({
      id: Date.now(),
      productId,
      productName: resolveProductName(product),
      productImage: resolveProductImage(product),
      price,
      quantity: safeQuantity,
      subTotal: price * safeQuantity,
    })
  }

  writeStorage(currentItems)
  return currentItems
}

export const updateGuestCartQuantity = (cartItemId, quantity) => {
  const safeQuantity = Number(quantity) || 0
  const currentItems = readStorage()
  const nextItems = currentItems
    .map((item) => {
      if (String(item.id) !== String(cartItemId)) {
        return item
      }

      if (safeQuantity <= 0) {
        return null
      }

      return {
        ...item,
        quantity: safeQuantity,
        subTotal: (Number(item.price) || 0) * safeQuantity,
      }
    })
    .filter(Boolean)

  writeStorage(nextItems)
  return nextItems
}

export const removeGuestCartItem = (cartItemId) => {
  const nextItems = readStorage().filter((item) => String(item.id) !== String(cartItemId))
  writeStorage(nextItems)
  return nextItems
}

export const clearGuestCart = () => {
  localStorage.removeItem(GUEST_CART_KEY)
}

export const mergeGuestCartIntoBackend = async (userId, api) => {
  const guestItems = readStorage()
  if (!guestItems.length) {
    return
  }

  await Promise.all(
    guestItems.map((item) =>
      api.post(`/api/cart/add?userId=${userId}`, {
        productId: item.productId,
        quantity: item.quantity,
      })
    )
  )

  clearGuestCart()
}