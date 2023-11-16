package com.rvigo.saga.builder

import com.rvigo.saga.domain.entity.DefaultSaga
import com.rvigo.saga.domain.entity.Participant
import com.rvigo.saga.domain.valueObject.ParticipantName
import com.rvigo.saga.domain.valueObject.ParticipantState
import com.rvigo.saga.domain.valueObject.SagaState
import java.util.UUID

class SagaBuilder {

    var id: UUID = UUID.randomUUID()

    var status: SagaState = SagaState.STARTED

    var participants: MutableList<Participant> = buildParticipants()

    companion object {
        fun build(block: SagaBuilder.() -> Unit = {}) = SagaBuilder().apply(block).buildSaga()
    }


    private fun buildSaga(): DefaultSaga = DefaultSaga(id, status, participants)

    private fun buildParticipants(): MutableList<Participant> {
        val trip = ParticipantBuilder.build { name = ParticipantName.TRIP }
        val hotel = ParticipantBuilder.build { name = ParticipantName.HOTEL }
        val flight = ParticipantBuilder.build { name = ParticipantName.FLIGHT }

        return mutableListOf(trip, hotel, flight)
    }
}

fun DefaultSaga.withUpdatedParticipant(name: ParticipantName, status: ParticipantState): DefaultSaga {
    val participant = this.participants.first { it.name == name }
    val newParticipant = ParticipantBuilder.build {
        this.name = name
        this.status = status
    }
    val participants = participants.filterNot { it.name == participant.name }
    return this.copy(id = this.id, participants = participants.plus(newParticipant).toMutableList())
}

