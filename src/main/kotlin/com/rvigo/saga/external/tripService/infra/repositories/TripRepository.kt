package com.rvigo.saga.external.tripService.infra.repositories

import com.rvigo.saga.external.tripService.domain.models.Trip
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TripRepository : JpaRepository<Trip, UUID>
