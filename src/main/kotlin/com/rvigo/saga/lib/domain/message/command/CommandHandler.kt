package com.rvigo.saga.lib.domain.message.command

abstract class CommandHandler {

    abstract fun handle(command: Command)
}
