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
        stage('Build') {
            steps {
                script {
                    sh("./gradlew build")
                }
            }
        }
		stage('Run App') {
			steps {
				script {
					sh("./gradlew bootRun &")
				}
			}
		}
    }
}
