package com.rvigo.saga.external.hotelService.application.listeners.commands

import com.rvigo.saga.infra.aws.SnsEvent
import java.util.UUID

data class CreateHotelReservationCommand(val sagaId: UUID, val cpf: String) : SnsEvent.SnsEventBody
