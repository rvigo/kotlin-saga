package com.rvigo.saga.lib.domain.message.event

import java.time.LocalDateTime
import java.util.UUID

interface Event {

    val emittedOn: LocalDateTime

    val eventId: UUID

    val aggregateId: UUID
}
