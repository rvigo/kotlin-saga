package com.rvigo.saga.external.tripService.domain.models

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "trip")
data class Trip(
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "hotel_reservation_id")
    var hotelReservationId: UUID? = null,

    @Column(name = "flight_reservation_id")
    var flightReservationId: UUID? = null,

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

