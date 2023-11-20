package com.rvigo.saga.cart.infra.repository

import com.rvigo.saga.cart.domain.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Int>
