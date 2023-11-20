package com.rvigo.saga.cart.domain.event

import java.util.UUID

data class CartEmptyEvent(val cartId: UUID)
