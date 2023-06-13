package com.rvigo.saga.application.proxies

import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.infra.aws.EVENT_TYPE_HEADER
import com.rvigo.saga.infra.aws.SNSPublisher
import com.rvigo.saga.infra.aws.SnsEvent
import com.rvigo.saga.infra.events.SagaEvent
import com.rvigo.saga.infra.proxies.TripProxy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class TripProxy(
    private val publisher: ApplicationEventPublisher,
    private val snsPublisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}") val topic: String
) : TripProxy {

    override fun create(createTripCommand: CreateTripCommand) {
        snsPublisher.publish(SnsEvent(createTripCommand, topic, mapOf(EVENT_TYPE_HEADER to SagaEvent.CREATE_TRIP.name)))
    }


    override fun compensate(compensateCreateTripCommand: CompensateCreateTripCommand) {
        publisher.publishEvent(compensateCreateTripCommand)
    }

    override fun confirmTrip(confirmTripCommand: ConfirmTripCommand) {
        publisher.publishEvent(confirmTripCommand)
    }
}
