import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface CartItem {
  id: number
  title: string
  teacher: string
  price: number
  image: string
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const discountCode = ref<string>('')
  const discountPercent = ref<number>(0)

  // LocalStorage key
  const STORAGE_KEY = 'englishpro_cart'
  const DISCOUNT_KEY = 'englishpro_discount'

  // Initialize cart from localStorage
  const initializeCart = () => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY)
      const savedDiscount = localStorage.getItem(DISCOUNT_KEY)
      if (saved) {
        items.value = JSON.parse(saved)
      }
      if (savedDiscount) {
        const discount = JSON.parse(savedDiscount)
        discountCode.value = discount.code
        discountPercent.value = discount.percent
      }
    } catch (error) {
      console.error('Error initializing cart:', error)
    }
  }

  // Save cart to localStorage
  const saveToLocalStorage = () => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(items.value))
      localStorage.setItem(
        DISCOUNT_KEY,
        JSON.stringify({
          code: discountCode.value,
          percent: discountPercent.value,
        }),
      )
    } catch (error) {
      console.error('Error saving cart to localStorage:', error)
    }
  }

  // Add item to cart
  const addToCart = (item: CartItem) => {
    const exists = items.value.find((i) => i.id === item.id)
    if (!exists) {
      items.value.push(item)
      saveToLocalStorage()
    }
  }

  // Remove item from cart
  const removeFromCart = (id: number) => {
    items.value = items.value.filter((item) => item.id !== id)
    if (items.value.length === 0) {
      discountCode.value = ''
      discountPercent.value = 0
    }
    saveToLocalStorage()
  }

  // Clear cart
  const clearCart = () => {
    items.value = []
    discountCode.value = ''
    discountPercent.value = 0
    saveToLocalStorage()
  }

  // Apply discount code
  const applyDiscount = (code: string, percent: number) => {
    discountCode.value = code
    discountPercent.value = percent
    saveToLocalStorage()
  }

  // Remove discount
  const removeDiscount = () => {
    discountCode.value = ''
    discountPercent.value = 0
    saveToLocalStorage()
  }

  // Computed properties
  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price, 0)
  })

  const discountAmount = computed(() => {
    return Math.round((totalPrice.value * discountPercent.value) / 100)
  })

  const finalPrice = computed(() => {
    return totalPrice.value - discountAmount.value
  })

  const itemCount = computed(() => {
    return items.value.length
  })

  const isEmpty = computed(() => {
    return items.value.length === 0
  })

  return {
    items,
    discountCode,
    discountPercent,
    totalPrice,
    discountAmount,
    finalPrice,
    itemCount,
    isEmpty,
    initializeCart,
    addToCart,
    removeFromCart,
    clearCart,
    applyDiscount,
    removeDiscount,
  }
})
