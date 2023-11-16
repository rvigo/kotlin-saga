package com.rvigo.saga.domain.entity

import com.rvigo.saga.domain.valueObject.ParticipantName
import com.rvigo.saga.domain.valueObject.ParticipantState
import com.rvigo.saga.domain.valueObject.SagaState
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.util.UUID

@Entity
@Table(name = "saga")
data class DefaultSaga(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override val id: UUID = UUID.randomUUID(),

    @Column
    @Enumerated(STRING)
    override var state: SagaState = SagaState.STARTED,

    @ElementCollection
    @CollectionTable(name = "participant")
    @JoinColumn(name = "saga_id", referencedColumnName = "id")
    override val participants: List<Participant> = emptyList()
) : Saga {

    @Version
    override val version: Int = 1

    override fun markAsCompensated() = changeStateTo(state = SagaState.COMPENSATED)

    override fun markAsCompensating() = changeStateTo(state = SagaState.COMPENSATING)

    override fun markAsCompleted() = changeStateTo(state = SagaState.COMPLETED)

    override fun appendParticipant(participant: Participant): Saga =
        this.copy(participants = participants.plus(participant))

    override fun updateParticipant(
        name: ParticipantName,
        participantId: UUID?,
        status: ParticipantState
    ): Saga {
        val participant = this.participants.first { it.name == name }
        val updatedParticipant = participant.update(participantId, status)
        val participants = this.participants.filterNot { it.name == name }

        return this.copy(participants = participants.plus(updatedParticipant))
    }

    private fun changeStateTo(state: SagaState): Saga = if (state in this.state.possibleChanges()) {
        copy(state = state)
    } else {
        throw SagaState.InvalidStateChangeAttempt(from = this.state, to = state)
    }
}
