package com.rvigo.saga.domain.valueObject

enum class SagaState {
    STARTED {
        override fun possibleChanges() = setOf(COMPLETED, COMPENSATING, COMPENSATED)
    },
    COMPLETED {
        override fun possibleChanges() = emptySet<SagaState>()
    },
    COMPENSATING {
        override fun possibleChanges() = setOf(COMPENSATING, COMPENSATED)
    },
    COMPENSATED {
        override fun possibleChanges() = emptySet<SagaState>()
    };

    abstract fun possibleChanges(): Set<SagaState>

    data class InvalidStateChangeAttempt(val from: SagaState, val to: SagaState) :
        RuntimeException("Cannot change the Saga status from $from to $to")
}
