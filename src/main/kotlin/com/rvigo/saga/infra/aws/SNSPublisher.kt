package com.rvigo.saga.infra.aws

import com.fasterxml.jackson.databind.ObjectMapper
import com.rvigo.saga.logger
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.stereotype.Component

@Component
class SNSPublisher(private val notificationMessagingTemplate: NotificationMessagingTemplate) {

    private val mapper = ObjectMapper()

    private val logger by logger()
    fun publish(event: SNSEvent) {
        val body = mapper.writeValueAsString(event.body)
        logger.debug("message $body sent")
        notificationMessagingTemplate.convertAndSend(event.topic, body, event.attributes)
    }
}
