package com.rvigo.saga

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class ExecutorApplication

fun main(args: Array<String>) {
    runApplication<ExecutorApplication>(*args)
}

inline fun <reified T> T.logger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(T::class.java) }
