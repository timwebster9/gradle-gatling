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
                    env.NAMESPACE         = sh returnStdout: true, script: 'cat /var/run/secrets/kubernetes.io/serviceaccount/namespace'
                    env.APP_NAME          = 'boot-app'
                    env.IMAGE_REPO        = 'timwebster9'
                    env.IMAGE_BASE_NAME   = "${IMAGE_REPO}/${APP_NAME}"
                    env.DEMO_IMAGE_NAME   = "${IMAGE_BASE_NAME}:${BRANCH_NAME}"
                    env.CI_IMAGE_NAME     = "${DEMO_IMAGE_NAME}-${BUILD_NUMBER}"
                    env.CI_SERVICE_NAME   = "${APP_NAME}-${BRANCH_NAME}-${BUILD_NUMBER}"
                    env.CI_APP_URL        = "http://${CI_SERVICE_NAME}.${NAMESPACE}.svc.cluster.local"
                    env.DEMO_SERVICE_NAME = "${APP_NAME}-${BRANCH_NAME}"
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
                        kubectlDeploy('spec', "${CI_SERVICE_NAME}", "${CI_IMAGE_NAME}")
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
        /*
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
                        kubectlDeploy('spec', "${DEMO_SERVICE_NAME}", "${CI_IMAGE_NAME}")
                        kubectlUpdateDeployment("${DEMO_SERVICE_NAME}", "${CI_IMAGE_NAME}")
                    }
                }
            }
        }
    }
    post {
        always {
            container('kubectl') {
                script {
                    kubectlDelete("${CI_SERVICE_NAME}")
                }
            }
        }
    }
}
