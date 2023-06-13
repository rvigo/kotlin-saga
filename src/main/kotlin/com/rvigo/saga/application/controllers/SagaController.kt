package com.rvigo.saga.application.controllers

import com.rvigo.saga.application.controllers.dtos.SagaDTO
import com.rvigo.saga.domain.CreateSagaCommand
import com.rvigo.saga.domain.SagaManager
import com.rvigo.saga.infra.aws.SnsEvent.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/saga/trip")
class SagaController(val manager: SagaManager) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTrip(@RequestBody sagaDTO: SagaDTO): SagaDTO {
        manager.createSaga(CreateSagaCommand(sagaDTO.cpf))
        return sagaDTO
    }
}
