package com.rvigo.saga.domain.command

import com.rvigo.saga.domain.event.DomainEventMessage

interface CommandMessage : DomainEventMessage {

    companion object {
        const val EVENT_TYPE_HEADER = "EVENT_TYPE"
    }
}
