package com.rvigo.saga.domain.repository

import com.rvigo.saga.domain.entity.Saga
import java.util.UUID

interface SagaRepository {
    fun save(saga: Saga): Saga

    fun findByIdOrNull(id: UUID): Saga?
}
