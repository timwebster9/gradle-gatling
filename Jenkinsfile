pipeline {

  agent {
    kubernetes {
      label 'k8s'
      defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: some-label-value
spec:
  containers:
  - name: java
    image: openjdk:8-jdk-alpine
    command:
    - cat
    tty: true
  - name: gradle
    image: gradle:4.7-jre8
    command:
    - cat
    tty: true
  - name: kubectl
    image: roffe/kubectl:v1.9.6
    command:
    - cat
    tty: true
  - name: docker
    image: docker:18.05.0-ce-git
    volumeMounts:
      - mountPath: /var/run/docker.sock
        name: docker-socket
    command:
    - cat
    tty: true
  volumes:
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock

"""
    }
  }
    stages {
        stage('Gradle Build') {
            steps {
                container('java') {
                    sh './gradlew build'
                }
            }
        }
        stage('Docker Build') {
            steps {
                container('docker') {
                   sh 'docker build -t timw/boot-app:1.0 .'
                }
            }
        }
        stage('Deploy App') {
            steps {
                container('kubectl') {
                   sh 'kubectl config set-cluster k8s --server=https://kubernetes.default.svc'
                   sh 'kubectl apply -f spec.yaml'
                }
            }
        }
        stage('Wait for App') {
            steps {
                timeout(5) {
                    waitUntil {
                       script {
                         def r = sh script: 'wget -q http://boot-app-service.default -O /dev/null', returnStatus: true
                         return (r == 0);
                       }
                    }
                }
            }
        }
        stage('Performance Test') {
            steps {
                container('java') {
                    withEnv(['BASE_URL=http://boot-app-service.default']) {
                        sh './gradlew gatlingRun'
                    }
                }
            }
        }
        stage('Tear Down') {
            steps {
                container('kubectl') {
                   sh 'kubectl config set-cluster k8s --server=https://kubernetes.default.svc'
                   sh 'kubectl delete -f spec.yaml'
                }
            }
        }


        /*
        stage('Gradle Build') {
            agent {
                kubernetes {
                    label 'java'
                    inheritFrom 'base'
                    containerTemplate {
                        name 'java'
                        image 'openjdk:8-jdk-alpine'
                        ttyEnabled true
                        command 'cat'
                    }
                }
            }
            steps {
                script {
                    sh("./gradlew build")
                }
            }
        }

        stage('Docker Build') {
            agent {
                kubernetes {
                    label 'docker'
                    inheritFrom 'base'
                    containerTemplate {
                        name 'docker'
                        image 'docker:18.05.0-ce-git'
                        ttyEnabled true
                        command 'cat'
                    }
                }
            }
            steps {
                timeout(5) {
                    waitUntil {
                       script {
                         return false;
                       }
                    }
                }
                //script {
                //	sh("docker build -t timw/gradle-gatling .")
                //}
            }
        }
        stage('Start App') {
            steps {
                script {
                    sh("./gradlew bootRun")
                }
            }
        }
        stage('Wait for App') {
            steps {
                timeout(5) {
                    waitUntil {
                       script {
                         def r = sh script: 'wget -q http://localhost:8080 -O /dev/null', returnStatus: true
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
        */
    }
}
