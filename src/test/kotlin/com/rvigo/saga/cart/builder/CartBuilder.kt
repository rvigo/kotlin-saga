package com.rvigo.saga.cart.builder

import com.rvigo.saga.cart.domain.model.Cart
import com.rvigo.saga.cart.domain.model.CartItem
import java.util.UUID

class CartBuilder {

    var id: UUID = UUID.randomUUID()

    var cartItems: List<CartItem> = listOf(CartItemBuilder.build())

    companion object {

        fun build(block: CartBuilder.() -> Unit = {}): Cart = CartBuilder().apply(block).build()
    }

    private fun build() = Cart(id, cartItems)
}
