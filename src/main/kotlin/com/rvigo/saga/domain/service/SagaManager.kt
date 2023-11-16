package com.rvigo.saga.domain.service

import com.rvigo.saga.application.proxy.HotelProxy
import com.rvigo.saga.domain.command.impl.CreateSagaCommand
import com.rvigo.saga.domain.command.onFailure
import com.rvigo.saga.domain.command.onSuccess
import com.rvigo.saga.domain.entity.DefaultParticipant
import com.rvigo.saga.domain.entity.DefaultSaga
import com.rvigo.saga.domain.entity.Participant
import com.rvigo.saga.domain.entity.Saga
import com.rvigo.saga.domain.messaging.impl.ParticipantInfo
import com.rvigo.saga.domain.messaging.impl.SagaUpdatedEvent
import com.rvigo.saga.domain.messaging.publisher.MessagePublisher
import com.rvigo.saga.domain.repository.SagaRepository
import com.rvigo.saga.domain.valueObject.ParticipantName
import com.rvigo.saga.domain.valueObject.ParticipantName.FLIGHT
import com.rvigo.saga.domain.valueObject.ParticipantName.HOTEL
import com.rvigo.saga.domain.valueObject.ParticipantName.TRIP
import com.rvigo.saga.domain.valueObject.ParticipantState
import com.rvigo.saga.domain.valueObject.ParticipantState.COMPENSATING
import com.rvigo.saga.domain.valueObject.ParticipantState.COMPLETED
import com.rvigo.saga.domain.valueObject.ParticipantState.PROCESSING
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripResponse
import com.rvigo.saga.infra.LoggerUtils.putSagaIdIntoMdc
import com.rvigo.saga.infra.eventStore.SagaEventStoreEntry
import com.rvigo.saga.infra.eventStore.SagaEventStoreManager
import com.rvigo.saga.infra.logger.logger
import com.rvigo.saga.infra.proxy.FlightProxy
import com.rvigo.saga.infra.proxy.TripProxy
import com.rvigo.saga.infra.repository.withinTransaction
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.UUID

