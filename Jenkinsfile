pipeline {
    agent any

    tools {
        jdk 'jdk17'       // configure JDK 17 under "Global Tool Configuration"
        maven 'maven3'    // configure Maven 3.x under "Global Tool Configuration"
    }

    parameters {
        string(name: 'BROWSER', defaultValue: 'chrome', description: 'Browser to run tests')
        string(name: 'GRID_URL', defaultValue: 'https://ankita26:pE9yzDEHxRB57A2iFCgz@hub-cloud.browserstack.com/wd/hub', description: 'Selenium Grid or BrowserStack hub URL')
        string(name: 'EMAIL_TO', defaultValue: 'ankita.it.2012@gmail.com', description: 'Recipients for report email')
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
                emailext (
                    subject: "UI Automation Report - ${env.JOB_NAME} Build #${env.BUILD_NUMBER}",
                    body: """Build: ${env.BUILD_NUMBER}
        Job: ${env.JOB_NAME}
        Result: ${currentBuild.currentResult}
        Report: ${env.BUILD_URL}artifact/test-output/ExtentReport.html""",
                    to: "${params.EMAIL_TO}"
                )
            }
        }
    }

    post {
        always {
            echo "Build finished. Email notification sent."
        }
    }
}
