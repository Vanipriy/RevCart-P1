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
            user: 'ubuntu',
            identity: 'ec2-key',
            allowAnyHosts: true
        ],
        command: "echo 'Deployment Success'"
    }
}

    }
}
