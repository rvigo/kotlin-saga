package com.rvigo.saga.domain.proxy

import com.rvigo.saga.domain.messaging.Message

interface ParticipantProxy {

    fun create(message: Message)

    fun compensate(message: Message)

    fun confirm(message: Message)
}
