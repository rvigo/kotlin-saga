package com.rvigo.saga.application.proxy

import com.rvigo.saga.domain.messaging.Message
import com.rvigo.saga.domain.messaging.publisher.MessagePublisher
import com.rvigo.saga.infra.proxy.TripProxy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class TripProxy(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val publisher: MessagePublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    val topic: String
) : TripProxy {

    override fun create(message: Message) {
        publisher.publish(
            message,
            topic,
        )
    }

    override fun compensate(message: Message) {
        applicationEventPublisher.publishEvent(message)
    }

    override fun confirm(message: Message) {
        applicationEventPublisher.publishEvent(message)
    }
}
