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


@NonCPS
def parseJson(text) {
    return new groovy.json.JsonSlurperClassic().parseText(text)
}

// refered "https://github.com/doowb/typeof-github-event/blob/master/lib/event-map.js"
def createMessage(json) {
    if(['pull_request', 'repository'].every { json."${it}" }) {
        messageForPR(json)
    }
    else if(['ref', 'before',/* 'commits', could be empty array */ 'repository'].every { json."${it}" }) { 
        messageForPush(json)  
    }
    else { "不明" }
}

def messageForPR(json){
    "PR #${json.number} ${json.action} by ${json.sender.login} : ${json.pull_request.title}"
}

def messageForPush(json){
    "Push ${json.ref} by ${json.sender.login} : ${json.head_commit.id}"
}
    
def notifyGithubResult(payload) {
    def json = parseJson(payload)
    def msg = createMessage(json)
    notifyToSlack(msg)
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