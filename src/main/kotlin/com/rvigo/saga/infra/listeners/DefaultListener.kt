package com.rvigo.saga.infra.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.springframework.beans.factory.annotation.Autowired

open class DefaultListener {

    @Autowired
    protected lateinit var mapper: ObjectMapper
    protected inline fun <reified T> convertMessage(value: String): T =
        mapper.readValue(value, jacksonTypeRef<T>())
}
