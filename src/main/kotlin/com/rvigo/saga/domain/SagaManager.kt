package com.rvigo.saga.domain

import com.rvigo.saga.application.proxies.HotelProxy
import com.rvigo.saga.application.proxies.TripProxy
import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCanceledResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse
import com.rvigo.saga.infra.repositories.SagaRepository
import com.rvigo.saga.logger
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional
class SagaManager(private val sagaRepository: SagaRepository,
                  private val hotelProxy: HotelProxy,
                  private val tripProxy: TripProxy) {
    private val logger by logger()

    // for non-brazilians, "cpf" is the person document number
    fun start(cpf: String) {
        // TODO include sagaId on MDC
        val updatedSaga = Saga().save().also { logger.info("A new saga has started $it") }

        // first saga step
        tripProxy.create(CreateTripCommand(sagaId = updatedSaga.id, cpf = cpf))
    }

    // second saga step
    @EventListener
    fun on(response: TripCreatedResponse) {
        val saga = getSaga(response.sagaId)

        if (response.isSuccess()) {
            saga.updateTripId(response.tripId).save()
            logger.info("${saga.id} - Saga updated! Creating a reservation")
            hotelProxy.create(CreateReservationCommand(response.sagaId, response.cpf))
        } else {
            // since this is the first step, there is nothing to compensate
            saga.markAsCompensated().save()
        }
    }

    // saga third step
    @EventListener
    fun on(response: CreateReservationResponse) {
        val saga = getSaga(response.sagaId)
        if (response.isSuccess()) {
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
            updatedSaga.markAsCompleted().save().also {
                logger.info("${saga.id} - Saga completed")
            }
        } else {
            saga.markAsCompensating().save()
            tripProxy.compensate(CompensateCreateTripCommand(sagaId = saga.id, tripId = saga.tripId!!))
        }
    }

    // first compensation response
    @EventListener
    fun on(response: TripCanceledResponse) {
        val saga = getSaga(response.sagaId)
        saga.markAsCompensated().save().also {
            logger.info("${it.id} - Saga marked as compensated")
        }
    }

    private fun getSaga(id: UUID) = sagaRepository.findByIdOrNull(id)
        ?: throw RuntimeException("Cannot find saga with id: $id")

    private fun Saga.save() = sagaRepository.save(this)
}
