package com.rvigo.saga.domain.valueObject

enum class ParticipantState {
    NONE {
        override fun possibleChanges(): Set<ParticipantState> = setOf(PROCESSING, COMPENSATING)
    },
    PROCESSING {
        override fun possibleChanges(): Set<ParticipantState> = setOf(COMPLETED, COMPENSATING)
    },
    COMPLETED {
        override fun possibleChanges(): Set<ParticipantState> = emptySet()
    },
    COMPENSATING {
        override fun possibleChanges(): Set<ParticipantState> = setOf(COMPENSATED)
    },
    COMPENSATED {
        override fun possibleChanges(): Set<ParticipantState> = emptySet()
    };

    abstract fun possibleChanges(): Set<ParticipantState>

    data class InvalidStatusChangeAttempt(val from: ParticipantState, val to: ParticipantState) :
        RuntimeException("Cannot change the Participant Status from $from to $to")
}

