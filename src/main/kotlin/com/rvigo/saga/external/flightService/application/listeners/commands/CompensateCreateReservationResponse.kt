package com.rvigo.saga.external.flightService.application.listeners.commands

import com.rvigo.saga.domain.command.AbstractCommandResponse
import com.rvigo.saga.domain.command.CommandResponse
import java.util.UUID

data class CompensateCreateReservationResponse(
    val sagaId: UUID,
    val responseStatus: CommandResponse.Status
) : AbstractCommandResponse(sagaId, status = responseStatus)
