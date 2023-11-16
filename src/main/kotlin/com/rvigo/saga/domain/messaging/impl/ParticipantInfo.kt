package com.rvigo.saga.domain.messaging.impl

import com.rvigo.saga.domain.command.MessageBody
import com.rvigo.saga.domain.valueObject.ParticipantName
import com.rvigo.saga.domain.valueObject.ParticipantState

data class ParticipantInfo(val from: ParticipantName, val status: ParticipantState) : MessageBody
