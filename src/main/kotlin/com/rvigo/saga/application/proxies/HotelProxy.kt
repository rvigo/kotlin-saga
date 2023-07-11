package com.rvigo.saga.application.proxies

import com.rvigo.saga.external.hotelService.application.listeners.commands.CompensateCreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationCommand
import com.rvigo.saga.infra.aws.EVENT_TYPE_HEADER
import com.rvigo.saga.infra.aws.SNSPublisher
import com.rvigo.saga.infra.aws.SnsEvent
import com.rvigo.saga.infra.events.SagaEvent
import com.rvigo.saga.infra.proxies.HotelProxy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class HotelProxy(
    private val publisher: ApplicationEventPublisher,
    private val snsPublisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    val topic: String
) : HotelProxy {
    override fun create(createHotelReservationCommand: CreateHotelReservationCommand) {
        snsPublisher.publish(
            SnsEvent(
                createHotelReservationCommand,
                topic,
                mapOf(EVENT_TYPE_HEADER to SagaEvent.CREATE_HOTEL_RESERVATION.name)
            )
        )
    }

    override fun compensate(compensateCreateReservationCommand: CompensateCreateReservationCommand) {
        publisher.publishEvent(compensateCreateReservationCommand)
    }

    override fun confirm(confirmReservationCommand: ConfirmReservationCommand) {
        publisher.publishEvent(confirmReservationCommand)
    }
}
