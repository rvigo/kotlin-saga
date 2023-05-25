package com.rvigo.saga.external.tripService.domain.services

import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse.Status
import com.rvigo.saga.external.tripService.domain.models.Trip
import com.rvigo.saga.external.tripService.infra.repositories.TripRepository
import com.rvigo.saga.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

//TODO this "services" should be async
@Transactional(propagation = Propagation.SUPPORTS)
@Service
class TripService(private val repository: TripRepository,
                  private val publisher: ApplicationEventPublisher) {
    private val logger by logger()

    fun create(command: CreateTripCommand) = runCatching {
        logger.info("${command.sagaId} - Creating new Trip")
        val trip = Trip(cpf = command.cpf)

        repository.save(trip)
    }.onSuccess {
        publisher.publishEvent(TripCreatedResponse(
            sagaId = command.sagaId,
            cpf = command.cpf, tripId = it.id,
            status = Status.SUCCESS)
        )
    }.onFailure {
        logger.error("${command.sagaId} - Something went wrong: $it")
        publisher.publishEvent(TripCreatedResponse(
            sagaId = command.sagaId,
            cpf = command.cpf,
            status = Status.FAILURE)
        )
    }

    fun cancel(id: UUID) {
        logger.warn("Cancelling trip with id: $id")
        val trip = repository.findByIdOrNull(id) ?: throw RuntimeException("Cannot find trip with id $id")
        trip.cancel()
        repository.save(trip)
    }

    fun confirmTrip(command: ConfirmTripCommand) {
        val trip = repository.findByIdOrNull(command.tripId)
            ?: throw RuntimeException("Cannot find trip with id ${command.tripId}")
        val updatedTrip = trip.copy(hotelReservationId = command.hotelReservationId, status = Trip.TripStatus.CONFIRMED)
        repository.save(updatedTrip).also { logger.info("${command.sagaId} - Trip ${it.id} confirmed!") }
    }
}
