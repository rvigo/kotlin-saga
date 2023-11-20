package com.rvigo.saga.lib.infra.initializer

import com.rvigo.saga.lib.domain.message.command.Command
import com.rvigo.saga.lib.domain.message.command.CommandDispatcher
import com.rvigo.saga.lib.domain.message.command.CommandHandler
import com.rvigo.saga.lib.domain.message.command.annotation.Handler
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class CommandHandlerInitializer(private val commandDispatcher: CommandDispatcher) : ApplicationContextAware {

    @Suppress("UNCHECKED_CAST")
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        val handlersMap = applicationContext.getBeansWithAnnotation(Handler::class.java)

        handlersMap.forEach { (_, handler) ->
            val handlerInstance = applicationContext.getBean(handler.javaClass) as CommandHandler<Command>

            commandDispatcher.registerHandler(handlerInstance.type(), handlerInstance)
        }
    }
}
