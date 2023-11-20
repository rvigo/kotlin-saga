package com.rvigo.saga.cart.builder

import com.rvigo.saga.cart.domain.model.Product

class ProductBuilder {

    var id: Int = 1

    var name: String = "product"

    var price: Double = 1.99

    companion object {

        fun build(block: ProductBuilder.() -> Unit = {}): Product = ProductBuilder().apply(block).build()
    }

    private fun build() = Product(id, name, price)
}
