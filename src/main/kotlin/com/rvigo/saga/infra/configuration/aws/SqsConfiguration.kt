package com.rvigo.saga.infra.configuration.aws

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary


@EnableSqs
@Configuration
class SqsConfiguration {
    @Bean
    @Primary
    fun amazonSQSAsync(
        @Value("\${cloud.aws.credentials.secret-key}")
        secretKey: String,
        @Value("\${cloud.aws.credentials.access-key}")
        accessKey: String,
        @Value("\${cloud.aws.endpoint}")
        endpoint: String,
        @Value("\${cloud.aws.region.static}")
        region: String
    ): AmazonSQSAsync = AmazonSQSAsyncClientBuilder.standard()
        .withEndpointConfiguration(EndpointConfiguration(endpoint, region))
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
        .build()

    @Bean
    fun queueMessageTemplate(amazonSqs: AmazonSQSAsync): QueueMessagingTemplate {
        return QueueMessagingTemplate(amazonSqs)
    }
}
