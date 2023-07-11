package com.rvigo.saga.application.proxies

import com.rvigo.saga.external.flightService.application.listeners.commands.CompensateCreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.ConfirmFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.infra.aws.EVENT_TYPE_HEADER
import com.rvigo.saga.infra.aws.SNSPublisher
import com.rvigo.saga.infra.aws.SnsEvent
import com.rvigo.saga.infra.events.SagaEvent
import com.rvigo.saga.infra.proxies.FlightProxy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class FlightProxy(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val snsPublisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    val topic: String
) : FlightProxy {
    override fun create(createFlightReservationCommand: CreateFlightReservationCommand) {
        snsPublisher.publish(
            SnsEvent(
                createFlightReservationCommand,
                topic,
                mapOf(EVENT_TYPE_HEADER to SagaEvent.CREATE_FLIGHT_RESERVATION.name)
            )
        )
    }

    override fun compensate(compensateCreateFlightReservationCommand: CompensateCreateFlightReservationCommand) {
        applicationEventPublisher.publishEvent(compensateCreateFlightReservationCommand)
    }

    override fun confirm(confirmFlightReservationCommand: ConfirmFlightReservationCommand) {
        applicationEventPublisher.publishEvent(confirmFlightReservationCommand)
    }
}
