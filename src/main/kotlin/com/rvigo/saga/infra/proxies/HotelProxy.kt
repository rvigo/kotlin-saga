package com.rvigo.saga.infra.proxies

import com.rvigo.saga.external.hotelService.application.listeners.commands.CompensateCreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateReservationCommand

interface HotelProxy {
    fun create(createReservationCommand: CreateReservationCommand)
    fun compensate(compensateCreateReservationCommand: CompensateCreateReservationCommand)
}
