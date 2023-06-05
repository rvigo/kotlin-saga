package com.rvigo.saga.infra.proxies

import com.rvigo.saga.external.flightService.application.listeners.commands.CompensateCreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.ConfirmFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand


interface FlightProxy {
    fun create(createFlightReservationCommand: CreateFlightReservationCommand)
    fun compensate(compensateCreateFlightReservationCommand: CompensateCreateFlightReservationCommand)
    fun confirm(confirmFlightReservationCommand: ConfirmFlightReservationCommand)
}
