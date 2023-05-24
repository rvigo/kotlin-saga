CREATE EXTENSION "uuid-ossp";

CREATE TABLE trip (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    hotel_reservation_id UUID,
    status VARCHAR(14) NOT NULL,
    cpf VARCHAR(11) NOT NULL
);

CREATE TABLE hotel_reservation (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    status VARCHAR(14) NOT NULL,
    cpf VARCHAR(14) NOT NULL
);

CREATE TABLE saga (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    status VARCHAR(14) NOT NULL
);
