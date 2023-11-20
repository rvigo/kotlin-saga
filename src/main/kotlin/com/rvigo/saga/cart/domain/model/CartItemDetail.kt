package com.rvigo.saga.cart.domain.model

data class CartItemDetail(
    val cartItemName: String,
    val quantity: Quantity,
    val totalPrice: Double
) {
    constructor(cartItem: CartItem) : this(
        cartItem.product.name,
        cartItem.quantity,
        cartItem.totalPrice()
    )
}
