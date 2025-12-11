pipeline {
    agent any
    
    environment { 
        AWS_REGION = 'ap-south-2' 
        ECR_REGISTRY = '744640651616.dkr.ecr.ap-south-2.amazonaws.com' 
        BACKEND_REPO = 'revcart-backend' 
        FRONTEND_REPO = 'revcart-frontend' 
        EC2_HOST = '40.192.87.17'
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
                echo 'Images pushed to ECR successfully!'
                echo 'Backend: 744640651616.dkr.ecr.ap-south-2.amazonaws.com/revcart-backend:latest'
                echo 'Frontend: 744640651616.dkr.ecr.ap-south-2.amazonaws.com/revcart-frontend:latest'
                echo 'SSH to EC2 and run: /home/ubuntu/deploy.sh'
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
