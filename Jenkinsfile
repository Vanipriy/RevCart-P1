pipeline {
    agent any

    environment {
        GIT_BRANCH = "main"
        GIT_URL    = "https://github.com/Vanipriy/RevCart-P1.git"

        EC2_HOST = "ubuntu@16.112.70.145"

        DB_HOST     = "revcart-db.cvs2i6k829o9.ap-south-2.rds.amazonaws.com"
        DB_NAME     = "revcart-db"
        DB_USER     = "admin"
        DB_PASSWORD = "Vanipriya"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: "${GIT_BRANCH}", url: "${GIT_URL}"
            }
        }

        stage('Build Backend JAR') {
    steps {
        dir('backend') {
            bat 'mvn clean package -DskipTests'
        }
    }
}


        stage('Copy Files to EC2') {
            steps {
                sshagent(['ec2-ssh-key']) {
                    bat '''
                      ssh -o StrictHostKeyChecking=no ${EC2_HOST} "mkdir -p /home/ubuntu/app && rm -rf /home/ubuntu/app/*"

                      scp -o StrictHostKeyChecking=no -r backend ${EC2_HOST}:/home/ubuntu/app/
                      scp -o StrictHostKeyChecking=no -r frontend ${EC2_HOST}:/home/ubuntu/app/
                    '''
                }
            }
        }

        stage('Build & Run Containers on EC2') {
            steps {
                sshagent(['ec2-ssh-key']) {
                    bat '''
                      ssh -o StrictHostKeyChecking=no ${EC2_HOST} "
                        cd /home/ubuntu/app/backend &&
                        docker build -t backend-app . &&
                        docker stop backend-app || true &&
                        docker rm backend-app || true &&
                        docker run -d -p 8080:8080 --name backend-app \
                          -e DB_HOST=${DB_HOST} \
                          -e DB_NAME=${DB_NAME} \
                          -e DB_USER=${DB_USER} \
                          -e DB_PASSWORD=${DB_PASSWORD} \
                          backend-app
                      "

                      ssh -o StrictHostKeyChecking=no ${EC2_HOST} "
                        cd /home/ubuntu/app/frontend &&
                        docker build -t frontend-app . &&
                        docker stop frontend-app || true &&
                        docker rm frontend-app || true &&
                        docker run -d -p 80:80 --name frontend-app frontend-app
                      "
                    '''
                }
            }
        }
    }
}
