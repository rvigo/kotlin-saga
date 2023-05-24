package com.rvigo.saga.external.tripService.application.listeners.commands

import java.util.UUID


data class TripCreatedResponse(val sagaId: UUID, val cpf: String, val status: Status, val tripId: UUID? = null) {
    fun isSuccess() = this.status == Status.SUCCESS
    enum class Status {
        SUCCESS, FAILURE
    }
}
