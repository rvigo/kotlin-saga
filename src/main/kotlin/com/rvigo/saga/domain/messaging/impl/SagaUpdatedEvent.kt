package com.rvigo.saga.domain.messaging.impl

import com.rvigo.saga.domain.event.DomainEventMessage
import java.time.LocalDateTime
import java.util.UUID

data class SagaUpdatedEvent(
    val sagaId: UUID,
    override val body: ParticipantInfo,
    override val attributes: Map<String, Any> = mapOf(),
) : DomainEventMessage {

    override val createdAt: LocalDateTime = LocalDateTime.now()

    override val eventId: UUID = UUID.randomUUID()

    override val aggregateRootId: UUID = sagaId
}