// TODO this class should be an interface
@Component
class SagaManager(
    private val sagaRepository: SagaRepository,
    private val sagaEventStoreManager: SagaEventStoreManager,
    private val hotelProxy: HotelProxy,
    private val tripProxy: TripProxy,
    private val flightProxy: FlightProxy,
    private val publisher: MessagePublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    private val targetTopic: String
) {
    private val logger by logger()

    fun create(message: CreateSagaCommand) {
        val participants = buildParticipants()

        withNewSaga(participants) {

            sagaEventStoreManager.updateEntry(SagaEventStoreEntry(sagaId = id, sagaState = state))
            // trigger first saga step
            tripProxy.create(CreateTripCommand(id, message.cpf))

            notifySagaUpdate(TRIP, PROCESSING)
        }
    }

    // TODO each handler should be implemented in your own service
    fun handleCreateTripResponse(response: CreateTripResponse) {
        withSaga(response.sagaId) {
            response.onSuccess {
                logger.info("Trip created")

                hotelProxy.create(CreateHotelReservationCommand(response.sagaId, response.cpf))
                flightProxy.create(CreateFlightReservationCommand(response.sagaId, response.cpf))

                sagaEventStoreManager.updateEntry(
                    SagaEventStoreEntry(
                        sagaId = id,
                        sagaState = state,
                        tripStatus = response.body.tripStatus,
                        tripId = response.body.tripId
                    )
                )

                notifySagaUpdate(TRIP, COMPLETED)
            }.onFailure {

                val updatedSaga = this.updateParticipant(
                    TRIP,
                    response.body.tripId,
                    COMPENSATING
                )

                updatedSaga.markAsCompensated().andSave()

                sagaEventStoreManager.updateEntry(
                    SagaEventStoreEntry(
                        sagaId = updatedSaga.id,
                        sagaState = updatedSaga.state
                    )
                )

                notifySagaUpdate(TRIP, COMPENSATING).also { logger.info("updating saga") }
            }
        }
    }

    fun handleCreateHotelReservationResponse(response: CreateHotelReservationResponse) {
        withSaga(response.sagaId) {
            response.onSuccess {
                val updatedParticipant = this.updateParticipant(
                    HOTEL,
                    response.body.reservationId,
                    COMPLETED
                )

                sagaEventStoreManager.updateEntry(
                    SagaEventStoreEntry(
                        sagaId = updatedParticipant.id,
                        sagaState = updatedParticipant.state,
                        hotelReservationId = response.body.reservationId,
                        hotelReservationStatus = response.body.reservationStatus
                    )
                )

                notifySagaUpdate(HOTEL, COMPLETED)
//                applicationEventPublisher.publishEvent(SagaUpdatedEvent(this.id, Participant.ParticipantName.HOTEL))
            }.onFailure {
                val updatedParticipant = this.updateParticipant(
                    HOTEL,
                    response.body.reservationId,
                    COMPENSATING
                )

                updatedParticipant.markAsCompensating().andSave()

                sagaEventStoreManager.updateEntry(
                    SagaEventStoreEntry(
                        sagaId = updatedParticipant.id,
                        sagaState = updatedParticipant.state,
                        hotelReservationStatus = response.body.reservationStatus,
                        hotelReservationId = response.body.reservationId
                    )
                )

                notifySagaUpdate(HOTEL, COMPENSATING)
            }
        }
    }

    fun handleCreateFlightReservationResponse(response: CreateFlightReservationResponse) {
        withSaga(response.sagaId) {
            response.onSuccess {
                this.updateParticipant(
                    FLIGHT,
                    response.body!!.reservationId,
                    COMPLETED
                ).also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaState = it.state,
                            flightReservationId = response.body.reservationId,
                            flightReservationStatus = response.body.reservationStatus
                        )
                    )
                }.also {
                    notifySagaUpdate(FLIGHT, COMPLETED)
                }
            }.onFailure {
                this.updateParticipant(
                    FLIGHT,
                    response.body!!.reservationId,
                    COMPENSATING
                ).markAsCompensating().also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaState = it.state,
                            flightReservationStatus = response.body.reservationStatus,
                            flightReservationId = response.body.reservationId
                        )
                    )
                }.also {
                    notifySagaUpdate(FLIGHT, COMPENSATING)
                }
            }
        }
    }

    //    @EventListener
    fun validateSaga(sagaUpdatedEvent: SagaUpdatedEvent) {
        withSaga(sagaUpdatedEvent.sagaId) {
            logger.info("notified by ${sagaUpdatedEvent.body.from}")
            if (this.participants.any { it.state != COMPLETED }) {
                logger.info("Saga is not completed")
                // TODO nothing to do
            } else if (this.participants.any { it.state == COMPENSATING }) {
                logger.warn("Saga is running the compensation flow")
                // TODO send a compensation message to all completed participant
            } else if (this.participants.all { it.state == COMPLETED }) {
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

    private fun getSaga(id: UUID) = sagaRepository.findByIdOrNull(id)
        ?: throw RuntimeException("Cannot find saga with id: $id")

    private fun Saga.andSave() = withinTransaction { sagaRepository.save(this) }

    private fun withNewSaga(participants: List<Participant>, block: Saga.() -> Unit): Saga = withinTransaction {
        val saga = participants.fold(DefaultSaga()) { s: Saga, p: Participant ->
            s.appendParticipant(p)
        }

        saga.andSave()
            .also { putSagaIdIntoMdc(it.id) }
            .also { logger.info("A new saga has started!") }
        block(saga)

        saga
    }

    private fun <T> withSaga(sagaId: UUID, block: Saga.() -> T): T =
        block(getSaga(sagaId).also { putSagaIdIntoMdc(it.id) })


    // TODO updateSaga or updateSagaParticipant????
    fun updateSaga(sagaEvent: SagaUpdatedEvent) {
        withSaga(sagaEvent.sagaId) {
            val updatedSaga = this.updateParticipant(
                name = sagaEvent.body.from,
                status = sagaEvent.body.status
            ).andSave()
            logger.info("Saga updated to $updatedSaga")
        }
    }

    // TODO notifySagaUpdate or notifySagaParticipantUpdate????
    fun Saga.notifySagaUpdate(from: ParticipantName, status: ParticipantState) {
        publisher.publish(
            SagaUpdatedEvent(
                sagaId = id,
                body = ParticipantInfo(from, status)
            ),
            targetTopic)
    }

    // TODO should it go to its own place?
    private fun buildParticipants(): List<Participant> {
        val trip = DefaultParticipant(name = TRIP)
        val hotel = DefaultParticipant(name = HOTEL)
        val flight = DefaultParticipant(name = FLIGHT)

        return listOf(trip, hotel, flight)
    }
}
