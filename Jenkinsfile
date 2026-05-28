pipeline {
  agent any

  options {
    disableConcurrentBuilds()
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  environment {
    MAIL_HOST = credentials('MAIL_HOST')
    MAIL_PORT = credentials('MAIL_PORT')
    MAIL_USERNAME = credentials('MAIL_USERNAME')
    MAIL_PASSWORD = credentials('MAIL_PASSWORD')
    CONTACT_RECIPIENT = credentials('MAIL_SERVICE_CONTACT_RECIPIENT')
  }

  stages {
    stage('Deploy') {
      steps {
        sh 'docker compose -f docker-compose-mail-service.yml up --build -d --remove-orphans'
      }
    }
  }

  post {
    always {
      sh 'docker image prune -f'
    }
  }
}
