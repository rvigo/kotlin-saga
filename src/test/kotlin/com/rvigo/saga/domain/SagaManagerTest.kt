package com.rvigo.saga.domain

import com.rvigo.saga.application.proxy.HotelProxy
import com.rvigo.saga.builder.SagaBuilder
import com.rvigo.saga.domain.command.CommandResponse
import com.rvigo.saga.domain.command.impl.CreateSagaCommand
import com.rvigo.saga.domain.messaging.publisher.MessagePublisher
import com.rvigo.saga.domain.repository.SagaRepository
import com.rvigo.saga.domain.service.SagaManager
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationBody
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationResponse
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import com.rvigo.saga.external.tripService.application.listeners.commands.CreateTripResponse
import com.rvigo.saga.external.tripService.application.listeners.commands.TripResponseBody
import com.rvigo.saga.external.tripService.domain.model.TripEntity
import com.rvigo.saga.infra.eventStore.SagaEventStoreManager
import com.rvigo.saga.infra.proxy.FlightProxy
import com.rvigo.saga.infra.proxy.TripProxy
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class SagaManagerTest {
    private val saga = SagaBuilder.build()

    private val snsPublisher: MessagePublisher = mockk { justRun { publish(any(), any()) } }
    private val sagaEventStoreManager: SagaEventStoreManager = mockk { justRun { updateEntry(any()) } }
    private val hotelProxy: HotelProxy = mockk { justRun { create(any()) } }
    private val tripProxy: TripProxy = mockk { justRun { create(any()) } }
    private val flightProxy: FlightProxy = mockk { justRun { create(any()) } }
    private val sagaRepository: SagaRepository = mockk {
        every { save(any()) } returns saga
        every { findByIdOrNull(saga.id) } returns saga
    }
    private val targetTopic: String = "target"

    val cpf = "00000000000"

    private var manager: SagaManager = SagaManager(
        sagaRepository,
        sagaEventStoreManager,
        hotelProxy,
        tripProxy,
        flightProxy,
        snsPublisher,
        targetTopic
    )

    @Nested
    inner class CreateSaga {

        @Test
        fun `should create a new saga`() {

            val command = CreateSagaCommand(cpf = cpf)

            justRun { tripProxy.create(any()) }

            manager.create(command)

            verify {
                sagaRepository.save(any())
                snsPublisher.publish(any(), any())
            }
        }
    }

    @Nested
    inner class HandleCreateTripResponse {

        @Nested
        inner class `on success` {

            @Test
            fun `should handle createTripResponse event`() {
                val response = CreateTripResponse(
                    sagaId = saga.id,
                    cpf = cpf,
                    responseStatus = CommandResponse.Status.SUCCESS,
                    body = TripResponseBody(UUID.randomUUID(), TripEntity.TripStatus.CONFIRMED)
                )

                manager.handleCreateTripResponse(response)

                verify { snsPublisher.publish(any(), any()) }
            }
        }

        @Nested
        inner class `on failure` {

            @Test
            fun `should handle error scenario`() {

                val response = CreateTripResponse(
                    sagaId = saga.id,
                    cpf = cpf,
                    responseStatus = CommandResponse.Status.FAILURE,
                    body = TripResponseBody(tripStatus = TripEntity.TripStatus.FAILED)
                )

                manager.handleCreateTripResponse(response)

                verify {
                    sagaRepository.save(any())
                    snsPublisher.publish(any(), any())
                }
            }
        }
    }

    @Nested
    inner class HandleCreateHotelReservationResponse {

        @Nested
        inner class `on success` {

            @Test
            fun `should handle create hotel reservation`() {
                val response = CreateHotelReservationResponse(
                    sagaId = saga.id,
                    responseStatus = CommandResponse.Status.SUCCESS,
                    body = CreateHotelReservationBody(UUID.randomUUID(), HotelReservation.Status.CONFIRMED)
                )

                manager.handleCreateHotelReservationResponse(response)

                verify { snsPublisher.publish(any(), any()) }
            }
        }

        @Nested
        inner class `on failure` {
            @Test
            fun `should handle error scenario`() {
                val response = CreateHotelReservationResponse(
                    sagaId = saga.id,
                    responseStatus = CommandResponse.Status.FAILURE,
                    body = CreateHotelReservationBody(reservationStatus = HotelReservation.Status.FAILED)
                )

                manager.handleCreateHotelReservationResponse(response)

                verify {
                    sagaRepository.save(any())
                    snsPublisher.publish(any(), any())
                }
            }
        }
    }
}
