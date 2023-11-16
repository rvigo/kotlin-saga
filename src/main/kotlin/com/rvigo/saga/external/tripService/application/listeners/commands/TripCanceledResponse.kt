package com.rvigo.saga.external.tripService.application.listeners.commands

import com.rvigo.saga.external.tripService.domain.model.TripEntity
import java.util.UUID

data class TripCanceledResponse(val sagaId: UUID, val tripId: UUID, val tripStatus: TripEntity.TripStatus = TripEntity.TripStatus.CANCELED)
