package com.rvigo.saga.cart.domain.event

import com.rvigo.saga.lib.domain.message.event.AbstractEvent
import java.util.UUID

data class CartEmptyEvent(val cartId: UUID) : AbstractEvent(cartId)
