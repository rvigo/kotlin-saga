package com.rvigo.saga.domain

import com.rvigo.saga.application.proxies.FlightProxy
import com.rvigo.saga.application.proxies.HotelProxy
import com.rvigo.saga.application.proxies.TripProxy
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripResponse
import com.rvigo.saga.infra.LoggerUtils.putSagaIdIntoMdc
import com.rvigo.saga.infra.aws.EVENT_TYPE_HEADER
import com.rvigo.saga.infra.aws.SNSPublisher
import com.rvigo.saga.infra.aws.SnsEvent
import com.rvigo.saga.infra.eventStore.SagaEventStoreEntry
import com.rvigo.saga.infra.eventStore.SagaEventStoreManager
import com.rvigo.saga.infra.events.onFailure
import com.rvigo.saga.infra.events.onSuccess
import com.rvigo.saga.infra.repositories.SagaRepository
import com.rvigo.saga.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class SagaManager(
    private val sagaRepository: SagaRepository,
    private val sagaEventStoreManager: SagaEventStoreManager,
    private val hotelProxy: HotelProxy,
    private val tripProxy: TripProxy,
    private val flightProxy: FlightProxy,
    private val snsPublisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    private val targetTopic: String
) {
    private val logger by logger()

    fun createSaga(command: CreateSagaCommand) {
        val participants = buildParticipants()
        withNewSaga(participants) {
            sagaEventStoreManager.updateEntry(SagaEventStoreEntry(sagaId = id, sagaStatus = status))
            // trigger first saga step
            tripProxy.create(CreateTripCommand(id, command.cpf))
        }
    }

    fun handleCreateTripResponse(response: CreateTripResponse) {
        withSaga(response.sagaId) {
            response.onSuccess {
                logger.info("Trip created")
                hotelProxy.create(CreateReservationCommand(response.sagaId, response.cpf))
                flightProxy.create(CreateFlightReservationCommand(response.sagaId, response.cpf))
                sagaEventStoreManager.updateEntry(
                    SagaEventStoreEntry(
                        sagaId = id,
                        sagaStatus = status,
                        tripStatus = response.tripStatus,
                        tripId = response.tripId
                    )
                )
                snsPublisher.publish(
                    SnsEvent(
                        SagaUpdatedEvent(
                            id,
                            Participant.ParticipantName.TRIP,
                            Participant.Status.COMPLETED
                        ),
                        targetTopic,
                        mapOf(EVENT_TYPE_HEADER to "UPDATE_EVENT")
                    )
                )
            }.onFailure {
                // since this is the first step, there is nothing to compensate
                val updateSaga = this.updateParticipant(
                    Participant.ParticipantName.TRIP,
                    response.tripId,
                    Participant.Status.COMPENSATING
                ).markAsCompensated().save()
                sagaEventStoreManager.updateEntry(
                    SagaEventStoreEntry(
                        sagaId = updateSaga.id,
                        sagaStatus = updateSaga.status
                    )
                )
            }
        }
    }

    fun onCreateHotelReservationCommand(response: CreateHotelReservationResponse) {
        withSaga(response.sagaId) {
            response.onSuccess {
                this.updateParticipant(
                    Participant.ParticipantName.HOTEL,
                    response.reservationId,
                    Participant.Status.COMPLETED
                ).also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaStatus = it.status,
                            hotelReservationId = response.reservationId,
                            hotelReservationStatus = response.reservationStatus
                        )
                    )
                }
//                applicationEventPublisher.publishEvent(SagaUpdatedEvent(this.id, Participant.ParticipantName.HOTEL))
            }.onFailure {
                this.updateParticipant(
                    Participant.ParticipantName.HOTEL,
                    response.reservationId,
                    Participant.Status.COMPENSATING
                ).markAsCompensating().also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaStatus = it.status,
                            hotelReservationStatus = response.reservationStatus,
                            hotelReservationId = response.reservationId
                        )
                    )
                }
                // TODO("notify failure")
            }
        }
    }

    //    @EventListener
    fun on(response: CreateFlightReservationResponse) {
        withSaga(response.sagaId) {
            response.onSuccess {
                this.updateParticipant(
                    Participant.ParticipantName.FLIGHT,
                    response.reservationId,
                    Participant.Status.COMPLETED
                ).also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaStatus = it.status,
                            flightReservationId = response.reservationId,
                            flightReservationStatus = response.reservationStatus
                        )
                    )
                }
