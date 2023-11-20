package com.rvigo.saga.cart.domain.command

import com.rvigo.saga.lib.domain.message.command.Command
import java.util.UUID

data class CreateCartCommand(
    val cartId: UUID
) : Command
