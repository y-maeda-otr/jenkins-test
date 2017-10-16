#! groovy

node {
  stage ('Checkout'){
    checkout scm
  }

  stage ('run'){
    echo "HELLO!"
    echo payload
    sh 'ls'
  }

  stage ('env'){
    sh 'printenv'
  }

  stage ('git'){
    sh "git log -1 '--pretty=format:%h'"
    sh "git rev-parse --abbrev-ref HEAD"
  }
}

    