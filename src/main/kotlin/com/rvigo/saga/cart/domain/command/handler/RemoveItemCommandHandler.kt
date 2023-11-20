package com.rvigo.saga.cart.domain.command.handler

import com.rvigo.saga.cart.domain.command.RemoveItemCommand
import com.rvigo.saga.cart.domain.service.CartService
import com.rvigo.saga.cart.infra.logger.logger
import com.rvigo.saga.lib.domain.message.command.CommandHandler
import com.rvigo.saga.lib.domain.message.command.annotation.Handler
import kotlin.reflect.KClass

@Handler
class RemoveItemCommandHandler(private val service: CartService) : CommandHandler<RemoveItemCommand> {

    private val logger by logger()

    override fun handle(command: RemoveItemCommand) {
        with(command) {
            logger.info("removing item $itemId to cart with id: $cartId")
            service.removeItem(cartId, itemId, quantity)
        }
    }

    override fun type(): KClass<RemoveItemCommand> = RemoveItemCommand::class
}
