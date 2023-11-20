package com.rvigo.saga.lib.domain.message.command.annotation

import org.springframework.stereotype.Component

/**
 * Used for automatically registering implementations of the
 * [@CommandHandler](com.rvigo.saga.lib.domain.message.command.CommandHandler) interface
 *
 * @see com.rvigo.saga.lib.domain.message.command.CommandHandler
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class Handler
