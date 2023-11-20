package com.rvigo.saga.cart.infra.repository

import com.rvigo.saga.cart.domain.model.Cart
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CartRepository : JpaRepository<Cart, UUID> {

    @EntityGraph("Cart.FullLoad")
    @Query(value = """
        SELECT c FROM Cart c
        WHERE c.id = :id
    """)
    fun fullLoad(@Param("id") id: UUID): Cart?
}
