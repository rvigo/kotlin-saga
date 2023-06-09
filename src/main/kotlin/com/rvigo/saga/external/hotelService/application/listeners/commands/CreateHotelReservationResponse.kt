package com.rvigo.saga.external.hotelService.application.listeners.commands

import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import com.rvigo.saga.infra.events.BaseResponse
import java.util.UUID


data class CreateHotelReservationResponse(
    val sagaId: UUID,
    val status: BaseResponse.Status,
    val reservationId: UUID? = null,
    val reservationStatus: HotelReservation.Status? = null
) : BaseResponse {
    override fun isSuccess() = this.status == BaseResponse.Status.SUCCESS
}

