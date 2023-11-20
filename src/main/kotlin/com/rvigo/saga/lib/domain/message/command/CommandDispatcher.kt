package com.rvigo.saga.lib.domain.message.command

import com.rvigo.saga.cart.infra.logger.logger
import kotlin.reflect.KClass

abstract class CommandDispatcher {

    private val logger by logger()

    private val handlers: MutableMap<KClass<out Command>, CommandHandler<Command>> = mutableMapOf()

    fun <T : Command> registerHandler(command: KClass<T>, handler: CommandHandler<Command>) {
        logger.info("registering handler ${handler.javaClass.simpleName} to command ${command.simpleName}")
        handlers[command] = handler
    }

    fun <T : Command> dispatch(command: T) {
        val handler = handlers[command::class]
        handler?.let {
            logger.info("dispatching command to ${it.javaClass.simpleName}")
            it.handle(command)
        }
    }
}
