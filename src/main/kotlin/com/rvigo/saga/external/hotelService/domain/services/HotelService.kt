package com.rvigo.saga.external.hotelService.domain.services

import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationResponse.Status
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import com.rvigo.saga.external.hotelService.infra.repositories.HotelRepository
import com.rvigo.saga.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class HotelService(private val repository: HotelRepository,
                   private val publisher: ApplicationEventPublisher) {
    private val logger by logger()

    @Transactional(propagation = Propagation.NEVER)
    fun createReservation(command: CreateReservationCommand) = runCatching {
        logger.info("creating a new hotel reservation")
        val hotelReservation = HotelReservation(cpf = command.cpf)
        repository.save(hotelReservation)
    }.onSuccess {
        logger.info("publishing a response")
        publisher.publishEvent(CreateReservationResponse(sagaId = command.sagaId, reservationId = it.id, status = Status.SUCCESS))
    }.onFailure {
        logger.error("Something went wrong when creating the reservation: $it")
        publisher.publishEvent(CreateReservationResponse(sagaId = command.sagaId, status = Status.FAILURE))
    }
}

