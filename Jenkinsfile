pipeline {
    agent any
    
    environment {
        GCR_REGISTRY = "asia.gcr.io/upsmf-368011" 
        IMAGE_NAME = "grievance-be-uat"
        BRANCH_NAME = "main" 
	IMAGE_TAG = 0.01    
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/vky25/Grievance.git'
            }
        }

        stage('Build Artifact') {
            steps {
                sh 'mvn clean install -DskipTests' 
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
		    dockerImage = docker.build("${GCR_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}")	
                }
            }
        }

    }
}
