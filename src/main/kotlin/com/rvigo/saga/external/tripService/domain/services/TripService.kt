package com.rvigo.saga.external.tripService.domain.services

import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripResponse.Status
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCanceledResponse
import com.rvigo.saga.external.tripService.domain.models.Trip
import com.rvigo.saga.external.tripService.domain.models.Trip.TripStatus
import com.rvigo.saga.external.tripService.infra.repositories.TripRepository
import com.rvigo.saga.infra.aws.EVENT_TYPE_HEADER
import com.rvigo.saga.infra.aws.SNSPublisher
import com.rvigo.saga.infra.aws.SnsEvent
import com.rvigo.saga.infra.events.SagaEvent
import com.rvigo.saga.infra.logger.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class TripService(
    private val repository: TripRepository,
    private val publisher: ApplicationEventPublisher,
    private val snsPublisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    private val sagaEventsTopic: String
) {
    private val logger by logger()

    fun create(command: CreateTripCommand) = runCatching {
        logger.info("Creating new Trip")
        val trip = Trip(cpf = command.cpf)

        // uncomment below to run a compensation scenario
        // throw RuntimeException("${command.sagaId} - Error creating a new trip")

        repository.save(trip)
    }.onSuccess {
        snsPublisher.publish(
            SnsEvent(
                CreateTripResponse(
                    sagaId = command.sagaId,
                    cpf = command.cpf,
                    responseStatus = Status.SUCCESS,
                    tripId = it.id,
                    tripStatus = it.status
                ),
                sagaEventsTopic,
                mapOf(EVENT_TYPE_HEADER to SagaEvent.CREATE_TRIP_RESPONSE.name)
            )
        )
    }.onFailure {
        logger.error("Something went wrong: $it")
        snsPublisher.publish(
            SnsEvent(
                CreateTripResponse(
                    sagaId = command.sagaId,
                    cpf = command.cpf,
                    responseStatus = Status.FAILURE,
                    tripStatus = TripStatus.FAILED
                ), sagaEventsTopic,
                mapOf(EVENT_TYPE_HEADER to SagaEvent.CREATE_TRIP_RESPONSE.name)
            )
        )
    }

    fun cancel(command: CompensateCreateTripCommand) {
        logger.warn("Cancelling trip with id: ${command.tripId}")
        val trip = repository.findByIdOrNull(command.tripId)
            ?: throw RuntimeException("Cannot find trip with id ${command.tripId}")
        trip.cancel()
        repository.save(trip).also {
            publisher.publishEvent(
                TripCanceledResponse(
                    sagaId = command.sagaId,
                    tripId = command.tripId,
                    tripStatus = it.status
                )
            )
        }
    }

    fun confirmTrip(command: ConfirmTripCommand) {
        val trip = repository.findByIdOrNull(command.tripId)
            ?: throw RuntimeException("Cannot find trip with id ${command.tripId}")
        val updatedTrip = trip.copy(hotelReservationId = command.hotelReservationId, status = TripStatus.CONFIRMED)
        repository.save(updatedTrip).also {
            logger.info("Trip ${it.id} confirmed!")
        }
    }
}
