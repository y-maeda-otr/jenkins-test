#! groovy

node {
  stage ('Checkout'){
    checkout scm
  }

  stage ('run'){
    echo "HELLO!"
    sh 'ls'
  }

  stage ('env'){
    sh 'printenv'
  }
}