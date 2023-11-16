package com.rvigo.saga.external.flightService.application.listeners.commands

import com.rvigo.saga.domain.command.AbstractCommandMessage
import com.rvigo.saga.domain.command.CommandMessage.Companion.EVENT_TYPE_HEADER
import com.rvigo.saga.domain.event.SagaEventType
import java.util.UUID

data class CreateFlightReservationCommand(
    val sagaId: UUID,
    val cpf: String,
) : AbstractCommandMessage(sagaId, mapOf(EVENT_TYPE_HEADER to SagaEventType.CREATE_FLIGHT_RESERVATION.name))
