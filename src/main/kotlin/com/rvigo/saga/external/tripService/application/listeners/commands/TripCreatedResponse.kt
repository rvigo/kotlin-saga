package com.rvigo.saga.external.tripService.application.listeners.commands

import com.rvigo.saga.external.tripService.domain.models.Trip
import java.util.UUID


data class TripCreatedResponse(
    val sagaId: UUID,
    val cpf: String,
    val responseStatus: Status,
    val tripId: UUID? = null,
    val tripStatus: Trip.TripStatus? = null
) {
    fun isSuccess() = this.responseStatus == Status.SUCCESS
    enum class Status { SUCCESS, FAILURE }
}

fun <T> TripCreatedResponse.ifSuccess(block: (TripCreatedResponse) -> T) =
    if (isSuccess()) block(this).let { this } else this

fun <T> TripCreatedResponse.ifFailure(block: (TripCreatedResponse) -> T) =
    if (!isSuccess()) block(this).let { this } else this
