package com.rvigo.saga.cart.builder

import com.rvigo.saga.cart.domain.model.CartItem
import com.rvigo.saga.cart.domain.model.Product
import com.rvigo.saga.cart.domain.model.Quantity

class CartItemBuilder {

    var id: Int = 1

    var product: Product = ProductBuilder.build()

    var quantity: Quantity = 1

    companion object {

        fun build(block: CartItemBuilder.() -> Unit = {}): CartItem = CartItemBuilder().apply(block).build()
    }

    private fun build() = CartItem(id, product, quantity)
}
