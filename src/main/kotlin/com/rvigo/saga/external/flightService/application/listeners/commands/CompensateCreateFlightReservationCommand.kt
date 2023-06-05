package com.rvigo.saga.external.flightService.application.listeners.commands

import java.util.UUID


data class CompensateCreateFlightReservationCommand(val sagaId: UUID, val hotelReservationId: UUID)
