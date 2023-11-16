package com.rvigo.saga.external.tripService.domain.model

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
@Table(name = "trip")
data class TripEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "hotel_reservation_id")
    var hotelReservationId: UUID? = null,

    @Column(name = "flight_reservation_id")
    var flightReservationId: UUID? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var state: TripStatus = TripStatus.PENDING,

    @Column
    var cpf: String
) {
    fun cancel() {
        if (this.state != TripStatus.CONFIRMED)
            this.state = TripStatus.CANCELED
    }

    enum class TripStatus {
        PENDING, CONFIRMED, CANCELED, REJECTED, FAILED
    }
}

