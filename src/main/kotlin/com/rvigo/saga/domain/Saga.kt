package com.rvigo.saga.domain

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("saga")
data class Saga(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,
    @Column
    @Enumerated(EnumType.STRING)
    val status: Status = Status.STARTED,
) {
    fun markAsCompensated() = this.copy(status = Status.COMPENSATED)
    fun markAsCompensating() = this.copy(status = Status.COMPENSATING)
    fun markAsCompleted() = this.copy(status = Status.COMPLETED)
    enum class Status { STARTED, COMPLETED, COMPENSATING, COMPENSATED }
}
