pipeline {
    agent any
    tools {
        maven "maven-3.5"
        jdk 'java8'
    }
    stages {
        stage('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/Sathyvs/deliver-it.git']]])

                sh 'mvn clean -Dmaven.test.failure.ignore=true install'
            }

        }
        stage ('Check Unit Test Reports') {
            steps {
                script {
                    if (fileExists('target/surefire-reports/TEST-*.xml')) {
                        echo 'YES. report found. Recording scripts'
                        junit 'target/surefire-reports/**/*.xml'
                    } else {
                        echo 'There are no test reports to record'
                    }
                }
            }
        }

        stage ('Release') {

            steps {
                sh 'mvn deploy scm:tag -Drevision=2.0'
            }
        }
    }
}