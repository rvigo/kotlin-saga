resource "aws_sns_topic_subscription" "sns-saga-saga-events-topic_sqs-saga-create-saga-command-queue_subscription" {
  topic_arn            = aws_sns_topic.sns-saga-saga-events-topic.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs-saga-create-saga-command-queue.arn
  raw_message_delivery = true
  filter_policy        = jsonencode({ "EVENT_TYPE" : ["START_SAGA"] })
}

resource "aws_sns_topic_subscription" "sns-saga-saga-events-topic_sqs-saga-create-trip-command-queue_subscription" {
  topic_arn            = aws_sns_topic.sns-saga-saga-events-topic.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs-saga-create-trip-command-queue.arn
  raw_message_delivery = true
  filter_policy        = jsonencode({ "EVENT_TYPE" : ["CREATE_TRIP"] })
}

resource "aws_sns_topic_subscription" "sns-saga-saga-events-topic_sqs-saga-create-trip-response-queue_subscription" {
  topic_arn            = aws_sns_topic.sns-saga-saga-events-topic.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs-saga-create-trip-response-queue.arn
  raw_message_delivery = true
  filter_policy        = jsonencode({ "EVENT_TYPE" : ["CREATE_TRIP_RESPONSE"] })
}

resource "aws_sns_topic_subscription" "sns-saga-saga-events-topic_sqs-saga-update-events-queue_subscription" {
  topic_arn            = aws_sns_topic.sns-saga-saga-events-topic.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs-saga-update-events-queue.arn
  raw_message_delivery = true
  filter_policy        = jsonencode({ "EVENT_TYPE" : ["UPDATE_EVENT"] })
}

resource "aws_sns_topic_subscription" "sns-saga-saga-events-topic_sqs-saga-create-hotel-reservation-command-queue_subscription" {
  topic_arn            = aws_sns_topic.sns-saga-saga-events-topic.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs-saga-create-hotel-reservation-command-queue.arn
  raw_message_delivery = true
  filter_policy        = jsonencode({ "EVENT_TYPE" : ["CREATE_HOTEL_RESERVATION"] })
}

resource "aws_sns_topic_subscription" "sns-saga-saga-events-topic_sqs-saga-create-hotel-reservation-response-queue_subscription" {
  topic_arn            = aws_sns_topic.sns-saga-saga-events-topic.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs-saga-create-hotel-reservation-response-queue.arn
  raw_message_delivery = true
  filter_policy        = jsonencode({ "EVENT_TYPE" : ["CREATE_HOTEL_RESERVATION_RESPONSE"] })
}

resource "aws_sns_topic_subscription" "sns-saga-saga-events-topic_sqs-saga-create-flight-reservation-command-queue_subscription" {
  topic_arn            = aws_sns_topic.sns-saga-saga-events-topic.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs-saga-create-flight-reservation-command-queue.arn
  raw_message_delivery = true
  filter_policy        = jsonencode({ "EVENT_TYPE" : ["CREATE_FLIGHT_RESERVATION"] })
}

resource "aws_sns_topic_subscription" "sns-saga-saga-events-topic_sqs-saga-create-flight-reservation-response-queue_subscription" {
  topic_arn            = aws_sns_topic.sns-saga-saga-events-topic.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs-saga-create-flight-reservation-response-queue.arn
  raw_message_delivery = true
  filter_policy        = jsonencode({ "EVENT_TYPE" : ["CREATE_FLIGHT_RESERVATION_RESPONSE"] })
}
