package com.rvigo.saga.external.flightService.domain.services

import com.rvigo.saga.external.flightService.application.listeners.commands.ConfirmFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationResponse
import com.rvigo.saga.external.flightService.domain.models.FlightReservation
import com.rvigo.saga.external.flightService.infra.repositories.FlightRepository
import com.rvigo.saga.logger
import kotlinx.coroutines.delay
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
class FlightReservationService(
    private val repository: FlightRepository,
    private val publisher: ApplicationEventPublisher
) {
    private val logger by logger()

    suspend fun createFlightReservation(command: CreateFlightReservationCommand) = runCatching {
        delay(2000)
        logger.info("Creating a new hotel reservation")
        val hotelReservation = FlightReservation(cpf = command.cpf)

        // uncomment below to force the compensation scenario
        // throw RuntimeException("Cannot create the reservation for cpf: ${command.cpf}")
        repository.save(hotelReservation)
    }.onSuccess {
        publisher.publishEvent(
            CreateFlightReservationResponse(
                sagaId = command.sagaId,
                status = CreateFlightReservationResponse.Status.SUCCESS,
                reservationStatus = it.status,
                reservationId = it.id
            )
        )
    }.onFailure {
        logger.error("${command.sagaId} - Something went wrong: $it")
        publisher.publishEvent(
            CreateFlightReservationResponse(
                sagaId = command.sagaId, status = CreateFlightReservationResponse.Status.FAILURE,
                reservationStatus = FlightReservation.Status.FAILED
            )
        )
    }

    suspend fun confirmFlightReservation(command: ConfirmFlightReservationCommand) {
        delay(2000)
        val reservation = repository.findByIdOrNull(command.flightReservationId)
            ?: throw RuntimeException("Cannot find reservation with id: ${command.flightReservationId}")

        logger.info("Confirming Hotel Reservation with id: ${command.flightReservationId}")
        val updatedReservation = reservation.copy(status = FlightReservation.Status.CONFIRMED)
        repository.save(updatedReservation).also {
            logger.info("Reservation with id ${it.id} confirmed")
        }
    }
}

