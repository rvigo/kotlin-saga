package com.rvigo.saga.cart.application

import com.rvigo.saga.cart.domain.command.AddItemCommand
import com.rvigo.saga.cart.domain.command.CreateCartCommand
import com.rvigo.saga.cart.domain.command.RemoveItemCommand
import com.rvigo.saga.cart.domain.model.Product
import com.rvigo.saga.cart.infra.logger.logger
import com.rvigo.saga.cart.infra.repository.ProductRepository
import com.rvigo.saga.cart.infra.repository.withinTransaction
import com.rvigo.saga.lib.domain.message.command.CommandDispatcher
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class CartController(
    private val commandDispatcher: CommandDispatcher,
    val productRepository: ProductRepository,
) {

    private val logger by logger()

    @PostMapping
    fun create() {
        val cartId = UUID.randomUUID()
        logger.info("creating a new cart with id $cartId")

        commandDispatcher.dispatch(CreateCartCommand(cartId))

        // creates a new item
        withinTransaction {
            val product = Product(name = "monster", price = 7.99)

            productRepository.save(product).also {
                logger.info("item: $it")
            }
        }
    }

    @PostMapping("item")
    fun addItem(@RequestBody addItemRequest: AddItemRequest) {
        logger.info("adding an item to cart with id ${addItemRequest.cartId}")
        commandDispatcher.dispatch(
            AddItemCommand(
                addItemRequest.cartId,
                addItemRequest.itemId,
                addItemRequest.quantity
            )
        )
    }

    @DeleteMapping("item")
    fun removeItem(@RequestBody removeItemRequest: RemoveItemRequest) {
        logger.info("removing an item from cart with id ${removeItemRequest.cartId}")
        commandDispatcher.dispatch(
            RemoveItemCommand(
                removeItemRequest.cartId,
                removeItemRequest.itemId,
                removeItemRequest.quantity
            )
        )
    }

    @PostMapping("clear/{cartId}")
    fun clearCart(@PathVariable cartId: UUID) {
        //
    }
}

