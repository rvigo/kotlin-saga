package com.rvigo.saga.application.proxies

import com.rvigo.saga.external.hotelService.application.listeners.commands.CompensateCreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.infra.proxies.HotelProxy
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class HotelProxy(private val publisher: ApplicationEventPublisher) : HotelProxy {
    override fun create(createReservationCommand: CreateReservationCommand) {
        publisher.publishEvent(createReservationCommand)
    }

    override fun compensate(compensateCreateReservationCommand: CompensateCreateReservationCommand) {
        publisher.publishEvent(compensateCreateReservationCommand)
    }
}
