package com.rvigo.saga.domain.command

import java.time.LocalDateTime
import java.util.UUID

abstract class AbstractCommandResponse(
    override var aggregateRootId: UUID?,
    override val status: CommandResponse.Status,
    override val attributes: Map<String, Any> = mapOf(),
    override val body: MessageBody? = null
) : CommandResponse {

    override val eventId: UUID = UUID.randomUUID()

    override var createdAt: LocalDateTime = LocalDateTime.now()
}
