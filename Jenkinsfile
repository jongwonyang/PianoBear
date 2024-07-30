pipeline {
    agent { label 'ssafy_ec2' }

    stages {
        stage('Checkout') {
            steps {
                // 소스 코드 체크아웃
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '12f362a0-6064-4020-b1f1-cacfdb203aa5', url: 'https://lab.ssafy.com/s11-webmobile1-sub2/S11P12B103']])
            }
        }

        stage('Build') {
            steps {
                sh 'sh ./build.sh'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    dir('backend/application') {
                        sh './gradlew sonar'
                    }
                    
                }
            }
        }

        // stage('Build Docker Image') {
        //     steps {
        //         script {
        //             // Docker 이미지 빌드
        //             docker.build("${DOCKER_IMAGE}:latest")
        //         }
        //     }
        // }
        
        stage('Build / Run Container') {
            steps {
                script {
                    dir('backend') {
                        sh 'docker compose -p pianobear up -d --build'
                    }
                }
            }
        }
        
    }
    
}
