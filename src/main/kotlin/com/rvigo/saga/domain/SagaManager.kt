package com.rvigo.saga.domain

import com.rvigo.saga.application.proxies.HotelProxy
import com.rvigo.saga.application.proxies.TripProxy
import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationResponse
import com.rvigo.saga.external.hotelService.application.listeners.commands.ifFailure
import com.rvigo.saga.external.hotelService.application.listeners.commands.ifSuccess
import com.rvigo.saga.external.tripService.application.listeners.commands.CompensateCreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.ConfirmTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripCommand
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCanceledResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.TripCreatedResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.ifFailure
import com.rvigo.saga.external.tripService.application.listeners.commands.ifSuccess
import com.rvigo.saga.infra.LoggerUtils.putSagaIdIntoMdc
import com.rvigo.saga.infra.eventStore.SagaEventStoreEntry
import com.rvigo.saga.infra.eventStore.SagaEventStoreManager
import com.rvigo.saga.infra.repositories.SagaRepository
import com.rvigo.saga.logger
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional
class SagaManager(
    private val sagaRepository: SagaRepository,
    private val sagaEventStoreManager: SagaEventStoreManager,
    private val hotelProxy: HotelProxy,
    private val tripProxy: TripProxy,
) {
    private val logger by logger()

    @EventListener
    fun on(command: CreateTripSagaCommand) {
        withNewSaga {
            sagaEventStoreManager.updateEntry(SagaEventStoreEntry(sagaId = id, sagaStatus = status))

            // first saga step
            tripProxy.create(CreateTripCommand(sagaId = id, cpf = command.cpf))
        }
    }

    // second saga step
    @EventListener
    fun on(response: TripCreatedResponse) {
        withSaga(response.sagaId) {
            response.ifSuccess {
                this.updateTripId(response.tripId).save().also {
                    val entry = SagaEventStoreEntry(
                        sagaId = it.id,
                        sagaStatus = it.status,
                        tripId = response.tripId,
                        tripStatus = response.tripStatus
                    )
                    sagaEventStoreManager.updateEntry(entry)
                    logger.info("Saga updated! Creating a reservation")
                    hotelProxy.create(CreateReservationCommand(response.sagaId, response.cpf))
                }
            }.ifFailure {
                // since this is the first step, there is nothing to compensate
                this.markAsCompensated().save().also {
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
    fun on(response: CreateReservationResponse) {
        withSaga(response.sagaId) {
            response.ifSuccess {
                val updatedSaga = this.updateReservationId(response.reservationId).markAsCompleted()
                sagaEventStoreManager.updateEntry(
                    SagaEventStoreEntry(
                        sagaId = updatedSaga.id,
                        sagaStatus = updatedSaga.status,
                        hotelReservationId = response.reservationId,
                        hotelReservationStatus = response.reservationStatus
                    )
                )
                logger.info("Saga completed")
                logger.info("Sending \"confirm\" commands to Saga participants")
                hotelProxy.confirm(
                    ConfirmReservationCommand(
                        sagaId = updatedSaga.id,
                        hotelReservationId = updatedSaga.hotelReservationId!!
                    )
                )
                tripProxy.confirmTrip(
                    /*
                     * this proxy interaction could make the "trip service" emit a "ConfirmConfirmTripCommand"
                     * and trigger the SagaManager to update the Status at the Event Store,
                     * but since it's a study case, we will not update any FAILED or CONFIRMED events.
                     * Let's keep it simple ok?
                     */
                    ConfirmTripCommand(
                        sagaId = updatedSaga.id,
                        tripId = updatedSaga.tripId!!,
                        hotelReservationId = response.reservationId!!
                    )
                )
            }.ifFailure {
                this.markAsCompensating().save().also {
                    sagaEventStoreManager.updateEntry(
                        SagaEventStoreEntry(
                            sagaId = it.id,
                            sagaStatus = it.status,
                            hotelReservationStatus = response.reservationStatus,
                            hotelReservationId = response.reservationId
                        )
                    )
                }
                tripProxy.compensate(CompensateCreateTripCommand(sagaId = id, tripId = tripId!!))
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

    private fun getSaga(id: UUID) = sagaRepository.findByIdOrNull(id)
        ?: throw RuntimeException("Cannot find saga with id: $id")

    private fun Saga.save() = sagaRepository.save(this)

    private fun <T> withNewSaga(block: Saga.() -> T): T {
        val saga = Saga().save().also { putSagaIdIntoMdc(it.id) }
        logger.info("A new saga has started")
        return block(saga)
    }

    private fun <T> withSaga(sagaId: UUID, block: Saga.() -> T) = block(getSaga(sagaId))
}
