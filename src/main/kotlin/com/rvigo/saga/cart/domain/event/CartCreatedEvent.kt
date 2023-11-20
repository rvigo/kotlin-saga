package com.rvigo.saga.cart.domain.event

import com.rvigo.saga.lib.domain.message.event.AbstractEvent
import java.util.UUID

data class CartCreatedEvent(
    val cartId: UUID
) : AbstractEvent(cartId)
