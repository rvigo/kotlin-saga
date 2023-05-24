package com.rvigo.saga.external.hotelService.domain.models

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
@Table(name = "hotel_reservation")
class HotelReservation(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @Column
    @Enumerated(EnumType.STRING)
    var status: Status = Status.PENDING,
    @Column
    val cpf: String) {

    enum class Status { PENDING, CONFIRMED, CANCELED }
}
