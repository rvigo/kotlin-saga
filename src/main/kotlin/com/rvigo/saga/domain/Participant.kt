package com.rvigo.saga.domain

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "participant")
data class Participant(
    @Id
//    @GeneratedValue
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
