pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-2'
        ECR_REGISTRY = '744640651616.dkr.ecr.ap-south-2.amazonaws.com'
        BACKEND_REPO = 'rev-backend'
        FRONTEND_REPO = 'rev-frontend'
        EC2_HOST = '98.130.47.246'
        EC2_USER = 'ubuntu'
        RDS_ENDPOINT = 'revcart-db.cvs2i6k829o9.ap-south-2.rds.amazonaws.com'
        RDS_USERNAME = 'admin'
        GOOGLE_CLIENT_ID = '366076975270-k4d44ag99mpp9irv7jqgqqdiicfo35fe.apps.googleusercontent.com'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Vanipriy/RevCart-P1.git',
                    credentialsId: 'github-credentials'
            }
        }

        stage('Build Backend JAR') {
            steps {
                withCredentials([
                    string(credentialsId: 'db-password', variable: 'RDS_PASSWORD'),
                    string(credentialsId: 'google-client-secret', variable: 'GOOGLE_CLIENT_SECRET')
                ]) {
                    dir('backend') {
                        withMaven(maven: 'M3') {
                            bat "mvn clean package -DskipTests"
                        }
                    }
                }
            }
        }

        stage('Build & Push Backend to ECR') {
            steps {
                withCredentials([
                    string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY')
                ]) {
                    dir('backend') {
                        bat """
                            aws configure set aws_access_key_id %AWS_ACCESS_KEY_ID%
                            aws configure set aws_secret_access_key %AWS_SECRET_ACCESS_KEY%
                            aws configure set region %AWS_REGION%
                            
                            aws ecr get-login-password --region %AWS_REGION% | docker login --username AWS --password-stdin %ECR_REGISTRY%
                            
                            docker build -t %BACKEND_REPO% .
                            docker tag %BACKEND_REPO%:latest %ECR_REGISTRY%/%BACKEND_REPO%:latest
                            docker push %ECR_REGISTRY%/%BACKEND_REPO%:latest
                        """
                    }
                }
            }
        }

        stage('Build & Push Frontend to ECR') {
            steps {
                withCredentials([
                    string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY')
                ]) {
                    dir('revcart-frontend') {
                        bat """
                            aws ecr get-login-password --region %AWS_REGION% | docker login --username AWS --password-stdin %ECR_REGISTRY%
                            
                            docker build -t %FRONTEND_REPO% .
                            docker tag %FRONTEND_REPO%:latest %ECR_REGISTRY%/%FRONTEND_REPO%:latest
                            docker push %ECR_REGISTRY%/%FRONTEND_REPO%:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                        string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY'),
                        string(credentialsId: 'db-password', variable: 'RDS_PASSWORD'),
                        string(credentialsId: 'google-client-secret', variable: 'GOOGLE_CLIENT_SECRET'),
                        sshUserPrivateKey(credentialsId: 'ec2-ssh-key', keyFileVariable: 'SSH_KEY')
                    ]) {
                        bat """
                            ssh -o StrictHostKeyChecking=no -i %SSH_KEY% %EC2_USER%@%EC2_HOST% ^
                            "export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} && ^
                            export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} && ^
                            export RDS_ENDPOINT=${RDS_ENDPOINT} && ^
                            export RDS_USERNAME=${RDS_USERNAME} && ^
                            export RDS_PASSWORD=${RDS_PASSWORD} && ^
                            export GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} && ^
                            export GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} && ^
                            /home/ubuntu/deploy.sh"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}
