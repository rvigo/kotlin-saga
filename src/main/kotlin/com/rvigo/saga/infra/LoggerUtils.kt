package com.rvigo.saga.infra

import org.slf4j.MDC
import java.util.UUID


object LoggerUtils {
    fun putSagaIdIntoMdc(sagaId: UUID) {
        MDC.put("sagaId", sagaId.toString())
    }
}
