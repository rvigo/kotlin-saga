package com.rvigo.saga.infra.eventStore

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SagaEventStoreRepository : JpaRepository<SagaEventStoreEntry, UUID> {
    @Query("SELECT s FROM SagaEventStoreEntry s WHERE s.sagaId = :sagaId ORDER BY s.createAt DESC LIMIT 1")
    fun findLastEntryBySagaId(@Param("sagaId") sagaId: UUID): SagaEventStoreEntry?
}

