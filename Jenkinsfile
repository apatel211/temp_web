pipeline {
    agent any

    parameters {
        string(name: 'BROWSER', defaultValue: 'chrome', description: 'Browser to run tests')
        string(name: 'GRID_URL', defaultValue: '', description: 'Grid or BrowserStack URL')
        string(name: 'EMAIL_TO', defaultValue: 'team@example.com', description: 'Recipients for report email')
    }

    environment {
        MAVEN_OPTS = "-Dmaven.test.failure.ignore=true"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh "mvn clean test -Dbrowser=${params.BROWSER} -DgridUrl=${params.GRID_URL}"
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'test-output/**', fingerprint: true
            }
        }

        stage('Send Email') {
            steps {
                mail bcc: '',
                     body: """Build: ${env.BUILD_NUMBER}
Job: ${env.JOB_NAME}
Report: ${env.BUILD_URL}artifact/test-output/ExtentReport.html""",
                     from: 'jenkins@example.com',
                     replyTo: 'jenkins@example.com',
                     subject: "UI Automation Report - Job ${env.JOB_NAME} Build #${env.BUILD_NUMBER}",
                     to: "${params.EMAIL_TO}"
            }
        }
    }
}