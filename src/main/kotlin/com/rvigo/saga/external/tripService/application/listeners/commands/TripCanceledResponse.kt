package com.rvigo.saga.external.tripService.application.listeners.commands

import java.util.UUID

data class TripCanceledResponse(val sagaId: UUID, val tripId: UUID)
