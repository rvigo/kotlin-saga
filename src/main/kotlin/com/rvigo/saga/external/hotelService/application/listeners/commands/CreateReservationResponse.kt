package com.rvigo.saga.external.hotelService.application.listeners.commands

import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import java.util.UUID


data class CreateReservationResponse(
    val sagaId: UUID,
    val status: Status,
    val reservationId: UUID? = null,
    val reservationStatus: HotelReservation.Status? = null
) {
    fun isSuccess() = this.status == Status.SUCCESS
    enum class Status { SUCCESS, FAILURE }
}

fun <T> CreateReservationResponse.ifSuccess(block: (CreateReservationResponse) -> T) =
    if (isSuccess()) block(this).let { this } else this

fun <T> CreateReservationResponse.ifFailure(block: (CreateReservationResponse) -> T) =
    if (!isSuccess()) block(this).let { this } else this
