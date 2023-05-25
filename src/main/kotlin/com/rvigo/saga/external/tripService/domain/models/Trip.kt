package com.rvigo.saga.external.tripService.domain.models

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
data class Trip(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "hotel_reservation_id")
    var hotelReservationId: UUID? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var status: TripStatus = TripStatus.PENDING,

    @Column
    var cpf: String
) {
    fun cancel() {
        if (this.status != TripStatus.CONFIRMED)
            this.status = TripStatus.CANCELED
    }

    enum class TripStatus {
        PENDING, CONFIRMED, CANCELED, REJECTED, FAILED
    }
}

