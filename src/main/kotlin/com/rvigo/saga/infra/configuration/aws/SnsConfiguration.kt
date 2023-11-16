package com.rvigo.saga.infra.configuration.aws

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.config.annotation.EnableSns
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@EnableSns
@Configuration
class SnsConfiguration {
    @Bean
    @Primary
    fun snsClient(
        @Value("\${cloud.aws.credentials.secret-key}")
        secretKey: String,
        @Value("\${cloud.aws.credentials.access-key}")
        accessKey: String,
        @Value("\${cloud.aws.endpoint}")
        endpoint: String,
        @Value("\${cloud.aws.region.static}")
        region: String
    ): AmazonSNS = AmazonSNSClientBuilder.standard()
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
        .build()

    @Bean
    fun notificationMessagingTemplate(amazonSNS: AmazonSNS): NotificationMessagingTemplate =
        NotificationMessagingTemplate(amazonSNS)
}
