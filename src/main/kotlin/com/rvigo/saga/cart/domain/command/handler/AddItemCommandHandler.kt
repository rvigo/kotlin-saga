package com.rvigo.saga.cart.domain.command.handler

import com.rvigo.saga.cart.domain.command.AddItemCommand
import com.rvigo.saga.cart.domain.service.CartService
import com.rvigo.saga.cart.infra.logger.logger
import com.rvigo.saga.lib.domain.message.command.CommandHandler
import com.rvigo.saga.lib.domain.message.command.annotation.Handler
import kotlin.reflect.KClass

@Handler
class AddItemCommandHandler(private val service: CartService) : CommandHandler<AddItemCommand> {

    private val logger by logger()

    override fun handle(command: AddItemCommand) {
        with(command) {
            logger.info("adding item $itemId to cart with id: $cartId")
            service.addItem(cartId, itemId, quantity)
        }
    }

    override fun type(): KClass<AddItemCommand> = AddItemCommand::class
}
