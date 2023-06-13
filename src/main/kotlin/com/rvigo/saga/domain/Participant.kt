package com.rvigo.saga.domain

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated


@Embeddable
data class Participant(
    @Column
    @Enumerated(EnumType.STRING)
    val name: ParticipantName,
    @Column(name = "participant_id")
    val participantId: UUID? = null,
    @Column
    @Enumerated(EnumType.STRING)
    val status: Status = Status.NONE,
) {
    fun update(participantId: UUID? = null, status: Status? = null) =
        copy(
            name = this.name,
            participantId = participantId ?: this.participantId,
            status = status ?: this.status,
        )

    enum class ParticipantName { TRIP, HOTEL, FLIGHT }
    enum class Status { NONE, PROCESSING, COMPLETED, COMPENSATING, COMPENSATED }
}
