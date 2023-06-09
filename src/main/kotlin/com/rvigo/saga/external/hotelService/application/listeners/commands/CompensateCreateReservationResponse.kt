package com.rvigo.saga.external.hotelService.application.listeners.commands

import java.util.UUID

data class CompensateCreateReservationResponse(val sagaId: UUID, val status: Status) {
    enum class Status {
        SUCCESS, FAILURE
    }
}
