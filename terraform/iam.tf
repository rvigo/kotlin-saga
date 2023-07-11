resource "aws_sqs_queue_policy" "sqs-queue-policy" {
  queue_url = aws_sqs_queue.sqs-saga-create-saga-command-queue.id
  policy    = <<POLICY
{
  "Version": "2012-10-17",
  "Id": "sqspolicy",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "sqs:*",
      "Resource": "${aws_sqs_queue.sqs-saga-create-saga-command-queue.id}"
    }
  ]
}
POLICY
}

