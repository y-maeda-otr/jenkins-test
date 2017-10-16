#! groovy

node {
  stage ('Checkout'){
    checkout scm
  }

  stage ('env'){
    sh 'printenv'
  }

  stage ('git'){
    sh "git log -1 '--pretty=format:%h'"
    sh "git rev-parse --abbrev-ref HEAD"
  }

  stage ('notify'){
    notifyGithubResult(payload)
  }
}

def detectEventType(payload) {
    if(['pull_request', 'repository'].every { payload.hasProperty(it) }) { "PR" }
    else if(['ref', 'before', 'commits', 'repository']) { "Push" }
    else { "•s–¾" }
}

def notifyGithubResult(payload) {
    def eventType = detectEventType(payload)
    def msg = "${eventType} by ${payload.sender.login} "
    //notifyToSlack(msg)
    echo msg
}
    
def notifyToSlack(msg) {
    def slack_channel = "#patentoffice-lib"
    def slack_color = "good"
    def detail_link = "(<${env.BUILD_URL}|Open>)"

    if(currentBuild.result == "FAILURE") {
        slack_color = "danger"
    }
    def slack_msg = "job ${env.JOB_NAME}[No.${env.BUILD_NUMBER}] was builded ${currentBuild.result}.\n\n ${msg}"
    slackSend channel: slack_channel, color: slack_color, message: slack_msg
}