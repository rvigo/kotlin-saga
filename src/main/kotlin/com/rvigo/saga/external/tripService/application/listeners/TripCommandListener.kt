package com.rvigo.saga.external.tripService.application.listeners

import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.domain.services.TripService
import com.rvigo.saga.infra.listeners.DefaultListener
import com.rvigo.saga.logger
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


@Component
class TripCommandListener(private val service: TripService) : DefaultListener() {
    private val logger by logger()

    @SqsListener("\${cloud.aws.sqs.queues.create-trip-command}", deletionPolicy = ON_SUCCESS)
    fun on(@Payload message: String) {
        logger.info("Got a command: $message")

        service.create(convertMessage(message))
    }

    @EventListener
    fun on(command: CompensateCreateTripCommand) {
        logger.info("Got a compensation command: $command")
        service.cancel(command)
    }

    @EventListener
    fun on(command: ConfirmTripCommand) {
        logger.info("Got a command: $command")
        service.confirmTrip(command)
    }
}
