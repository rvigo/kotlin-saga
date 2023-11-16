package com.rvigo.saga.domain.event

import java.util.UUID

interface DomainEvent {

    val eventId: UUID

    val aggregateRootId: UUID?
}
