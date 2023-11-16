package com.rvigo.saga.application.controller

import com.rvigo.saga.application.controller.request.SagaRequest
import com.rvigo.saga.domain.command.impl.CreateSagaCommand
import com.rvigo.saga.domain.service.SagaManager
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
    fun createTrip(@RequestBody sagaRequest: SagaRequest): SagaRequest {
        manager.create(CreateSagaCommand(sagaRequest.cpf))
        return sagaRequest
    }
}
