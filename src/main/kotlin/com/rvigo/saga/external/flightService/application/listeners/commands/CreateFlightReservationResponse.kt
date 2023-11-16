package com.rvigo.saga.external.flightService.application.listeners.commands

import com.rvigo.saga.domain.command.AbstractCommandResponse
import com.rvigo.saga.domain.command.CommandResponse
import com.rvigo.saga.domain.command.CommandResponseBody
import com.rvigo.saga.external.flightService.domain.models.FlightReservation
import java.util.UUID


data class CreateFlightReservationResponse(
    val sagaId: UUID,
    val responseStatus: CommandResponse.Status,
    override val body: FlightReservationResponseBody? = null,
    override val attributes: Map<String, Any> = mapOf(),
) : AbstractCommandResponse(sagaId, status = responseStatus)

data class FlightReservationResponseBody(
    val reservationId: UUID? = null,
    val reservationStatus: FlightReservation.Status? = null,
) : CommandResponseBody
