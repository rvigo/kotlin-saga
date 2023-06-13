package com.rvigo.saga.external.hotelService.domain.models

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "hotel_reservation")
data class HotelReservation(
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),
    @Column
    @Enumerated(EnumType.STRING)
    var status: Status = Status.PENDING,
    @Column
    val cpf: String
) {
    enum class Status { PENDING, CONFIRMED, CANCELED, FAILED }
}
