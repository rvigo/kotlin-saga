package com.rvigo.saga.external.flightService.application.listeners

import com.rvigo.saga.external.flightService.application.listeners.commands.CompensateCreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.ConfirmFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.external.flightService.domain.services.FlightReservationService
import com.rvigo.saga.logger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class FlightCommandListener(private val service: FlightReservationService) {
    private val logger by logger()

    @EventListener
    fun on(command: CreateFlightReservationCommand) {
        logger.info("Got a new command: $command")
        service.createFlightReservation(command)
    }

    @EventListener
    fun on(command: CompensateCreateFlightReservationCommand) {
        logger.info("Got a new command: $command")
        service.cancelReservation(command)
    }

    @EventListener
    fun on(command: ConfirmFlightReservationCommand) {
        logger.info("Got a new command: $command")
        service.confirmFlightReservation(command)
    }
}
