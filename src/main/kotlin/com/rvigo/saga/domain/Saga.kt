package com.rvigo.saga.domain

import com.rvigo.saga.domain.Saga.Status.STARTED
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "saga")
data class Saga(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
