package com.rvigo.saga.external.hotelService.application.listeners.commands

import com.rvigo.saga.domain.command.AbstractCommandResponse
import com.rvigo.saga.domain.command.CommandMessage.Companion.EVENT_TYPE_HEADER
import com.rvigo.saga.domain.command.CommandResponse
import com.rvigo.saga.domain.command.MessageBody
import com.rvigo.saga.domain.event.SagaEventType
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import java.util.UUID

data class CreateHotelReservationResponse(
    val sagaId: UUID,
    val responseStatus: CommandResponse.Status,
    override val body: CreateHotelReservationBody
) : AbstractCommandResponse(
    sagaId,
    status = responseStatus,
    attributes = mapOf(EVENT_TYPE_HEADER to SagaEventType.CREATE_HOTEL_RESERVATION_RESPONSE.name)
)

data class CreateHotelReservationBody(
    val reservationId: UUID? = null,
    val reservationStatus: HotelReservation.Status?
) : MessageBody
