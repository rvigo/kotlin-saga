package com.rvigo.saga.external.hotelService.application.listeners.commands

import java.util.UUID

data class CreateReservationCommand(val sagaId: UUID, val cpf: String)
