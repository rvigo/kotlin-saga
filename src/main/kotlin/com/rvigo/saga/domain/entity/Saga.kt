package com.rvigo.saga.domain.entity

import com.rvigo.saga.domain.valueObject.ParticipantName
import com.rvigo.saga.domain.valueObject.ParticipantState
import com.rvigo.saga.domain.valueObject.SagaState
import java.util.UUID

interface Saga {

    val id: UUID

    val state: SagaState

    val participants: List<Participant>

    val version: Int

    fun markAsCompensated(): Saga

    fun markAsCompensating(): Saga

    fun markAsCompleted(): Saga

    fun appendParticipant(participant: Participant): Saga

    fun updateParticipant(
        name: ParticipantName,
        participantId: UUID? = null,
        status: ParticipantState
    ): Saga
}
