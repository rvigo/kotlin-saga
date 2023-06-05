package com.rvigo.saga.external.flightService.application.listeners.commands

import java.util.UUID


data class ConfirmFlightReservationCommand(val sagaId: UUID, val flightReservationId: UUID)
