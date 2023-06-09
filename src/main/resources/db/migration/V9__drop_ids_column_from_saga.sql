--this is a HUGE CHANGE so, in a production environment, a data migration should be applied
ALTER TABLE saga
DROP COLUMN trip_id,
DROP COLUMN hotel_reservation_id,
DROP COLUMN flight_reservation_id;
