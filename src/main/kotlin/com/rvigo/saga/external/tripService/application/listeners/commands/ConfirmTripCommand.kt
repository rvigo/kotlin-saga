package com.rvigo.saga.external.tripService.application.listeners.commands

import java.util.UUID

data class ConfirmTripCommand(val sagaId: UUID, val tripId: UUID, val hotelReservationId: UUID)
