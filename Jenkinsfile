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
        switch(json.action){
            case "opened": 
                return messageForPR(json, "created")
            case "reopened": 
                return messageForPR(json, "reopened")
            case "closed":
                return messageForPR(json, (json.pull_request?.merged ? "merged" : "closed"))
        }
    }else if(['ref', 'before',/* 'commits', could be empty array */ 'repository'].every { json."${it}" }) { 
        //messageForPush(json)
        echo "would be push event"
        null      
    }else if(['comment', 'pull_request', 'repository'].every { json."${it}" }) {
        //messageForPRComment(json)
        echo "would be comment event"
	null
    }
}

def messageForPR(json, status){
    [message: "PR #${json.number} ${status} by ${json.sender?.login} : ${json.pull_request?.title}"
    ,link: json.pull_request?.html_url]
}

def messageForPush(json){
    [message: "Push ${json.ref} by ${json.sender.login} : ${json.head_commit.id}"
    ,link: json.pull_request?.url]
}

def messageForPRComment(json){
    [message: "not implemented yet"]
}
    
def notifyGithubResult(payload) {
    def json = parseJson(payload)
    def msg = createMessage(json)

    if(msg){	
        notifyToSlack(msg.message, msg.link)
    }
}
    
def notifyToSlack(msg, link) {
    def slack_channel = "#patentoffice-lib"
    def slack_color = "good"
    def detail_link = link ? "(<${link}|Open>)" : ""
    currentBuild.result = "SUCCESS"
    if(currentBuild.result == "FAILURE") {
        slack_color = "danger"
    }
    def slack_msg = "job ${env.JOB_NAME}[No.${env.BUILD_NUMBER}] was builded ${currentBuild.result} ${detail_link}.\n\n ${msg}"
//    slackSend channel: slack_channel, color: slack_color, message: slack_msg
    echo "job ${env.JOB_NAME}[No.${env.BUILD_NUMBER}] was builded ${currentBuild.result}.\n\n ${msg} ${detail_link}"
}

/*
withCredentials([string(credentialsId: 'github-token', variable: 'githubToken')]) {
	sh "echo ${githubToken} > a.txt"
	sh "ls a.txt"
}
*/