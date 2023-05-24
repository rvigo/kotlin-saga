package com.rvigo.saga.external.tripService.domain.services

import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse.Status
import com.rvigo.saga.external.tripService.application.listeners.commands.UpdateTripHotelReservationInfoCommand
import com.rvigo.saga.external.tripService.domain.models.Trip
import com.rvigo.saga.external.tripService.infra.repositories.TripRepository
import com.rvigo.saga.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TripService(private val repository: TripRepository,
                  private val publisher: ApplicationEventPublisher) {
    private val logger by logger()

    @Transactional(propagation = Propagation.NEVER)
    fun create(command: CreateTripCommand) = runCatching {
        logger.info("creating new Trip")
        val trip = Trip(cpf = command.cpf)

        repository.save(trip)
    }.onSuccess {
        logger.info("publishing a response")
        publisher.publishEvent(TripCreatedResponse(sagaId = command.sagaId, cpf = command.cpf, tripId = it.id, status = Status.SUCCESS))
    }.onFailure {
        logger.error("Something went wrong: ${it.message}")
        logger.info("publishing an error response")
        publisher.publishEvent(TripCreatedResponse(sagaId = command.sagaId, cpf = command.cpf, status = Status.FAILURE))
    }

    fun get(id: UUID): Trip {
        logger.info("looking for id $id")
        return repository.findByIdOrNull(id) ?: throw RuntimeException("Cannot find trip with id $id")
    }

    fun cancel(id: UUID) {
        logger.warn("cancelling trip with id: $id")
        val trip = repository.findByIdOrNull(id) ?: throw RuntimeException("Cannot find trip with id $id")
        trip.cancel()
        repository.save(trip)
    }

    fun updateHotelReservationId(command: UpdateTripHotelReservationInfoCommand) {
        val trip = repository.findByIdOrNull(command.tripId)
            ?: throw RuntimeException("Cannot find trip with id ${command.tripId}")
        trip.copy(hotelCode = command.hotelReservationId)
        repository.save(trip).also { logger.info("trip updated!") }
    }
}
