package com.rvigo.saga.external.tripService.application.listeners.commands

import java.util.UUID

data class CreateTripCommand(val sagaId: UUID, val cpf: String)
