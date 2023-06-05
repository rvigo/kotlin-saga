package com.rvigo.saga.external.flightService.application.listeners.commands

import com.rvigo.saga.external.flightService.domain.models.FlightReservation
import java.util.UUID


data class CreateFlightReservationResponse(
    val sagaId: UUID,
    val status: Status,
    val reservationId: UUID? = null,
    val reservationStatus: FlightReservation.Status? = null
) {
    fun isSuccess() = this.status == Status.SUCCESS
    enum class Status { SUCCESS, FAILURE }
}

fun <T> CreateFlightReservationResponse.ifSuccess(block: (CreateFlightReservationResponse) -> T) =
    if (isSuccess()) block(this).let { this } else this

fun <T> CreateFlightReservationResponse.ifFailure(block: (CreateFlightReservationResponse) -> T) =
    if (!isSuccess()) block(this).let { this } else this
