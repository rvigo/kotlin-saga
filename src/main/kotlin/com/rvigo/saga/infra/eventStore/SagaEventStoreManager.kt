package com.rvigo.saga.infra.eventStore

import org.springframework.stereotype.Component


@Component
class SagaEventStoreManager(private val repository: SagaEventStoreRepository) {

    fun updateEntry(entry: SagaEventStoreEntry) {
        val eventStoreEntry = repository.findLastEntryBySagaId(entry.sagaId)?.let { lastEntry ->
            entry.mergeWithLastEntry(lastEntry)
        } ?: entry

        repository.save(eventStoreEntry)
    }
}
