package com.rvigo.saga.cart.domain.command.handler

import com.rvigo.saga.cart.domain.command.AddItemCommand
import com.rvigo.saga.cart.domain.service.CartService
import com.rvigo.saga.cart.infra.logger.logger
import com.rvigo.saga.lib.domain.message.command.Command
import com.rvigo.saga.lib.domain.message.command.CommandHandler
import org.springframework.stereotype.Component

@Component
class RemoveItemCommandHandler(private val service: CartService) : CommandHandler() {

    private val logger by logger()

    override fun handle(command: Command) {
        with(command as AddItemCommand) {
            logger.info("removing item $itemId to cart with id: $cartId")
            service.removeItem(cartId, itemId, quantity)
        }
    }
}
