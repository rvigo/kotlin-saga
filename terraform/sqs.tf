resource "aws_sqs_queue" "sqs-saga-create-saga-command-queue" {
  name = var.sqs_saga_create_saga_command_queue_name
}

resource "aws_sqs_queue" "sqs-saga-create-trip-command-queue" {
  name = var.sqs_saga_create_trip_command_queue_name
}

resource "aws_sqs_queue" "sqs-saga-create-trip-response-queue" {
  name = var.sqs_saga_create_trip_response_queue_name
}

resource "aws_sqs_queue" "sqs-saga-update-events-queue" {
  name = var.sqs_saga_update_events_queue_name
}

resource "aws_sqs_queue" "sqs-saga-create-hotel-reservation-command-queue" {
  name = var.sqs_saga_create_hotel_reservation_command_queue_name
}

resource "aws_sqs_queue" "sqs-saga-create-hotel-reservation-response-queue" {
  name = var.sqs_saga_create_hotel_reservation_response_queue_name
}

resource "aws_sqs_queue" "sqs-saga-create-flight-reservation-command-queue" {
  name = var.sqs_saga_create_flight_reservation_command_queue_name
}

resource "aws_sqs_queue" "sqs-saga-create-flight-reservation-response-queue" {
  name = var.sqs_saga_create_flight_reservation_response_queue_name
}