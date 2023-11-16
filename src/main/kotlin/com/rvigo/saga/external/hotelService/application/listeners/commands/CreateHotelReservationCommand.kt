package com.rvigo.saga.external.hotelService.application.listeners.commands

import com.rvigo.saga.domain.command.AbstractCommandMessage
import java.util.UUID

data class CreateHotelReservationCommand(val sagaId: UUID, val cpf: String) : AbstractCommandMessage(sagaId)
