package com.rvigo.saga.external.hotelService.domain.services

import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationResponse
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation.Status.CONFIRMED
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation.Status.FAILED
import com.rvigo.saga.external.hotelService.infra.repositories.HotelRepository
import com.rvigo.saga.infra.aws.EVENT_TYPE_HEADER
import com.rvigo.saga.infra.aws.SNSPublisher
import com.rvigo.saga.infra.aws.SnsEvent
import com.rvigo.saga.infra.events.BaseEvent
import com.rvigo.saga.infra.events.BaseResponse
import com.rvigo.saga.infra.events.SagaEvent
import com.rvigo.saga.infra.logger.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional
@Service
class HotelService(
    private val repository: HotelRepository,
    private val publisher: ApplicationEventPublisher,
    private val snsPublisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    private val sagaEventsTopic: String
) {
    private val logger by logger()

    fun createReservation(command: CreateHotelReservationCommand) {
        runCatching {
            logger.info("Creating a new hotel reservation")
            val hotelReservation = HotelReservation(cpf = command.cpf)

            // uncomment below to force the compensation scenario
            // throw RuntimeException("Cannot create the reservation for cpf: ${command.cpf}")
            repository.save(hotelReservation)
        }.onSuccess {
            snsPublisher.publish(
                SnsEvent(
                    CreateHotelReservationResponse(
                        reservationId = it.id,
                        reservationStatus = it.status,
                        status = BaseResponse.Status.SUCCESS,
                        sagaId = command.sagaId
                    ),
                    sagaEventsTopic,
                    mapOf(EVENT_TYPE_HEADER to SagaEvent.CREATE_HOTEL_RESERVATION_RESPONSE.name)
                )
            )
        }.onFailure {
            notify(
                CreateHotelReservationResponse(
                    sagaId = command.sagaId, status = BaseResponse.Status.FAILURE,
                    reservationStatus = FAILED
                )
            )
        }
    }

    fun confirmReservation(command: ConfirmReservationCommand) {
        val reservation = repository.findByIdOrNull(command.hotelReservationId)
            ?: throw RuntimeException("Cannot find reservation with id: ${command.hotelReservationId}")

        logger.info("Confirming Hotel Reservation with id: ${command.hotelReservationId}")
        val updatedReservation = reservation.copy(status = CONFIRMED)
        repository.save(updatedReservation).also {
            logger.info("Reservation with id ${it.id} confirmed")
        }
    }

    fun notify(event: BaseEvent) {
        logger.info("publishing event: $event")
        publisher.publishEvent(event)
    }
}

