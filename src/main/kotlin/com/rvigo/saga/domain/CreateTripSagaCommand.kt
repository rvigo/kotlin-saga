package com.rvigo.saga.domain

// for non-brazilians, "cpf" is the person document number
data class CreateTripSagaCommand(val cpf: String)
