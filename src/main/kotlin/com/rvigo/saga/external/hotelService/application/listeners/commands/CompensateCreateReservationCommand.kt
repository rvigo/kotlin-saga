package com.rvigo.saga.external.hotelService.application.listeners.commands

import java.util.UUID

data class CompensateCreateReservationCommand(val sagaId: UUID, val hotelReservationId: UUID)
