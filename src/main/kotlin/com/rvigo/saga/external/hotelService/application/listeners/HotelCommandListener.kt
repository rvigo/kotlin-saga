package com.rvigo.saga.external.hotelService.application.listeners

import com.rvigo.saga.external.hotelService.application.listeners.commands.CompensateCreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.hotelService.domain.services.HotelService
import com.rvigo.saga.logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class HotelCommandListener(private val service: HotelService) {
    private val logger by logger()

    @EventListener
    fun on(command: CreateReservationCommand) {
        logger.info("${command.sagaId} - Got a new command: $command")
        runBlocking {
            logger.info("${command.sagaId} - Hotel service is \"busy\"...")
            delay(8000)
            service.createReservation(command)
        }
    }

    @EventListener
    fun on(command: CompensateCreateReservationCommand) {
        logger.info("${command.sagaId} - Got a new command: $command")
        //TODO service.cancelReservation
    }

    @EventListener
    fun on(command: ConfirmReservationCommand) {
        logger.info("${command.sagaId} - Got a new command: $command")
        runBlocking {
            service.confirmReservation(command)
        }
    }
}
