# Kotlin Saga Pattern Implementation

This project is a study implementation of the Saga pattern in Kotlin,
based on the concepts described in the book "Microservices Patterns"
and the [cignald](https://github.com/cingaldi)
implementation [here](https://medium.com/geekculture/case-study-a-diy-saga-pattern-implementation-190927220a0a).

## Introduction

The Saga pattern is a design pattern used in distributed systems to manage long-running transactions across multiple
microservices. It provides a way to maintain data consistency and reliability in the face of failures and partial
successes.

This implementation aims to showcase how the Saga pattern using the `ORCHESTRATION STRATEGY` can be implemented in
Kotlin.

## Running the application

```bash
docker-compose up -d \
&& ./gradlew build \
&& ./gradlew bootRun
```

After the app startup:

```bash
curl -X POST -H "Content-Type: application/json" -d '{"cpf": "12345678901"}' http://localhost:8080/saga/trip 
```

## File Structure

<details>
<summary>Click to expand</summary>  

```bash
.
└── main
    ├── kotlin
    │     └── com
    │       └── rvigo
    │           └── saga
    │               ├── SagaApplication.kt
    │               ├── application
    │               │   ├── controllers
    │               │   │   ├── SagaController.kt
    │               │   │   └── dtos
    │               │   │       └── SagaDTO.kt
    │               │   └── proxies
    │               │       ├── HotelProxy.kt
    │               │       └── TripProxy.kt
    │               ├── domain
    │               │   ├── CreateTripSagaCommand.kt
    │               │   ├── Saga.kt
    │               │   └── SagaManager.kt
    │               ├── external
    │               │   ├── hotelService
    │               │   │   ├── application
    │               │   │   │   └── listeners
    │               │   │   │       ├── HotelCommandListener.kt
    │               │   │   │       └── commands
    │               │   │   │           ├── CompensateCreateReservationCommand.kt
    │               │   │   │           ├── CompensateCreateReservationResponse.kt
    │               │   │   │           ├── ConfirmReservationCommand.kt
    │               │   │   │           ├── CreateReservationCommand.kt
    │               │   │   │           └── CreateReservationResponse.kt
    │               │   │   ├── domain
    │               │   │   │   ├── models
    │               │   │   │   │   └── HotelReservation.kt
    │               │   │   │   └── services
    │               │   │   │       └── HotelService.kt
    │               │   │   └── infra
    │               │   │       └── repositories
    │               │   │           └── HotelRepository.kt
    │               │   └── tripService
    │               │       ├── application
    │               │       │   └── listeners
    │               │       │       ├── TripCommandListener.kt
    │               │       │       └── commands
    │               │       │           ├── CompensateCreateTripCommand.kt
    │               │       │           ├── CompensateCreateTripResponse.kt
    │               │       │           ├── ConfirmTripCommand.kt
    │               │       │           ├── CreateTripCommand.kt
    │               │       │           ├── TripCanceledResponse.kt
    │               │       │           └── TripCreatedResponse.kt
    │               │       ├── domain
    │               │       │   ├── models
    │               │       │   │   └── Trip.kt
    │               │       │   └── services
    │               │       │       └── TripService.kt
    │               │       └── infra
    │               │           └── repositories
    │               │               └── TripRepository.kt
    │               └── infra
    │                   ├── LoggerUtils.kt
    │                   ├── eventStore
    │                   │   ├── SagaEventStoreEntry.kt
    │                   │   ├── SagaEventStoreManager.kt
    │                   │   └── SagaEventStoreRepository.kt
    │                   ├── proxies
    │                   │   ├── HotelProxy.kt
    │                   │   └── TripProxy.kt
    │                   └── repositories
    │                       └── SagaRepository.kt
    └── resources
        ├── application.yaml
        ├── db
        │   └── migration
        │       ├── V1__init.sql
        │       ├── V2__saga_trip_and_reservation_id.sql
        │       ├── V3__event_store.sql
        │       ├── V4__drop_default_id.sql
        │       └── V5__drop_default_timestamp.sql
        └── logback.xml
```

</details>

The `SagaController` is the entrypoint of the saga and all `participants` are in the `external` package. The
communication between services is made through spring's `org.springframework.context.event` api
for the sake of simplicity.

## TODO

- [ ] Introduce a Flight Reservation microservice to the implemented Saga
- [ ] Implement Unit and Integration Tests scenarios

###

_PLEASE NOTE_ this is _NOT_ a production ready implementation
