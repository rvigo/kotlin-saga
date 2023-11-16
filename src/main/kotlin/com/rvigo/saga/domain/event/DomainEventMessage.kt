package com.rvigo.saga.domain.event

import com.rvigo.saga.domain.messaging.Message
import java.time.LocalDateTime

interface DomainEventMessage : DomainEvent, Message {

    val createdAt: LocalDateTime
}
