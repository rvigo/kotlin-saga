package com.rvigo.saga.application.proxy

import com.rvigo.saga.domain.messaging.Message
import com.rvigo.saga.infra.proxy.HotelProxy
import com.rvigo.saga.infra.publisher.SNSPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class HotelProxy(
    private val publisher: ApplicationEventPublisher,
    private val snsPublisher: SNSPublisher,
    @Value("\${cloud.aws.sns.topics.saga-events}")
    val topic: String
) : HotelProxy {

    override fun create(message: Message) {
        snsPublisher.publish(message, topic)
    }

    override fun compensate(message: Message) {
        publisher.publishEvent(message)
    }

    override fun confirm(message: Message) {
        publisher.publishEvent(message)
    }
}
