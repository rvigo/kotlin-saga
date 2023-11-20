package com.rvigo.saga.cart.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "product")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
) {

    @Column
    lateinit var name: String
        private set

    @Column
    var price: Double = 0.0
        private set

    constructor(
        id: Int? = null,
        name: String,
        price: Double
    ) : this(id) {
        this.name = name
        this.price = price
    }
}
