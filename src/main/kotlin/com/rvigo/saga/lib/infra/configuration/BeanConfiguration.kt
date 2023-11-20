package com.rvigo.saga.lib.infra.configuration

import com.rvigo.saga.lib.domain.message.command.CommandDispatcher
import com.rvigo.saga.lib.domain.message.command.DefaultCommandDispatcher
import com.rvigo.saga.lib.domain.message.event.DefaultEventDispatcher
import com.rvigo.saga.lib.domain.message.event.EventDispatcher
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun commandDispatcher(): CommandDispatcher = DefaultCommandDispatcher()

    @Bean
    @ConditionalOnMissingBean
    fun eventDispatcher(): EventDispatcher = DefaultEventDispatcher()
}
