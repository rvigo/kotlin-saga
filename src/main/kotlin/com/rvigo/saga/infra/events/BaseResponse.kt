package com.rvigo.saga.infra.events

interface BaseResponse : BaseEvent {
    fun isSuccess(): Boolean
    enum class Status { SUCCESS, FAILURE }
}

fun <T : BaseResponse, R> T.onSuccess(block: (T) -> R) =
    if (this.isSuccess()) block(this).let { this } else this

fun <T : BaseResponse, R> T.onFailure(block: (T) -> R) =
    if (!isSuccess()) block(this).let { this } else this
