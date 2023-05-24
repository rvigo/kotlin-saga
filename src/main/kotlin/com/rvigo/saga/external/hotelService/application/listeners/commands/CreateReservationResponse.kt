package com.rvigo.saga.external.hotelService.application.listeners.commands

import java.util.UUID

data class CreateReservationResponse(val sagaId: UUID, val reservationId: UUID? = null, val status: Status) {

    fun isSuccess() = this.status == Status.SUCCESS
    enum class Status {
        SUCCESS, FAILURE
    }
}
