package com.rvigo.saga.external.flightService.application.listeners

import com.rvigo.saga.external.flightService.application.listeners.commands.CompensateCreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.ConfirmFlightReservationCommand
import com.rvigo.saga.external.flightService.domain.services.FlightReservationService
import com.rvigo.saga.infra.listeners.DefaultListener
import com.rvigo.saga.infra.logger.logger
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class FlightCommandListener(private val service: FlightReservationService) : DefaultListener() {
    private val logger by logger()

    @EventListener
    @SqsListener(
        "\${cloud.aws.sqs.queues.create-flight-reservation-command}",
        deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS
    )
    fun on(@Payload command: String) {
        logger.info("Got a new command: $command")
        service.createFlightReservation(convertMessage(command))
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
