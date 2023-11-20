package com.rvigo.saga.cart.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table


@Entity
@Table(name = "cart_item")
data class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
) {

    @ManyToOne
    @JoinColumn(name = "product_id")
    lateinit var product: Product
        private set

    @Column
    var quantity: Quantity = 0
        private set

    fun increaseQuantity(quantity: Quantity): CartItem {
        this.quantity += quantity
        return this
    }

    fun decreaseQuantity(quantity: Quantity): CartItem {
        this.quantity -= quantity
        return this
    }

    fun totalPrice(): Double = product.price * quantity

    constructor(id: Int? = null, product: Product, quantity: Quantity) : this(id) {
        this.product = product
        this.quantity = quantity
    }
}
