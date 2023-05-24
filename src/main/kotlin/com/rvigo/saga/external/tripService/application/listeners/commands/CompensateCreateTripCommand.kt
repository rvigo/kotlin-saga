package com.rvigo.saga.external.tripService.application.listeners.commands

import java.util.UUID

data class CompensateCreateTripCommand(val sagaId: UUID, val tripId: UUID)
