package com.rvigo.saga.external.tripService.application.listeners.commands

import com.rvigo.saga.domain.command.AbstractCommandResponse
import com.rvigo.saga.domain.command.CommandMessage.Companion.EVENT_TYPE_HEADER
import com.rvigo.saga.domain.command.CommandResponse
import com.rvigo.saga.domain.event.SagaEventType
import java.util.UUID

data class CreateTripResponse(
    val sagaId: UUID,
    val cpf: String,
    val responseStatus: CommandResponse.Status,
    override val body: TripResponseBody,
) : AbstractCommandResponse(
    sagaId,
    status = responseStatus,
    attributes = mapOf(EVENT_TYPE_HEADER to SagaEventType.CREATE_TRIP_RESPONSE)
)
