package com.rvigo.saga.external.hotelService.application.listeners.commands

import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import java.util.UUID

data class CreateReservationResponse(val sagaId: UUID,
                                     val status: Status,
                                     val reservationId: UUID? = null,
                                     val reservationStatus: HotelReservation.Status? = null) {
    fun isSuccess() = this.status == Status.SUCCESS
    enum class Status {
        SUCCESS, FAILURE
    }
}
