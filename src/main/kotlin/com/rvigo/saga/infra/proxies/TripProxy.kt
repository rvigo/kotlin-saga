package com.rvigo.saga.infra.proxies

import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand

interface TripProxy {
    fun create(createTripCommand: CreateTripCommand)
    fun compensate(compensateCreateTripCommand: CompensateCreateTripCommand)
    fun confirmTrip(confirmTripCommand: ConfirmTripCommand)
}
