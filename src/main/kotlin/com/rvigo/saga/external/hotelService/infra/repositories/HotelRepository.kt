package com.rvigo.saga.external.hotelService.infra.repositories

import com.rvigo.saga.external.hotelService.domain.models.HotelReservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface HotelRepository : JpaRepository<HotelReservation, UUID>
