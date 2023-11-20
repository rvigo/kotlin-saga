package com.rvigo.saga.cart.domain.command.handler

import com.rvigo.saga.cart.domain.command.EmptyCartCommand
import com.rvigo.saga.cart.domain.service.CartService
import com.rvigo.saga.cart.infra.logger.logger
import com.rvigo.saga.lib.domain.message.command.CommandHandler
import com.rvigo.saga.lib.domain.message.command.annotation.Handler
import kotlin.reflect.KClass

@Handler
class EmptyCartCommandHandler(private val service: CartService) : CommandHandler<EmptyCartCommand> {

    private val logger by logger()

    override fun handle(command: EmptyCartCommand) {
        with(command) {
            logger.info("removing all items from cart with id $cartId")

            service.emptyCart(cartId)
        }
    }

    override fun type(): KClass<EmptyCartCommand> = EmptyCartCommand::class
}
