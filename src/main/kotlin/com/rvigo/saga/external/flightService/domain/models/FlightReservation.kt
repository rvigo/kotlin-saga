package com.rvigo.saga.external.flightService.domain.models

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
@Table(name = "flight_reservation")
data class FlightReservation(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),
    @Column
    @Enumerated(EnumType.STRING)
    var status: Status = Status.PENDING,
    @Column
    val cpf: String
) {
    enum class Status { PENDING, CONFIRMED, CANCELED, FAILED }
}
