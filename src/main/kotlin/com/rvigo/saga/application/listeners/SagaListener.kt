package com.rvigo.saga.application.listeners

import com.rvigo.saga.domain.SagaManager
import com.rvigo.saga.infra.listeners.DefaultListener
import com.rvigo.saga.infra.logger.logger
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component


@Component
class SagaListener(private val sagaManager: SagaManager) : DefaultListener() {

    private val logger by logger()

    @SqsListener("\${cloud.aws.sqs.queues.create-saga}", deletionPolicy = ON_SUCCESS)
    fun listenCreateSaga(@Payload message: String) = sagaManager.createSaga(convertMessage(message))

    @SqsListener("\${cloud.aws.sqs.queues.create-trip-response}", deletionPolicy = ON_SUCCESS)
    fun listenCreateTripResponse(@Payload message: String) =
        sagaManager.handleCreateTripResponse(convertMessage(message))

    @SqsListener("\${cloud.aws.sqs.queues.create-hotel-reservation-response}", deletionPolicy = ON_SUCCESS)
    fun listenCreateHotelReservationResponse(@Payload message: String) =
        sagaManager.handleCreateHotelReservationResponse(convertMessage(message))

    @SqsListener("\${cloud.aws.sqs.queues.create-flight-reservation-response}", deletionPolicy = ON_SUCCESS)
    fun listenCreateFlightReservationResponse(@Payload message: String) =
        sagaManager.handleCreateFlightReservationResponse(convertMessage(message))
}
