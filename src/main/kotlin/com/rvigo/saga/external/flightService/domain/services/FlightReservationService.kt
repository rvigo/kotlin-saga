package com.rvigo.saga.external.flightService.domain.services

import com.rvigo.saga.domain.command.CommandMessage.Companion.EVENT_TYPE_HEADER
import com.rvigo.saga.domain.command.CommandResponse
import com.rvigo.saga.domain.event.SagaEventType
import com.rvigo.saga.domain.messaging.Message
import com.rvigo.saga.external.flightService.application.listeners.commands.CompensateCreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.ConfirmFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationResponse
import com.rvigo.saga.external.flightService.application.listeners.commands.FlightReservationResponseBody
import com.rvigo.saga.external.flightService.domain.models.FlightReservation
import com.rvigo.saga.external.flightService.infra.repositories.FlightRepository
import com.rvigo.saga.infra.logger.logger
import com.rvigo.saga.infra.publisher.SNSPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class FlightReservationService(
    private val repository: FlightRepository,
    private val publisher: ApplicationEventPublisher,
    private val snsPublisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    private val sagaEventsTopic: String

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
            snsPublisher.publish(
                CreateFlightReservationResponse(
                    sagaId = command.sagaId,
                    responseStatus = CommandResponse.Status.SUCCESS,
                    body = FlightReservationResponseBody(
                        reservationId = it.id,
                        reservationStatus = it.status,
                    ),
                    attributes = mapOf(EVENT_TYPE_HEADER to SagaEventType.CREATE_FLIGHT_RESERVATION.name)),
                sagaEventsTopic,
            )
        }.onFailure {
            logger.error("${command.sagaId} - Something went wrong: $it")
            notify(
                CreateFlightReservationResponse(
                    sagaId = command.sagaId,
                    responseStatus = CommandResponse.Status.FAILURE,
                    body = FlightReservationResponseBody(reservationStatus = FlightReservation.Status.FAILED),
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

    fun notify(message: Message) {
        logger.info("Publishing event: $message")
        publisher.publishEvent(message)
    }
}

