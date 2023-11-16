package com.rvigo.saga.external.tripService.application.listeners.commands

import com.rvigo.saga.domain.command.CommandResponseBody
import com.rvigo.saga.external.tripService.domain.model.TripEntity
import java.util.UUID

data class TripResponseBody(val tripId: UUID? = null, val tripStatus: TripEntity.TripStatus?) : CommandResponseBody
