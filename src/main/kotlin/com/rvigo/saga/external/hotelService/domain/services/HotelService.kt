package com.rvigo.saga.external.hotelService.domain.services

import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationResponse.Status
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation.Status.CONFIRMED
import com.rvigo.saga.external.hotelService.infra.repositories.HotelRepository
import com.rvigo.saga.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional(propagation = Propagation.SUPPORTS)
@Service
class HotelService(private val repository: HotelRepository,
                   private val publisher: ApplicationEventPublisher) {
    private val logger by logger()

    fun createReservation(command: CreateReservationCommand) = runCatching {
        logger.info("${command.sagaId} - Creating a new hotel reservation")
        val hotelReservation = HotelReservation(cpf = command.cpf)

        // uncomment below to force the compensation scenario
        // throw RuntimeException("Cannot create the reservation for cpf: ${command.cpf}")

        repository.save(hotelReservation)
    }.onSuccess {
        publisher.publishEvent(CreateReservationResponse(sagaId = command.sagaId, reservationId = it.id, status = Status.SUCCESS))
    }.onFailure {
        logger.error("${command.sagaId} - Something went wrong: $it")
        publisher.publishEvent(CreateReservationResponse(sagaId = command.sagaId, status = Status.FAILURE))
    }

    fun confirmReservation(command: ConfirmReservationCommand) {
        val reservation = repository.findByIdOrNull(command.hotelReservationId)
            ?: throw RuntimeException("Cannot find reservation with id: ${command.hotelReservationId}")

        logger.info("${command.sagaId} - Confirming Hotel Reservation with id: ${command.hotelReservationId}")
        val updatedReservation = reservation.copy(status = CONFIRMED)
        repository.save(updatedReservation).also {
            logger.info("${command.sagaId} - Reservation with id ${it.id} confirmed")
        }
    }
}

