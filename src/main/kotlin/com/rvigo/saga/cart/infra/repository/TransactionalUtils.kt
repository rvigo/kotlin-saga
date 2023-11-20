package com.rvigo.saga.cart.infra.repository

import jakarta.transaction.Transactional

@Transactional
fun <T> withinTransaction(block: () -> T): T = block()
