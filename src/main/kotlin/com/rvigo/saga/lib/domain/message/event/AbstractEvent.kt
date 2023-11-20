package com.rvigo.saga.lib.domain.message.event

import java.time.LocalDateTime
import java.util.UUID

abstract class AbstractEvent(override val aggregateId: UUID) : Event {

    override val emittedOn: LocalDateTime = LocalDateTime.now()

    override val eventId: UUID = UUID.randomUUID()
}
