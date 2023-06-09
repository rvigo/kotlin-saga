package com.rvigo.saga.domain

import com.rvigo.saga.application.proxies.FlightProxy
import com.rvigo.saga.application.proxies.HotelProxy
import com.rvigo.saga.application.proxies.TripProxy
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationCommand
import com.rvigo.saga.external.flightService.application.listeners.commands.CreateFlightReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCanceledResponse
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
) {
    private val logger by logger()

    @EventListener
    fun on(command: CreateTripSagaCommand) {
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

    // second saga step
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

    // saga third step
    @EventListener
    fun on(response: CreateHotelReservationResponse) {
        withSaga(response.sagaId) {
            response.ifSuccess {
                val updatedSaga = this.updateParticipant(
                    Participant.ParticipantName.HOTEL,
                    response.reservationId,
                    Participant.Status.COMPLETED
                )

                updatedSaga.save()
                sagaEventStoreManager.updateEntry(
                    SagaEventStoreEntry(
                        sagaId = updatedSaga.id,
                        sagaStatus = updatedSaga.status,
                        hotelReservationId = response.reservationId,
                        hotelReservationStatus = response.reservationStatus
                    )
                )
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
                // tripProxy.compensate(CompensateCreateTripCommand(sagaId = id, tripId = tripId!!))
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
//                tripProxy.compensate(CompensateCreateTripCommand(sagaId = id, tripId = tripId!!))
            }
        }
    }

    // first compensation response
    @EventListener
    fun on(response: TripCanceledResponse) {
        withSaga(response.sagaId) {
            this.markAsCompensated().also {
                logger.info("Saga marked as compensated")
            }
        }
    }

    fun onSagaCompletion() {

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
    }

    private fun getSaga(id: UUID) = sagaRepository.findByIdOrNull(id)
        ?: throw RuntimeException("Cannot find saga with id: $id")

    private fun Saga.save() = sagaRepository.save(this)

    private fun <T> withNewSaga(participants: List<Participant>, block: Saga.() -> T): T {
        val saga = Saga(participants = participants.toMutableList()).save().also { putSagaIdIntoMdc(it.id) }
        logger.info("A new saga has started")
        return block(saga)
    }

    private fun <T> withSaga(sagaId: UUID, block: Saga.() -> T) = block(getSaga(sagaId))

    // TODO should go to its own place?
    private fun buildParticipants(): List<Participant> {
        val trip = Participant(name = Participant.ParticipantName.TRIP)
        val hotel = Participant(name = Participant.ParticipantName.HOTEL)
        val flight = Participant(name = Participant.ParticipantName.FLIGHT)

        return listOf(trip, hotel, flight)
    }
}
