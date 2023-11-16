package com.rvigo.saga.external.tripService.application.listeners.commands

import com.rvigo.saga.domain.command.AbstractCommandMessage
import com.rvigo.saga.domain.command.CommandMessage.Companion.EVENT_TYPE_HEADER
import com.rvigo.saga.domain.event.SagaEventType
import java.util.UUID

data class CreateTripCommand(
    val sagaId: UUID,
    val cpf: String,
) : AbstractCommandMessage(sagaId, mapOf(EVENT_TYPE_HEADER to SagaEventType.CREATE_TRIP.name))
