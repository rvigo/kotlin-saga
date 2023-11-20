package com.rvigo.saga.cart.application

import com.fasterxml.jackson.annotation.JsonProperty
import com.rvigo.saga.cart.domain.model.Quantity
import java.util.UUID

data class AddItemRequest(
    @JsonProperty("cart_id")
    val cartId: UUID,
    @JsonProperty("item_id")
    val itemId: Int,
    val quantity: Quantity
)
