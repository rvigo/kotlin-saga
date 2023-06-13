package com.rvigo.saga.domain

import com.rvigo.saga.infra.aws.SnsEvent

// for non-brazilians, "cpf" is the person document number
data class CreateSagaCommand(val cpf: String) : SnsEvent.SnsEventBody
