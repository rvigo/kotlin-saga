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
    fun markAsCompensated() = copy(status = Status.COMPENSATED)
    fun markAsCompensating() = copy(status = Status.COMPENSATING)
    fun markAsCompleted() = copy(status = Status.COMPLETED)
    fun updateTripId(tripId: UUID?) = copy(tripId = tripId)
    fun updateReservationId(reservationId: UUID?) = copy(hotelReservationId = reservationId)

    enum class Status { STARTED, COMPLETED, COMPENSATING, COMPENSATED }
}
