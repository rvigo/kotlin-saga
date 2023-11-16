package com.rvigo.saga.infra.repository

import com.rvigo.saga.domain.entity.DefaultSaga
import com.rvigo.saga.domain.repository.SagaRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface SagaRepositoryImpl : SagaRepository, JpaRepository<DefaultSaga, UUID>
