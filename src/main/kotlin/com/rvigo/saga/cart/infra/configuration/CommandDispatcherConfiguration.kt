package com.rvigo.saga.cart.infra.configuration

import com.rvigo.saga.cart.domain.command.AddItemCommand
import com.rvigo.saga.cart.domain.command.CreateCartCommand
import com.rvigo.saga.cart.domain.command.EmptyCartCommand
import com.rvigo.saga.cart.domain.command.RemoveItemCommand
import com.rvigo.saga.cart.domain.command.handler.AddItemCommandHandler
import com.rvigo.saga.cart.domain.command.handler.CreateCartCommandHandler
import com.rvigo.saga.cart.domain.command.handler.EmptyCartCommandHandler
import com.rvigo.saga.cart.domain.command.handler.RemoveItemCommandHandler
import com.rvigo.saga.lib.domain.message.command.CommandDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommandDispatcherConfiguration {

    @Bean
    fun commandDispatcher(
        commandDispatcher: CommandDispatcher,
        createCartCommandHandler: CreateCartCommandHandler,
        addItemCommandHandler: AddItemCommandHandler,
        removeItemCommandHandler: RemoveItemCommandHandler,
        emptyCartCommandHandler: EmptyCartCommandHandler
    ): CommandDispatcher {
        commandDispatcher.registerHandler(CreateCartCommand::class, createCartCommandHandler)
        commandDispatcher.registerHandler(AddItemCommand::class, addItemCommandHandler)
        commandDispatcher.registerHandler(RemoveItemCommand::class, addItemCommandHandler)
        commandDispatcher.registerHandler(EmptyCartCommand::class, emptyCartCommandHandler)

        return commandDispatcher
    }
}
