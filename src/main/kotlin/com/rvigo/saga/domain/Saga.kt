package com.rvigo.saga.domain

import com.rvigo.saga.domain.Saga.Status.STARTED
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType.STRING
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "saga")
data class Saga(
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column
    @Enumerated(STRING)
    var status: Status = STARTED,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "saga_id")
    val participants: MutableList<Participant> = mutableListOf()
) {
    fun markAsCompensated() = changeStatusTo(status = Status.COMPENSATED)
    fun markAsCompensating() = changeStatusTo(status = Status.COMPENSATING)
    fun markAsCompleted() = changeStatusTo(status = Status.COMPLETED)

    fun updateParticipant(
        participantName: Participant.ParticipantName,
        participantId: UUID? = null,
        status: Participant.Status? = null
    ): Saga {
        val participant = this.participants.first { it.name == participantName }.update(participantId, status)
        val participants = this.participants.filterNot { it.name == participantName }

        return this.copy(participants = participants.plus(participant).toMutableList())
    }

    private fun changeStatusTo(status: Status): Saga = if (status in this.status.possibleChanges()) {
        copy(status = status)
    } else {
        throw Status.InvalidStatusChangeAttempt(from = this.status, to = status)
    }

    enum class Status {
        STARTED {
            override fun possibleChanges() = setOf(COMPLETED, COMPENSATING)
        },
        COMPLETED {
            override fun possibleChanges() = emptySet<Status>()
        },
        COMPENSATING {
            override fun possibleChanges() = setOf(COMPENSATING)
        },
        COMPENSATED {
            override fun possibleChanges() = emptySet<Status>()
        };

        abstract fun possibleChanges(): Set<Status>
        data class InvalidStatusChangeAttempt(val from: Status, val to: Status) :
            RuntimeException("Cannot change the Saga status from $from to $to")
    }
}
