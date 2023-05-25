package com.rvigo.saga.external.tripService.application.listeners.commands

import com.rvigo.saga.external.tripService.domain.models.Trip
import java.util.UUID

data class TripCanceledResponse(val sagaId: UUID, val tripId: UUID, val tripStatus: Trip.TripStatus = Trip.TripStatus.CANCELED)
