package com.rvigo.saga.infra.aws

data class SNSEvent(val body: Body, val topic: String, val attributes: Map<String, String>) {
    data class Body(val name: String)
}
