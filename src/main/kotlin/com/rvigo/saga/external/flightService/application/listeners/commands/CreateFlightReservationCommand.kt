package com.rvigo.saga.external.flightService.application.listeners.commands

import com.rvigo.saga.infra.aws.SnsEvent
import java.util.UUID


data class CreateFlightReservationCommand(val sagaId: UUID, val cpf: String) : SnsEvent.SnsEventBody
