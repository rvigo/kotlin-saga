
variable "sqs_saga_create_saga_command_queue_name" {
  description = "Name of the sqs queue to be created"
  default     = "saga-create-saga-command-queue"
}

variable "sns_saga_saga_events_topic_name" {
  description = "Name of the sns topic to be created"
  default     = "saga-saga-events-topic"
}

variable "sqs_saga_create_trip_command_queue_name" {
  description = "Name of the sqs queue to be created"
  default     = "saga-create-trip-command-queue"
}

variable "sqs_saga_create_trip_response_queue_name" {
  description = "Name of the sqs queue to be created"
  default     = "saga-create-trip-response-queue"
}

variable "sqs_saga_update_events_queue_name" {
  description = "Name of the sqs queue to be created"
  default     = "saga-update-events-queue"
}

variable "sqs_saga_create_hotel_reservation_command_queue_name" {
  default     = "saga-create-hotel-reservation-command-queue"
}

variable "sqs_saga_create_hotel_reservation_response_queue_name" {
  default     = "saga-create-hotel-reservation-response-queue"
}

variable "sqs_saga_create_flight_reservation_command_queue_name" {
  default     = "saga-create-flight-reservation-command-queue"
}

variable "sqs_saga_create_flight_reservation_response_queue_name" {
  default     = "saga-create-flight-reservation-response-queue"
}
