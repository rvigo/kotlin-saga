package com.rvigo.saga.domain.messaging

import com.rvigo.saga.domain.command.MessageBody

interface Message {

    val attributes: Map<String, Any>

    val body: MessageBody?
}
