package com.rvigo.saga.external.flightService.application.listeners.commands

import com.rvigo.saga.infra.events.BaseEvent
import java.util.UUID


data class CompensateCreateReservationResponse(val sagaId: UUID, val status: Status) : BaseEvent {
    enum class Status { SUCCESS, FAILURE }
}
