CREATE TABLE participant (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(20) NOT NULL,
    status VARCHAR(14) NOT NULL,
    saga_id UUID,
    participant_id UUID
);


