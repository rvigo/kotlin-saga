package com.rvigo.saga.external.flightService.domain.services

import com.rvigo.saga.external.flightService.application.listeners.commands.CompensateCreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.ConfirmFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationResponse
import com.rvigo.saga.external.flightService.domain.models.FlightReservation
import com.rvigo.saga.external.flightService.infra.repositories.FlightRepository
import com.rvigo.saga.infra.events.BaseEvent
import com.rvigo.saga.infra.events.BaseResponse
import com.rvigo.saga.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class FlightReservationService(
    private val repository: FlightRepository,
    private val publisher: ApplicationEventPublisher
) {
    private val logger by logger()

    fun createFlightReservation(command: CreateFlightReservationCommand) {
        runCatching {
            logger.info("Creating a new flight reservation: $command")
            val flightReservation = FlightReservation(cpf = command.cpf)

            // uncomment below to force the compensation scenario
            // throw RuntimeException("Cannot create the reservation for cpf: ${command.cpf}")
            repository.save(flightReservation)
        }.onSuccess {
            notify(
                CreateFlightReservationResponse(
                    sagaId = command.sagaId,
                    status = BaseResponse.Status.SUCCESS,
                    reservationStatus = it.status,
                    reservationId = it.id
                )
            )
        }.onFailure {
            logger.error("${command.sagaId} - Something went wrong: $it")
            notify(
                CreateFlightReservationResponse(
                    sagaId = command.sagaId, status = BaseResponse.Status.FAILURE,
                    reservationStatus = FlightReservation.Status.FAILED
                )
            )
        }
    }

    fun cancelReservation(command: CompensateCreateFlightReservationCommand) {
        val reservation = repository.findByIdOrNull(command.flightReservationId)
            ?: throw RuntimeException("Cannot find reservation with id: ${command.flightReservationId}")

        logger.info("Cancelling Flight Reservation with id: ${command.flightReservationId}")
        val updatedReservation = reservation.copy(status = FlightReservation.Status.CANCELED)
        repository.save(updatedReservation).also {
            logger.info("Reservation with id ${it.id} cancelled")
        }
    }

    fun confirmFlightReservation(command: ConfirmFlightReservationCommand) {
        val reservation = repository.findByIdOrNull(command.flightReservationId)
            ?: throw RuntimeException("Cannot find reservation with id: ${command.flightReservationId}")

        logger.info("Confirming Flight Reservation with id: ${command.flightReservationId}")
        val updatedReservation = reservation.copy(status = FlightReservation.Status.CONFIRMED)
        repository.save(updatedReservation).also {
            logger.info("Reservation with id ${it.id} confirmed")
        }
    }

    fun notify(event: BaseEvent) {
        logger.info("Publishing event: $event")
        publisher.publishEvent(event)
    }
}

