package com.rvigo.saga.infra.eventStore

import com.rvigo.saga.domain.Saga
import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import com.rvigo.saga.external.tripService.domain.models.Trip
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "saga_event_store")
data class SagaEventStoreEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id")
    val id: UUID = UUID.randomUUID(),
    @Column(name = "saga_id")
    val sagaId: UUID,
    @Column(name = "saga_status")
    @Enumerated(EnumType.STRING)
    val sagaStatus: Saga.Status,
    @Column(name = "trip_id")
    val tripId: UUID? = null,
    @Column(name = "trip_status")
    @Enumerated(EnumType.STRING)
    val tripStatus: Trip.TripStatus? = null,
    @Column(name = "hotel_reservation_id")
    val hotelReservationId: UUID? = null,
    @Column(name = "hotel_reservation_status")
    @Enumerated(EnumType.STRING)
    val hotelReservationStatus: HotelReservation.Status? = null,
    @Column(name = "created_at")
    @CreationTimestamp
    val createAt: LocalDateTime = LocalDateTime.now(),
) {
    fun mergeWithLastEntry(lastEntry: SagaEventStoreEntry): SagaEventStoreEntry {
        return this.copy(
            id = UUID.randomUUID(),
            tripId = this.tripId ?: lastEntry.tripId,
            tripStatus = this.tripStatus ?: lastEntry.tripStatus,
            hotelReservationId = this.hotelReservationId ?: lastEntry.hotelReservationId,
            hotelReservationStatus = this.hotelReservationStatus ?: lastEntry.hotelReservationStatus
        )
    }
}
