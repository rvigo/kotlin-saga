ALTER TABLE trip ALTER id DROP DEFAULT;
ALTER TABLE hotel_reservation ALTER id DROP DEFAULT;
ALTER TABLE saga ALTER id DROP DEFAULT;

ALTER TABLE saga_event_store ALTER event_id DROP DEFAULT;
