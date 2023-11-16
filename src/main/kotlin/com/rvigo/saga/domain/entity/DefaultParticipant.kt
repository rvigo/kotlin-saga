package com.rvigo.saga.domain.entity

import com.rvigo.saga.domain.valueObject.ParticipantName
import com.rvigo.saga.domain.valueObject.ParticipantState
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Version
import java.util.UUID


@Embeddable
data class DefaultParticipant(
    @Column
    @Enumerated(EnumType.STRING)
    override val name: ParticipantName,
    @Column(name = "participant_id")
    override val id: UUID? = null,
    @Column
    @Enumerated(EnumType.STRING)
    override val state: ParticipantState = ParticipantState.NONE
) : Participant {

    @Version
    override val version: Int = 1

    override fun update(id: UUID?, status: ParticipantState?): Participant =
        id?.let {
            this.updateId(it)
        } ?: this.let {
            status?.let { newStatus -> this.changeStatusTo(newStatus) } ?: it
        }

    private fun updateId(id: UUID) = copy(id = id) //TODO repensar atualizacao de ID

    private fun changeStatusTo(status: ParticipantState): DefaultParticipant {
        if (status `not in` this.state.possibleChanges()) {
            throw ParticipantState.InvalidStatusChangeAttempt(from = this.state, to = status)
        }
        return copy(state = status)
    }

    private infix fun <T> T.`not in`(collection: Collection<T>) = this !in collection
}
