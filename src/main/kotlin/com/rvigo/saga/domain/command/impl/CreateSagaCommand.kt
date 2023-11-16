package com.rvigo.saga.domain.command.impl

import com.rvigo.saga.domain.command.AbstractCommandMessage

// for non-brazilians, "cpf" is the person document number
data class CreateSagaCommand(val cpf: String) : AbstractCommandMessage()
