package com.rvigo.saga.external.tripService.application.listeners

import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.domain.services.TripService
import com.rvigo.saga.logger
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class TripCommandListener(private val service: TripService) {
    private val logger by logger()

    @EventListener
    fun on(command: CreateTripCommand) {
        logger.info("Got a command: $command")

        service.create(command)
    }

    @EventListener
    fun on(command: CompensateCreateTripCommand) {
        logger.info("Got a compensation command: $command")
        runBlocking {
            service.cancel(command)
        }
    }

    @EventListener
    fun on(command: ConfirmTripCommand) {
        logger.info("Got a command: $command")
        runBlocking {
            service.confirmTrip(command)
        }
    }
}
