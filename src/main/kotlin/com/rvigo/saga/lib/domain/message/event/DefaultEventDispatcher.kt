package com.rvigo.saga.lib.domain.message.event

import com.rvigo.saga.cart.infra.logger.logger
import org.springframework.stereotype.Component

@Component
class DefaultEventDispatcher : EventDispatcher {
    private val logger by logger()

    override fun <T> emit(event: T) {
        logger.info("emitting event: $event")
    }
}
