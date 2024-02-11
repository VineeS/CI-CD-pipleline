pipeline {
    agent any

    environment {
        ARTIFACTORY_URL = 'https://your-artifactory-url.com'
        ARTIFACTORY_REPO = 'docker-repo'
        AWS_REGION = 'us-east-1'
        LAMBDA_FUNCTION_NAME = 'lambda-function-for-CI-CD-pipeline'
        S3_BUCKET = 'MyS3Bucket'
        EMR_CLUSTER_NAME = 'emr-cluster'
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image
                    sh 'docker build -t my-docker-image .'
                }
            }
        }

        stage('Push Docker Image to Artifactory') {
            steps {
                script {
                    // Tag Docker image
                    sh 'docker tag my-docker-image $ARTIFACTORY_URL/$ARTIFACTORY_REPO/my-docker-image:latest'
                    
                    // Login to Artifactory
                    withCredentials([usernamePassword(credentialsId: 'artifactory-credentials', usernameVariable: 'ARTIFACTORY_USERNAME', passwordVariable: 'ARTIFACTORY_PASSWORD')]) {
                        sh "docker login -u $ARTIFACTORY_USERNAME -p $ARTIFACTORY_PASSWORD $ARTIFACTORY_URL"
                    }

                    // Push Docker image to Artifactory
                    sh "docker push $ARTIFACTORY_URL/$ARTIFACTORY_REPO/my-docker-image:latest"
                }
            }
        }

        stage('Create Lambda Function') {
            steps {
                script {
                    // Create Lambda function
                    sh "aws lambda create-function --function-name $LAMBDA_FUNCTION_NAME --runtime python3.8 --role arn:aws:iam::123456789012:role/lambda-execution-role --handler lambda_function.lambda_handler --code S3Bucket=my-bucket,S3Key=my-function-code.zip"
                }
            }
        }

        stage('Trigger Lambda on EMR File Event') {
            steps {
                script {
                    // Configure Lambda trigger for EMR event
                    sh "aws events put-rule --name emr-file-event-rule --event-pattern '{\"source\": [\"aws.emr\"],\"detail-type\": [\"EMR Step Status Change\"],\"detail\": {\"state\": [\"COMPLETED\"],\"type\": [\"Spark Step\"]}}'"
                    sh "aws events put-targets --rule emr-file-event-rule --targets 'Id'='1','Arn'='arn:aws:lambda:$AWS_REGION:123456789012:function:$LAMBDA_FUNCTION_NAME'"
                }
            }
        }
    }
}
