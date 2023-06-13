package com.rvigo.saga.domain

import com.rvigo.saga.infra.aws.SnsEvent
import java.util.UUID

data class SagaUpdatedEvent(val sagaId: UUID, val from: Participant.ParticipantName, val status: Participant.Status) :
    SnsEvent.SnsEventBody
