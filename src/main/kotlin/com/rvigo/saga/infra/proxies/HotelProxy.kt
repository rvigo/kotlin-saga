package com.rvigo.saga.infra.proxies

import com.rvigo.saga.external.hotelService.application.listeners.commands.CompensateCreateReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.ConfirmReservationCommand
import com.rvigo.saga.external.hotelService.application.listeners.commands.CreateHotelReservationCommand

interface HotelProxy {
    fun create(createHotelReservationCommand: CreateHotelReservationCommand)
    fun compensate(compensateCreateReservationCommand: CompensateCreateReservationCommand)
    fun confirm(confirmReservationCommand: ConfirmReservationCommand)
}
