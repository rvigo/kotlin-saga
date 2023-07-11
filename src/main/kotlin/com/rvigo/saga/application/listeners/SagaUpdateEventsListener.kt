package com.rvigo.saga.application.listeners

import com.rvigo.saga.domain.SagaManager
import com.rvigo.saga.infra.listeners.DefaultListener
import com.rvigo.saga.infra.logger.logger
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component


@Component
class SagaUpdateEventsListener(private val sagaManager: SagaManager) : DefaultListener() {

    private val logger by logger()

    @SqsListener("\${cloud.aws.sqs.queues.update-events}", deletionPolicy = ON_SUCCESS)
    fun listenCreateSaga(@Payload message: String) = sagaManager.updateSaga(convertMessage(message))
}
