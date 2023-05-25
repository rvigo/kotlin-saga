package com.rvigo.saga.infra.eventStore

import com.rvigo.saga.logger
import org.springframework.stereotype.Component

@Component
class SagaEventStoreManager(private val eventStoreRepository: SagaEventStoreRepository) {
    private val logger by logger()
    fun updateEntry(entry: SagaEventStoreEntry) {
        val eventStoreEntry = eventStoreRepository.findLastEntryBySagaId(entry.sagaId)?.let { lastEntry ->
            entry.mergeWithLastEntry(lastEntry)
        } ?: entry

        eventStoreRepository.save(eventStoreEntry)
    }
}
