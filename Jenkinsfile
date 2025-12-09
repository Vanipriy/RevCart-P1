pipeline {
    agent any

    environment {
        DB_HOST = 'revcart-db.cvs2i6k829o9.ap-south-2.rds.amazonaws.com'
        DB_NAME = 'revcartdb'
        DB_USER = 'admin'
        DB_PASSWORD = 'Vanipriya'
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
                dir('backend') {
                    withMaven(maven: 'M3') {
                        bat "mvn clean package -DskipTests"
                    }
                }
            }
        }

        stage("Test SSH") {
            steps {
                sshCommand remote: [
                    name: 'EC2-Server',
                    host: '16.112.70.145',
                    user: 'ubuntu',
                    credentialsId: 'ec2-ssh-key',
                    allowAnyHosts: true
                ], command: "echo 'SSH Success from Jenkins'"
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshCommand remote: [
                    name: 'EC2-Server',
                    host: '16.112.70.145',
                    port: 22,
                    user: 'ubuntu',
                    credentialsId: 'ec2-ssh-key',
                    allowAnyHosts: true
                ], command: '''
                    set -e
                    sudo apt-get update -y
                    sudo apt-get install -y docker.io git

                    mkdir -p /home/ubuntu/app
                    cd /home/ubuntu/app

                    if [ -d "RevCart-P1/.git" ]; then
                        cd RevCart-P1
                        git pull origin main
                    else
                        rm -rf RevCart-P1
                        git clone https://github.com/Vanipriy/RevCart-P1.git
                        cd RevCart-P1
                    fi

                    cd backend
                    sudo docker build -t backend-app .
                    sudo docker stop backend-app || true
                    sudo docker rm backend-app || true
                    sudo docker run -d -p 8080:8080 --name backend-app \
                        -e DB_HOST=$DB_HOST -e DB_NAME=$DB_NAME -e DB_USER=$DB_USER -e DB_PASSWORD=$DB_PASSWORD backend-app

                    cd ../frontend
                    sudo docker build -t frontend-app .
                    sudo docker stop frontend-app || true
                    sudo docker rm frontend-app || true
                    sudo docker run -d -p 80:80 --name frontend-app frontend-app
                '''
            }
        }
    }
}
