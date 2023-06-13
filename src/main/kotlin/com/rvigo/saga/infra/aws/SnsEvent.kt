package com.rvigo.saga.infra.aws

data class SnsEvent(val body: SnsEventBody, val topic: String, val attributes: Map<String, String>) {
    interface SnsEventBody
}

const val EVENT_TYPE_HEADER = "EVENT_TYPE"
