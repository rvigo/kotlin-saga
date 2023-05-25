package com.rvigo.saga.domain

import com.rvigo.saga.application.proxies.HotelProxy
import com.rvigo.saga.application.proxies.TripProxy
import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse
import com.rvigo.saga.infra.repositories.SagaRepository
import com.rvigo.saga.logger
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class SagaManager(private val sagaRepository: SagaRepository,
                  private val hotelProxy: HotelProxy,
                  private val tripProxy: TripProxy) {
    private val logger by logger()

    // for non-brazilians, cpf is the person document number
    fun start(cpf: String) {
        val saga = Saga()
        // TODO include sagaId on MDC
        val updatedSaga = sagaRepository.save(saga).also { logger.info("A new saga has started $it") }

        // first saga step
        tripProxy.create(CreateTripCommand(sagaId = updatedSaga.id, cpf = cpf))
    }

    @EventListener
    fun on(response: TripCreatedResponse) {
        val saga = sagaRepository.findByIdOrNull(response.sagaId)
            ?: throw RuntimeException("Cannot find saga with id: ${response.sagaId}")

        if (response.isSuccess()) {
            sagaRepository.save(saga.updateTripId(response.tripId))
            logger.info("${saga.id} - Saga updated! Creating a reservation")
            hotelProxy.create(CreateReservationCommand(response.sagaId, response.cpf))
        } else {
            // since this is the first step, there is nothing to compensate
            sagaRepository.save(saga.markAsCompensated())
        }
    }

    @EventListener
    fun on(response: CreateReservationResponse) {
        if (response.isSuccess()) {
            val saga = sagaRepository.findByIdOrNull(response.sagaId)
                ?: throw RuntimeException("Cannot find saga with id: ${response.sagaId}")

            val updatedSaga = saga
                .updateReservationId(response.reservationId)

            logger.info("${saga.id} - Sending \"confirm\" commands to Saga participants")
            hotelProxy.confirm(ConfirmReservationCommand(
                sagaId = updatedSaga.id,
                hotelReservationId = updatedSaga.hotelReservationId!!)
            )
            tripProxy.confirmTrip(ConfirmTripCommand(
                sagaId = updatedSaga.id,
                tripId = updatedSaga.tripId!!,
                hotelReservationId = response.reservationId!!)
            )
            sagaRepository.save(updatedSaga.markAsCompleted()).also {
                logger.info("${saga.id} - Saga completed")
            }
        } else {
            //TODO compensate
        }
    }
}
