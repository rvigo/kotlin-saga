package com.rvigo.saga.domain

import com.rvigo.saga.application.proxies.HotelProxy
import com.rvigo.saga.application.proxies.TripProxy
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse
import com.rvigo.saga.infra.repositories.SagaRepository
import com.rvigo.saga.logger
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class SagaManager(private val sagaRepository: SagaRepository,
                  private val hotelProxy: HotelProxy,
                  private val tripProxy: TripProxy) {
    private val logger by logger()

    fun start(cpf: String) {
        logger.info("creating a new saga")
        val saga = sagaRepository.save(Saga())
        logger.info("starting saga")
        tripProxy.create(CreateTripCommand(saga.id!!, cpf))
    }

    @EventListener
    fun on(event: TripCreatedResponse) {
        if (event.isSuccess()) {
            logger.info("sending a createReservationCommand")
            hotelProxy.create(CreateReservationCommand(event.sagaId, event.cpf))
        } else {
            val saga = sagaRepository.findByIdOrNull(event.sagaId)
                ?: throw RuntimeException("cannot find saga with id: ${event.sagaId}")
            sagaRepository.save(saga.markAsCompensated())
        }
    }

    @EventListener
    fun on(event: CreateReservationResponse) {
        if (event.isSuccess()) {
            // TODO update trip hotel reservation id -> tripProxy.updateHotelReservation (or updateTrip???)
            logger.info("Saga is completed")
            val saga = sagaRepository.findByIdOrNull(event.sagaId)
                ?: throw RuntimeException("Cannot find saga with id: ${event.sagaId}")
            sagaRepository.save(saga.markAsCompleted()).also {
                //TODO send confirmation events to trip and hotel services
            }
        } else {
            //TODO compensate
        }
    }
}
