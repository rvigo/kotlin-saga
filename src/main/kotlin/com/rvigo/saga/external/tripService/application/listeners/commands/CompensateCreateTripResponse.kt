package com.rvigo.saga.external.tripService.application.listeners.commands

import java.util.UUID

data class CompensateCreateTripResponse(val sagaId: UUID, val status: Status) {
    enum class Status { SUCCESS, FAILURE }
}
