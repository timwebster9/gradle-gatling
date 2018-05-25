pipeline {
    agent {
        kubernetes {
            label 'k8s'
            inheritFrom 'base'
            containerTemplate {
                name 'java'
                image 'openjdk:8-jdk-alpine'
                ttyEnabled true
                command 'cat'
            }
        }
    }
    stages {
        stage('say hello') {
            steps {
                script {
                    sh("./gradlew build")
                }
            }
        }
    }
}
