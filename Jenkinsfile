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
                    setBranchName()
                    env.NAMESPACE = sh returnStdout: true, script: 'cat /var/run/secrets/kubernetes.io/serviceaccount/namespace'
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
                        buildDockerImage("timwebster9/boot-app:${BRANCH_NAME}", 'dockerhub')
                    }
                }
            }
        }
        stage('Deploy App') {
            steps {
                container('kubectl') {
                    script {
                        kubectlDeploy('spec')
                    }
                }
            }
        }
        stage('Wait for App') {
            steps {
                container('alpine') {
                    timeout(5) {
                        waitUntil {
                           script {
                             def r = sh script: "wget -q http://boot-app-service.${env.NAMESPACE}.svc.cluster.local -O /dev/null", returnStatus: true
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
                    withEnv(["BASE_URL=http://boot-app-service.${env.NAMESPACE}.svc.cluster.local"]) {
                        sh './gradlew gatlingRun'
                    }
                }
            }
        }
    }
    post {
        always {
            container('kubectl') {
                script {
                    kubectlDelete('spec')
                }
            }
        }
    }
}
