pipeline {
    agent any
    
    environment {
        GCR_REGISTRY = "asia.gcr.io/upsmf-368011" 
        IMAGE_NAME = "Grievance-BE-UAT "
        BRANCH_NAME = "main" 
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/vky25/Grievance.git'
            }
        }

        stage('Build Artifact') {
            steps {
                sh 'mvn clean install' 
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def dockerImage = docker.build("${GCR_REGISTRY}/${IMAGE_NAME}:${BRANCH_NAME}")
	 				          dockerImage.tag("${GCR_REGISTRY}/${IMAGE_NAME}:0.0.1")
                }
            }
        }

    }
}
