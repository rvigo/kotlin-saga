package com.rvigo.saga.external.tripService.application.listeners.commands

import com.rvigo.saga.external.tripService.domain.models.Trip
import com.rvigo.saga.infra.aws.SnsEvent
import com.rvigo.saga.infra.events.BaseResponse
import java.util.UUID


data class CreateTripResponse(
    val sagaId: UUID,
    val cpf: String,
    val responseStatus: Status,
    val tripId: UUID? = null,
    val tripStatus: Trip.TripStatus? = null
) : SnsEvent.SnsEventBody, BaseResponse {
    override fun isSuccess() = this.responseStatus == Status.SUCCESS
    enum class Status { SUCCESS, FAILURE }
}

