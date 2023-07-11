resource "aws_sns_topic" "sns-saga-saga-events-topic" {
  name = var.sns_saga_saga_events_topic_name
}
