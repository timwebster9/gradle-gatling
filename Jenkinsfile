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
		stage('Start App') {
			steps {
				script {
					sh("./gradlew bootRun &")
				}
			}
		}
		stage('Wait for App') {
			steps {
				timeout(5) {
                    waitUntil {
                       script {
                         def r = sh script: 'wget -q http://locahost:8080 -O /dev/null', returnStatus: true
                         return (r == 0);
                       }
                    }
                }
			}
		}
		stage('Performance Test') {
			steps {
				script {
					sh("./gradlew gatlingRun")
				}
			}
		}
    }
}
