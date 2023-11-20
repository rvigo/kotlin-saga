package com.rvigo.saga.lib.domain.message.event

interface EventDispatcher {

    fun <T> emit(event: T)
}
