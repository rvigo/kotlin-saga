package com.rvigo.saga.domain.command

import com.rvigo.saga.domain.event.DomainEventMessage

interface CommandResponse : DomainEventMessage {

    val status: Status

    fun isSuccess(): Boolean = status == Status.SUCCESS

    enum class Status { SUCCESS, FAILURE }
}

fun <T : CommandResponse, R> T.onSuccess(block: (T) -> R) =
    if (this.isSuccess()) block(this).let { this } else this

fun <T : CommandResponse, R> T.onFailure(block: (T) -> R) =
    if (!isSuccess()) block(this).let { this } else this
