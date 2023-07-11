package com.rvigo.saga.infra.eventStore

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SagaEventStoreRepository : JpaRepository<SagaEventStoreEntry, UUID> {
    @Query(
        "SELECT * FROM saga_event_store WHERE saga_id = :sagaId ORDER BY created_at DESC LIMIT 1",
        nativeQuery = true
    )
    fun findLastEntryBySagaId(@Param("sagaId") sagaId: UUID): SagaEventStoreEntry?
}