//                applicationEventPublisher.publishEvent(SagaUpdatedEvent(this.id, Participant.ParticipantName.FLIGHT))
            }.onFailure {
                this.updateParticipant(
                    Participant.ParticipantName.FLIGHT,
                    response.reservationId,
                    Participant.Status.COMPENSATING
                ).markAsCompensating().also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaStatus = it.status,
                            flightReservationStatus = response.reservationStatus,
                            flightReservationId = response.reservationId
                        )
                    )
                }
                // TODO("notify failure")
            }
        }
    }

    //    @EventListener
    fun validateSaga(sagaUpdatedEvent: SagaUpdatedEvent) {
        withSaga(sagaUpdatedEvent.sagaId) {
            logger.info("notified by ${sagaUpdatedEvent.from}")
            if (this.participants.any { it.status != Participant.Status.COMPLETED }) {
                logger.info("Saga is not completed")
                // TODO nothing to do
            } else if (this.participants.any { it.status == Participant.Status.COMPENSATING }) {
                logger.warn("Saga is running the compensation flow")
                // TODO send a compensation message to all completed participant
            } else if (this.participants.all { it.status == Participant.Status.COMPLETED }) {
                logger.info("Saga is completed")
                // TODO marks the saga as completed
            }
        }
    }

//    fun onSagaCompletion() {
//        logger.info("Saga completed")
//        logger.info("Sending \"confirm\" commands to Saga participants")
//        hotelProxy.confirm(
//            ConfirmReservationCommand(
//                sagaId = updatedSaga.id,
//                hotelReservationId = updatedSaga.hotelReservationId!!
//            )
//        )
//        tripProxy.confirmTrip(
//            /*
//             * this proxy interaction could make the "trip service" emit a "ConfirmConfirmTripCommand"
//             * and trigger the SagaManager to update the Status at the Event Store,
//             * but since it's a study case, we will not update any FAILED or CONFIRMED events.
//             * Let's keep it simple ok?
//             */
//            ConfirmTripCommand(
//                sagaId = updatedSaga.id,
//                tripId = updatedSaga.tripId!!,
//                hotelReservationId = response.reservationId!!
//            )
//        )
//    }

    @Transactional(propagation = Propagation.REQUIRED)
    private fun getSaga(id: UUID) = sagaRepository.findByIdOrNull(id)
        ?: throw RuntimeException("Cannot find saga with id: $id")

    @Transactional(propagation = Propagation.REQUIRED)
    private fun Saga.save() = sagaRepository.save(this)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private inline fun withNewSaga(participants: List<Participant>, block: Saga.() -> Unit): Saga {
        val saga = Saga(participants = participants.toMutableList())
            .save()
            .also { putSagaIdIntoMdc(it.id) }
            .also { logger.info("A new saga has started!") }
        block(saga)
        return saga
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private inline fun <reified T> withSaga(sagaId: UUID, block: Saga.() -> T): T =
        block(getSaga(sagaId).also { putSagaIdIntoMdc(it.id) })

    @Transactional(propagation = Propagation.REQUIRED)
    fun updateSaga(sagaEvent: SagaUpdatedEvent) {
        withSaga(sagaEvent.sagaId) {
            this.updateParticipant(
                participantName = sagaEvent.from,
                status = sagaEvent.status
            ).save()
        }
    }

    // TODO should it go to its own place?
    private fun buildParticipants(): List<Participant> {
        val trip = Participant(name = Participant.ParticipantName.TRIP)
        val hotel = Participant(name = Participant.ParticipantName.HOTEL)
        val flight = Participant(name = Participant.ParticipantName.FLIGHT)

        return listOf(trip, hotel, flight)
    }
}
