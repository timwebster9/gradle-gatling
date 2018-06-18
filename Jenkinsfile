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
  - name: alpine
    image: alpine:3.7
    command:
    - cat
    tty: true
  - name: java
    image: openjdk:8-jdk-alpine
    command:
    - cat
    tty: true
  - name: kubectl
    image: roffe/kubectl:v1.9.6
    command:
    - cat
    tty: true
  - name: azcopy
    image: timwebster9/azcopy:v1.0
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
        stage('Setup') {
            steps {
                script {
                    initialisePipeline('boot-app')
                }
            }
        }
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
                    script {
                        buildDockerImage("${CI_IMAGE_NAME}", 'dockerhub')
                    }
                }
            }
        }
        stage('Deploy App for Test') {
            steps {
                container('kubectl') {
                    script {
                        kubectlDeploy('spec', ["SERVICE_NAME=${CI_SERVICE_NAME}", "IMAGE_NAME=${CI_IMAGE_NAME}"])
                    }
                }
            }
        }
                /*
        stage('Wait for App') {
            steps {
                container('alpine') {
                    timeout(5) {
                        waitUntil {
                           script {
                             def r = sh script: "wget -q ${CI_APP_URL} -O /dev/null", returnStatus: true
                             return (r == 0);
                           }
                        }
                    }
                }
            }
        }
        stage('Performance Test') {
            steps {
                container('java') {
                    withEnv(["BASE_URL=${CI_APP_URL}"]) {
                        sh './gradlew gatlingRun'
                    }
                }
            }
        }
        stage('Retag Docker Image') {
            steps {
                container('docker') {
                    script {
                        tagDockerImage("${CI_IMAGE_NAME}", "${DEMO_IMAGE_NAME}", 'dockerhub')
                    }
                }
            }
        }
        */
        stage('Promote to Demo Environment') {
            steps {
                container('kubectl') {
                    script {
                        // in case this is the first time it runs
                        kubectlDeploy('spec', ["SERVICE_NAME=${DEMO_SERVICE_NAME}", "IMAGE_NAME=${CI_IMAGE_NAME}"])
                        kubectlUpdateDeployment("${DEMO_SERVICE_NAME}", "${CI_IMAGE_NAME}")

                        // update ingress
                        kubectlDeploy('ingress', ["SERVICE_NAME=${DEMO_SERVICE_NAME}"])
                    }
                }
            }
        }
    }
    post {
        always {
            container('kubectl') {
                script {
                    kubectlDelete('spec', ["SERVICE_NAME=${CI_SERVICE_NAME}", "IMAGE_NAME=${CI_IMAGE_NAME}"])
                }
            }
        }
    }
}
