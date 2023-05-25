package com.rvigo.saga.external.tripService.application.listeners

import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.domain.services.TripService
import com.rvigo.saga.logger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TripCommandListener(private val service: TripService) {
    private val logger by logger()

    @EventListener
    fun on(command: CreateTripCommand) {
        logger.info("${command.sagaId} - Got a command: $command")
        service.create(command)
    }

    @EventListener
    fun on(command: CompensateCreateTripCommand) {
        logger.info("${command.sagaId} - Got a compensation command: $command")
        service.cancel(command)
    }

    @EventListener
    fun on(command: ConfirmTripCommand) {
        logger.info("${command.sagaId} - Got a command: $command")
        service.confirmTrip(command)
    }
}
