CREATE TABLE flight_reservation (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    status VARCHAR(14) NOT NULL,
    cpf VARCHAR(14) NOT NULL
);

ALTER TABLE saga
ADD COLUMN flight_reservation_id UUID;

ALTER TABLE saga_event_store
ADD COLUMN flight_reservation_id UUID,
ADD COLUMN flight_reservation_status VARCHAR(14);
