package com.rvigo.saga.domain.messaging.publisher

import com.rvigo.saga.domain.messaging.Message

interface MessagePublisher {

    fun publish(message: Message, destination: String)
}
