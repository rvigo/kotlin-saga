package com.rvigo.saga.cart.domain.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.NamedSubgraph
import jakarta.persistence.Table
import java.util.UUID

typealias Quantity = Int

@Entity
@Table(name = "cart")
@NamedEntityGraph(
    name = "Cart.FullLoad",
    includeAllAttributes = true,
    attributeNodes = [
        NamedAttributeNode("cartItems", subgraph = "Items.FullLoad"),
    ],
    subgraphs = [
        NamedSubgraph(name = "Items.FullLoad", attributeNodes = [
            NamedAttributeNode("product", subgraph = "Product.FullLoad"),
            NamedAttributeNode("quantity"),
        ]),
        NamedSubgraph(name = "Product.FullLoad", attributeNodes = [
            NamedAttributeNode("name"),
            NamedAttributeNode("price")
        ])
    ],
)
data class Cart(
    @Id
    val id: UUID
) {

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(name = "cart_cart_item",
        joinColumns = [
            JoinColumn(name = "cart_id", referencedColumnName = "id")
        ], inverseJoinColumns = [
        JoinColumn(name = "cart_item_id", referencedColumnName = "id")
    ])
    var cartItems: List<CartItem> = emptyList()

    fun addItem(newCartItem: CartItem): Cart {
        val updatedItems = cartItems.toMutableList()
        updatedItems.find {
            it.product.id == newCartItem.product.id
        }?.increaseQuantity(newCartItem.quantity)
            ?: run {
                updatedItems.add(newCartItem)
            }

        return Cart(id, updatedItems)
    }

    fun removeItem(cartItem: CartItem): Cart {
        val updatedItems = cartItems.toMutableList()
        updatedItems.find {
            it.product.id == cartItem.product.id
        }?.decreaseQuantity(cartItem.quantity)
            ?: run {
                updatedItems.add(cartItem)
            }

        return Cart(id, updatedItems)
    }

    fun empty(): Cart {
        cartItems = emptyList()
        return this
    }

    fun cartItemDetails() = cartItems.map(::CartItemDetail)

    constructor(id: UUID, cartItems: List<CartItem>) : this(id) {
        this.cartItems = cartItems
    }
}

