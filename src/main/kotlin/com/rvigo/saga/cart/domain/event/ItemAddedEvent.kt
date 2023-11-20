package com.rvigo.saga.cart.domain.event

import com.rvigo.saga.cart.domain.model.Quantity
import com.rvigo.saga.lib.domain.message.event.AbstractEvent
import java.util.UUID

data class ItemAddedEvent(
    val cartId: UUID,
    val itemId: Int,
    val quantity: Quantity
) : AbstractEvent(cartId)
