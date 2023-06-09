package com.rvigo.saga.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "participant")
data class Participant(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column
    @Enumerated(EnumType.STRING)
    val name: ParticipantName,
    @Column(name = "participant_id")
    val participantId: UUID? = null,
    @Column
    @Enumerated(EnumType.STRING)
    val status: Status = Status.NONE,
    @ManyToOne
    @JoinColumn(name = "saga_id", insertable = false, updatable = false)
    val saga: Saga? = null
) {
    fun update(participantId: UUID? = null, status: Status? = null) =
        copy(saga = this.saga, participantId = participantId ?: this.participantId, status = status ?: this.status)

    enum class ParticipantName { TRIP, HOTEL, FLIGHT }
    enum class Status { NONE, PROCESSING, COMPLETED, COMPENSATING, COMPENSATED }
}
