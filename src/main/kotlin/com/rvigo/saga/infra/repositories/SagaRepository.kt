package com.rvigo.saga.infra.repositories

import com.rvigo.saga.domain.Saga
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface SagaRepository : JpaRepository<Saga, UUID>
