CREATE TABLE saga_event_store (
    event_id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    saga_id UUID NOT NULL REFERENCES saga(id),
    saga_status VARCHAR(14) NOT NULL,
    trip_id UUID,
    trip_status VARCHAR(14),
    hotel_reservation_id UUID,
    hotel_reservation_status VARCHAR(14),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
