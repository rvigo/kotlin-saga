package com.rvigo.saga.infra.repository

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional(propagation = Propagation.REQUIRES_NEW)
fun <T> withinTransaction(block: () -> T): T = block()
