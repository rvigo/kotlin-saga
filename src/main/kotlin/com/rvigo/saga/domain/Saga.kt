package com.rvigo.saga.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
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
    @Enumerated(EnumType.STRING)
    var status: Status = Status.STARTED,

    @Column(name = "trip_id")
    var tripId: UUID? = null,
    @Column(name = "hotel_reservation_id")
    var hotelReservationId: UUID? = null,
) {
    fun markAsCompensated(): Saga {
        this.status = Status.COMPENSATED
        return this
    }

    fun markAsCompensating(): Saga {
        this.status = Status.COMPENSATING
        return this
    }

    fun markAsCompleted(): Saga {
        this.status = Status.COMPLETED
        return this
    }

    fun updateTripId(tripId: UUID?) = this.copy(tripId = tripId)

    fun updateReservationId(reservationId: UUID?) = this.copy(hotelReservationId = reservationId)

    enum class Status { STARTED, COMPLETED, COMPENSATING, COMPENSATED }
}
