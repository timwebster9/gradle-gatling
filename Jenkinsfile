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
                        withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                            sh 'docker login -u $USERNAME -p $PASSWORD'
                            sh 'docker  build -t timwebster9/boot-app:2.0 .'
                            sh 'docker push timwebster9/boot-app:2.0'
                        }
                        //withCredentials('', 'dockerhub') {
                        //    def image = docker.build('timwebster9/boot-app:2.0')
                        //    image.push()
                        //}
                    }
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
                        sh './gradlew gatlingRun -sf $WORKSPACE/src/gatling/simulations'
                    }
                }
            }
        }
/**
        stage('Upload Gatling Reports') {
            steps {
                container('azcopy') {
                    withCredentials([string(credentialsId: 'container-key', variable: 'CONTAINER_KEY')]) {
                        sh "azcopy --source build/reports/gatling \
                                   --destination https://test23894237923.blob.core.windows.net/new-container \
                                   --dest-key ${CONTAINER_KEY} \
                                   --recursive"
                    }
                }
            }
        }
*/
    }
    post {
        always {
            container('kubectl') {
               //sh 'kubectl config set-cluster k8s --server=https://kubernetes.simulations.default.svc'
               sh 'kubectl delete -f spec.yaml'
            }
        }
    }
}
