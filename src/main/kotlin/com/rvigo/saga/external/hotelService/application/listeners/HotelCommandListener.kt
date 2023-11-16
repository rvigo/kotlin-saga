package com.rvigo.saga.external.hotelService.application.listeners

import com.rvigo.saga.external.hotelService.application.listeners.commands.CompensateCreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.domain.services.HotelService
import com.rvigo.saga.infra.listener.DefaultListener
import com.rvigo.saga.infra.logger.logger
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class HotelCommandListener(private val service: HotelService) : DefaultListener() {
    private val logger by logger()

    @SqsListener(
        "\${cloud.aws.sqs.queues.create-hotel-reservation-command}", deletionPolicy = ON_SUCCESS
    )
    fun on(@Payload message: String) {
        service.createReservation(convertMessage(message))
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
