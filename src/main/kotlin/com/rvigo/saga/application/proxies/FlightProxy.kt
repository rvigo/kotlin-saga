package com.rvigo.saga.application.proxies

import com.rvigo.saga.external.flightService.application.listeners.commands.CompensateCreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.ConfirmFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.infra.proxies.FlightProxy
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class FlightProxy(private val applicationEventPublisher: ApplicationEventPublisher) : FlightProxy {
    override fun create(createFlightReservationCommand: CreateFlightReservationCommand) {
        applicationEventPublisher.publishEvent(createFlightReservationCommand)
    }

    override fun compensate(compensateCreateFlightReservationCommand: CompensateCreateFlightReservationCommand) {
        applicationEventPublisher.publishEvent(compensateCreateFlightReservationCommand)
    }

    override fun confirm(confirmFlightReservationCommand: ConfirmFlightReservationCommand) {
        applicationEventPublisher.publishEvent(confirmFlightReservationCommand)
    }
}
