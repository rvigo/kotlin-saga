provider "aws" {
  access_key                  = "localstack"
  secret_key                  = "localstack"
  region                      = "us-east-1"
  s3_use_path_style           = true
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    iam = "http://localhost:4566"
    sns = "http://localhost:4566"
    sqs = "http://localhost:4566"
  }
}
