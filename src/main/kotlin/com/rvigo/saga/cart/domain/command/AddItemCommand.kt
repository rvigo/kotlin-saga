package com.rvigo.saga.cart.domain.command

import com.rvigo.saga.cart.domain.model.Quantity
import com.rvigo.saga.lib.domain.message.command.Command
import java.util.UUID

data class AddItemCommand(
    val cartId: UUID,
    val itemId: Int,
    val quantity: Quantity
) : Command
