package com.rvigo.saga.cart.domain.service

import com.rvigo.saga.cart.domain.event.CartCreatedEvent
import com.rvigo.saga.cart.domain.event.CartEmptyEvent
import com.rvigo.saga.cart.domain.event.ItemAddedEvent
import com.rvigo.saga.cart.domain.event.ItemRemovedEvent
import com.rvigo.saga.cart.domain.model.Cart
import com.rvigo.saga.cart.domain.model.CartItem
import com.rvigo.saga.cart.domain.model.CartItemDetail
import com.rvigo.saga.cart.domain.model.Product
import com.rvigo.saga.cart.domain.model.Quantity
import com.rvigo.saga.cart.infra.logger.logger
import com.rvigo.saga.cart.infra.repository.CartRepository
import com.rvigo.saga.cart.infra.repository.withinTransaction
import com.rvigo.saga.lib.domain.message.event.EventDispatcher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CartService(
    private val repository: CartRepository,
    private val eventDispatcher: EventDispatcher
) {

    private val logger by logger()

    fun create(id: UUID) {
        val cart = Cart(id)
        withinTransaction {
            repository.save(cart)
        }.also {
            logger.info("created cart: $it")
            eventDispatcher.emit(CartCreatedEvent(id))
        }
    }

    fun addItem(cartId: UUID, productId: Int, quantity: Quantity) {
        val cart = repository.findByIdOrNull(cartId)
            ?: throw RuntimeException("cart not found with Id: $cartId")

        val updatedCart = cart.addItem(
            CartItem(
                product = Product(productId),
                quantity = quantity
            )
        )

        withinTransaction {
            repository.save(updatedCart)
        }.also {
            logger.info("updated cart with id ${it.id}")
            eventDispatcher.emit(
                ItemAddedEvent(
                    cartId,
                    productId,
                    quantity
                )
            )
        }
    }

    fun removeItem(cartId: UUID, productId: Int, quantity: Quantity) {
        val cart = repository.findByIdOrNull(cartId)
            ?: throw RuntimeException("cart not found with Id: $cartId")

        val updatedCart = cart.removeItem(
            CartItem(
                product = Product(productId),
                quantity = quantity
            )
        )

        withinTransaction {
            repository.save(updatedCart)
        }.also {
            logger.info("updated cart with id ${it.id}")
            eventDispatcher.emit(
                ItemRemovedEvent(
                    cartId,
                    productId,
                    quantity
                )
            )
        }
    }

    fun emptyCart(cartId: UUID) {
        val cart = repository.findByIdOrNull(cartId)
            ?: throw RuntimeException("cart not found with Id: $cartId")

        val updatedCart = cart.empty()

        withinTransaction {
            repository.save(updatedCart)
        }.also {
            logger.info("updated cart with id ${it.id}")
            eventDispatcher.emit(
                CartEmptyEvent(cartId)
            )
        }
    }

    fun getDetails(foodCartId: UUID): List<CartItemDetail> {
        val fullLoad = repository.fullLoad(foodCartId)

        return fullLoad?.cartItemDetails()
            ?: throw RuntimeException("cart not found")
    }
}
