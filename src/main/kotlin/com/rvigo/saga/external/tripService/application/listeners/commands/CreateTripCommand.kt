package com.rvigo.saga.external.tripService.application.listeners.commands

import com.rvigo.saga.infra.aws.SnsEvent
import java.util.UUID

data class CreateTripCommand(val sagaId: UUID, val cpf: String) : SnsEvent.SnsEventBody
