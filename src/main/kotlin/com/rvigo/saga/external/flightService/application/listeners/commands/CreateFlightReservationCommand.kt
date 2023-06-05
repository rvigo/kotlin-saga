package com.rvigo.saga.external.flightService.application.listeners.commands

import java.util.UUID


data class CreateFlightReservationCommand(val sagaId: UUID, val cpf: String)
