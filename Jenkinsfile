pipeline {
    agent any

    tools {
        jdk 'jdk17'       // configure JDK 17 under "Global Tool Configuration"
        maven 'maven3'    // configure Maven 3.x under "Global Tool Configuration"
    }

    parameters {
        string(name: 'BROWSER', defaultValue: 'chrome', description: 'Browser to run tests')
        string(name: 'GRID_URL', defaultValue: '', description: 'Selenium Grid or BrowserStack hub URL')
        string(name: 'EMAIL_TO', defaultValue: 'team@example.com', description: 'Recipients for report email')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh "mvn clean test -Dbrowser=${BROWSER} -DgridUrl=${GRID_URL}"
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'test-output/**', fingerprint: true
            }
        }

        stage('Send Email') {
            steps {
                emailext (
                    subject: "UI Automation Report - ${env.JOB_NAME} Build #${env.BUILD_NUMBER}",
                    body: """Build: ${env.BUILD_NUMBER}
Job: ${env.JOB_NAME}
Report: ${env.BUILD_URL}artifact/test-output/ExtentReport.html""",
                    to: "${EMAIL_TO}"
                )
            }
        }
    }
}
