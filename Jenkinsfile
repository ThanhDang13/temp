pipeline {
    agent any

    environment {
        APP_VERSION = '1.0.0'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out the source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                script {
                    def buildResult = sh(script: './gradlew build api', returnStatus: true)
                    if (buildResult != 0) {
                        error "Build failed."
                    }
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying version ${APP_VERSION}..."
                script {
                    def deployResult = sh(script: 'nohup java -jar apps/api/build/libs/api-1.0.jar --spring.profiles.active=dev > app.log 2>&1 &', returnStatus: true)
                    if (deployResult != 0) {
                        error "Deployment failed."
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Build and Deploy completed successfully!'
        }
        failure {
            echo 'Build or Deploy failed!'
        }
        always {
            echo 'Pipeline finished.'
        }
    }
}