package com.rvigo.saga.external.hotelService.application.listeners

import com.rvigo.saga.external.hotelService.application.listeners.commands.CompensateCreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.hotelService.domain.services.HotelService
import com.rvigo.saga.logger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class HotelCommandListener(private val service: HotelService) {
    private val logger by logger()

    @EventListener
    fun on(command: CreateReservationCommand) {
        logger.info("Got a new command: $command")
        service.createReservation(command)
    }

    @EventListener
    fun on(command: CompensateCreateReservationCommand) {
        logger.info("Got a new command: $command")
        //TODO service.cancelReservation
    }

    @EventListener
    fun on(command: ConfirmReservationCommand) {
        logger.info("Got a new command: $command")
        service.confirmReservation(command)
    }
}
