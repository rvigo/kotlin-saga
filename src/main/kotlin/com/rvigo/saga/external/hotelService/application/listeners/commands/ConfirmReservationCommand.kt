package com.rvigo.saga.external.hotelService.application.listeners.commands

import java.util.UUID

data class ConfirmReservationCommand(val sagaId: UUID, val hotelReservationId: UUID)
