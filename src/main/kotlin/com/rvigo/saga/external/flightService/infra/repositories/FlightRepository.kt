package com.rvigo.saga.external.flightService.infra.repositories

import com.rvigo.saga.external.flightService.domain.models.FlightReservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface FlightRepository : JpaRepository<FlightReservation, UUID>
