package com.rvigo.saga.cart.domain.command.handler

import com.rvigo.saga.cart.domain.command.CreateCartCommand
import com.rvigo.saga.cart.domain.service.CartService
import com.rvigo.saga.cart.infra.logger.logger
import com.rvigo.saga.lib.domain.message.command.CommandHandler
import com.rvigo.saga.lib.domain.message.command.annotation.Handler
import kotlin.reflect.KClass

@Handler
class CreateCartCommandHandler(private val service: CartService) : CommandHandler<CreateCartCommand> {

    private val logger by logger()

    override fun handle(command: CreateCartCommand) {
        with(command) {
            logger.info("creating cart with id $cartId")
            service.create(cartId)
        }
    }

    override fun type(): KClass<CreateCartCommand> = CreateCartCommand::class
}
