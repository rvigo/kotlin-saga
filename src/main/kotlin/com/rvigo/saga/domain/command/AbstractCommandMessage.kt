package com.rvigo.saga.domain.command

import java.time.LocalDateTime
import java.util.UUID

abstract class AbstractCommandMessage(
    override val aggregateRootId: UUID? = null,
    override val attributes: Map<String, Any> = mapOf(),
    override val body: MessageBody? = null
) : CommandMessage {

    override var eventId: UUID = UUID.randomUUID()

    override var createdAt: LocalDateTime = LocalDateTime.now()
}
