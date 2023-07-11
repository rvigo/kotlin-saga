package com.rvigo.saga.external.flightService.application.listeners.commands

import com.rvigo.saga.external.flightService.domain.models.FlightReservation
import com.rvigo.saga.infra.aws.SnsEvent
import com.rvigo.saga.infra.events.BaseResponse
import java.util.UUID


data class CreateFlightReservationResponse(
    val sagaId: UUID,
    val status: BaseResponse.Status,
    val reservationId: UUID? = null,
    val reservationStatus: FlightReservation.Status? = null
) : BaseResponse, SnsEvent.SnsEventBody {
    override fun isSuccess() = this.status == BaseResponse.Status.SUCCESS
}
