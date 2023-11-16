package com.rvigo.saga.infra.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import com.rvigo.saga.domain.command.MessageBody
import com.rvigo.saga.domain.messaging.Message
import com.rvigo.saga.domain.messaging.publisher.MessagePublisher
import com.rvigo.saga.infra.logger.logger
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.stereotype.Component

@Component
class SNSPublisher(
    private val notificationMessagingTemplate: NotificationMessagingTemplate,
    private val mapper: ObjectMapper
) : MessagePublisher {
    private val logger by logger()

    override fun publish(message: Message, destination: String) {
        with(message) {
            notificationMessagingTemplate.convertAndSend(destination, body.asString(), attributes).also {
                logger.debug("message {} sent", body)
            }
        }
    }

    private fun MessageBody?.asString() = this?.let { mapper.writeValueAsString(it) } ?: ""
}
