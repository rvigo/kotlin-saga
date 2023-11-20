package com.rvigo.saga.cart.application

import com.fasterxml.jackson.annotation.JsonProperty

data class CartItemDetailResponse(
    @JsonProperty("product_name")
    val productName: String,
    val quantity: Int,
    @JsonProperty("total_price")
    val totalPrice: Double
)
