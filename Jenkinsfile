pipeline {
    agent any

    tools {
        gradle 'Gradle 5.6.2'
        jdk 'jdk-8'
    }

    environment {
//        gpg_passphrase = credentials("gpg_passphrase")
    }

    stages {

        stage('Build') {
            steps {
                 sh  'gradle clean build'
            }
        }

        stage('Results') {
            steps {
                archiveArtifacts 'build/distributions/*.zip'
            }
        }
    }
}
