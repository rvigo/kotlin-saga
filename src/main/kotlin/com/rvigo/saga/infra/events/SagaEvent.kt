package com.rvigo.saga.infra.events

enum class SagaEvent {
    START_SAGA,
    CREATE_TRIP,
    CREATE_TRIP_RESPONSE,
    CREATE_HOTEL_RESERVATION,
    CREATE_HOTEL_RESERVATION_RESPONSE,
    CREATE_FLIGHT_RESERVATION,
    CREATE_FLIGHT_RESERVATION_RESPONSE
}
