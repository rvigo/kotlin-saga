package com.rvigo.saga.domain

import com.rvigo.saga.domain.Saga.Status.STARTED
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "saga")
data class Saga(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column
    @Enumerated(STRING)
    var status: Status = STARTED,

    @Column(name = "trip_id")
    var tripId: UUID? = null,
    
    @Column(name = "hotel_reservation_id")
    var hotelReservationId: UUID? = null,

    @Column(name = "flight_reservation_id")
    var flightReservationId: UUID? = null
) {
    fun markAsCompensated() = changeStatusTo(status = Status.COMPENSATED)
    fun markAsCompensating() = changeStatusTo(status = Status.COMPENSATING)
    fun markAsCompleted() = changeStatusTo(status = Status.COMPLETED)
    fun updateTripId(tripId: UUID?) = copy(tripId = tripId)
    fun updateHotelReservationId(reservationId: UUID?) = copy(hotelReservationId = reservationId)
    fun updateFlightReservationId(reservationId: UUID?) = copy(flightReservationId = reservationId)

    private fun changeStatusTo(status: Status): Saga = if (status in this.status.possibleChanges()) {
        copy(status = status)
    } else {
        throw Status.InvalidStatusChangeAttempt(from = this.status, to = status)
    }

    enum class Status {
        STARTED {
            override fun possibleChanges() = setOf(COMPLETED, COMPENSATING)
        },
        COMPLETED {
            override fun possibleChanges() = emptySet<Status>()
        },
        COMPENSATING {
            override fun possibleChanges() = setOf(COMPENSATING)
        },
        COMPENSATED {
            override fun possibleChanges() = emptySet<Status>()
        };

        abstract fun possibleChanges(): Set<Status>
        data class InvalidStatusChangeAttempt(val from: Status, val to: Status) :
            RuntimeException("Cannot change the Saga status from $from to $to")
    }
}
