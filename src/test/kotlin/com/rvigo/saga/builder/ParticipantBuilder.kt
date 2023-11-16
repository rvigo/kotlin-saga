package com.rvigo.saga.builder

import com.rvigo.saga.domain.entity.DefaultParticipant
import com.rvigo.saga.domain.valueObject.ParticipantName
import com.rvigo.saga.domain.valueObject.ParticipantState
import java.util.UUID

class ParticipantBuilder {
    var name: ParticipantName = ParticipantName.TRIP
    var id: UUID = UUID.randomUUID()
    var status: ParticipantState = ParticipantState.NONE

    companion object {

        fun build(block: ParticipantBuilder.() -> Unit): DefaultParticipant = ParticipantBuilder().apply(block).build()
    }

    private fun build() = DefaultParticipant(name, id, status)
}
