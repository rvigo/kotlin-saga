package com.rvigo.saga.domain

import com.rvigo.saga.application.proxies.FlightProxy
import com.rvigo.saga.application.proxies.HotelProxy
import com.rvigo.saga.application.proxies.TripProxy
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.ifFailure
import com.rvigo.saga.external.tripService.application.listeners.commands.ifSuccess
import com.rvigo.saga.infra.LoggerUtils.putSagaIdIntoMdc
import com.rvigo.saga.infra.eventStore.SagaEventStoreEntry
import com.rvigo.saga.infra.eventStore.SagaEventStoreManager
import com.rvigo.saga.infra.events.ifFailure
import com.rvigo.saga.infra.events.ifSuccess
import com.rvigo.saga.infra.repositories.ParticipantRepository
import com.rvigo.saga.infra.repositories.SagaRepository
import com.rvigo.saga.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Component
class SagaManager(
    private val sagaRepository: SagaRepository,
    private val sagaEventStoreManager: SagaEventStoreManager,
    private val participantRepository: ParticipantRepository,
    private val hotelProxy: HotelProxy,
    private val tripProxy: TripProxy,
    private val flightProxy: FlightProxy,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val logger by logger()

    @EventListener
    fun start(command: CreateTripSagaCommand) {
        val participants = buildParticipants().let { participantRepository.saveAll(it) }
        withNewSaga(participants) {
            sagaEventStoreManager.updateEntry(SagaEventStoreEntry(sagaId = id, sagaStatus = status))

            // first saga step
            tripProxy.create(CreateTripCommand(sagaId = id, cpf = command.cpf)).also {
                this.updateParticipant(
                    participantName = Participant.ParticipantName.TRIP,
                    status = Participant.Status.PROCESSING
                ).save()
            }
        }
    }

    @EventListener
    fun on(response: TripCreatedResponse) {
        withSaga(response.sagaId) {
            response.ifSuccess {
                this.updateParticipant(
                    Participant.ParticipantName.TRIP,
                    response.tripId,
                    Participant.Status.COMPLETED
                ).save().also { saga ->
                    val entry = SagaEventStoreEntry(
                        sagaId = saga.id,
                        sagaStatus = saga.status,
                        tripId = response.tripId,
                        tripStatus = response.tripStatus
                    )
                    sagaEventStoreManager.updateEntry(entry).also {
                        logger.info("Saga updated!")
                        hotelProxy.create(CreateReservationCommand(response.sagaId, response.cpf))
                        flightProxy.create(CreateFlightReservationCommand(response.sagaId, response.cpf))
                    }
                }
                applicationEventPublisher.publishEvent(SagaUpdatedEvent(this.id, Participant.ParticipantName.TRIP))
            }.ifFailure {
                // since this is the first step, there is nothing to compensate
                this.updateParticipant(
                    Participant.ParticipantName.TRIP,
                    response.tripId,
                    Participant.Status.COMPENSATING
                ).markAsCompensated().save().also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaStatus = it.status
                        )
                    )
                }
            }
        }
    }

    @EventListener
    fun on(response: CreateHotelReservationResponse) {
        withSaga(response.sagaId) {
            response.ifSuccess {
                this.updateParticipant(
                    Participant.ParticipantName.HOTEL,
                    response.reservationId,
                    Participant.Status.COMPLETED
                ).save().also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaStatus = it.status,
                            hotelReservationId = response.reservationId,
                            hotelReservationStatus = response.reservationStatus
                        )
                    )
                }
                applicationEventPublisher.publishEvent(SagaUpdatedEvent(this.id, Participant.ParticipantName.HOTEL))
            }.ifFailure {
                this.updateParticipant(
                    Participant.ParticipantName.HOTEL,
                    response.reservationId,
                    Participant.Status.COMPENSATING
                ).markAsCompensating().save().also {
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

    @EventListener
    fun on(response: CreateFlightReservationResponse) {
        withSaga(response.sagaId) {
            response.ifSuccess {
                this.updateParticipant(
                    Participant.ParticipantName.FLIGHT,
                    response.reservationId,
                    Participant.Status.COMPLETED
                ).save().also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaStatus = it.status,
                            flightReservationId = response.reservationId,
                            flightReservationStatus = response.reservationStatus
                        )
                    )
                }
                applicationEventPublisher.publishEvent(SagaUpdatedEvent(this.id, Participant.ParticipantName.FLIGHT))
            }.ifFailure {
                this.updateParticipant(
                    Participant.ParticipantName.FLIGHT,
                    response.reservationId,
                    Participant.Status.COMPENSATING
                ).markAsCompensating().save().also {
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

    @EventListener
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

    private fun getSaga(id: UUID) = sagaRepository.findByIdOrNull(id)
        ?: throw RuntimeException("Cannot find saga with id: $id")

    private fun Saga.save() = sagaRepository.save(this)

    private fun <T> withNewSaga(participants: List<Participant>, block: Saga.() -> T): T {
        val saga = Saga(participants = participants.toMutableList()).save().also { putSagaIdIntoMdc(it.id) }
        logger.info("A new saga has started")
        return block(saga)
    }

    private fun <T> withSaga(sagaId: UUID, block: Saga.() -> T) = block(getSaga(sagaId))

    // TODO should it go to its own place?
    private fun buildParticipants(): List<Participant> {
        val trip = Participant(name = Participant.ParticipantName.TRIP)
        val hotel = Participant(name = Participant.ParticipantName.HOTEL)
        val flight = Participant(name = Participant.ParticipantName.FLIGHT)

        return listOf(trip, hotel, flight)
    }
}
