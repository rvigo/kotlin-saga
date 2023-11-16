package com.rvigo.saga.domain.entity

import com.rvigo.saga.domain.valueObject.ParticipantName
import com.rvigo.saga.domain.valueObject.ParticipantState
import java.util.UUID

interface Participant {

    val name: ParticipantName

    val id: UUID?

    val state: ParticipantState

    val version: Int

    fun update(id: UUID? = null, status: ParticipantState? = null): Participant
}
