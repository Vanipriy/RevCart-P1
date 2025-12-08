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

    }
}
