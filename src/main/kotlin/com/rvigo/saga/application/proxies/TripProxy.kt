package com.rvigo.saga.application.proxies

import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.infra.proxies.TripProxy
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class TripProxy(private val publisher: ApplicationEventPublisher) : TripProxy {
    override fun create(createTripCommand: CreateTripCommand) {
        publisher.publishEvent(createTripCommand)
    }

    override fun compensate(compensateCreateTripCommand: CompensateCreateTripCommand) {
        publisher.publishEvent(compensateCreateTripCommand)
    }
}
