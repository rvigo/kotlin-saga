package com.rvigo.saga.application.controllers

import com.rvigo.saga.application.controllers.dtos.SagaDTO
import com.rvigo.saga.infra.aws.SNSEvent
import com.rvigo.saga.infra.aws.SNSEvent.*
import com.rvigo.saga.infra.aws.SNSPublisher
import com.rvigo.saga.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/saga/trip")
class SagaController(
    val publisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.start-saga}")
    val topic: String
) {
    private val logger by logger()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTrip(@RequestBody sagaDTO: SagaDTO): SagaDTO {
        logger.info("topic: $topic")

        val event = SNSEvent(Body("test"), topic, mapOf("sender" to "controller"))
        
        publisher.publish(event)
        logger.info("message sent")

        return sagaDTO
    }
}


