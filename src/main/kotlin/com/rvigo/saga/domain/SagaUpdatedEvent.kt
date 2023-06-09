package com.rvigo.saga.domain

import java.util.UUID

data class SagaUpdatedEvent(val sagaId: UUID, val from: Participant.ParticipantName)
