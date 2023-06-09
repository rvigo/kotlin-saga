package com.rvigo.saga.infra.events

interface BaseResponse : BaseEvent {
    fun isSuccess(): Boolean
    enum class Status { SUCCESS, FAILURE }
}

fun <T : BaseResponse, R> T.ifSuccess(block: (T) -> R) =
    if (this.isSuccess()) block(this).let { this } else this

fun <T : BaseResponse, R> T.ifFailure(block: (T) -> R) =
    if (!isSuccess()) block(this).let { this } else this
