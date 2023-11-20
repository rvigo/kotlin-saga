package com.rvigo.saga.lib.domain.message.command

import kotlin.reflect.KClass

interface CommandHandler<T : Command> {

    fun type(): KClass<T>

    fun handle(command: T)
}
