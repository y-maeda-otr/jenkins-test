#! groovy

currentBuild.result = "SUCCESS"

node {
  stage ('Checkout'){
    checkout scm
  }

  stage ('run'){
    echo "HELLO!"
    sh 'ls'
  }

  stage ('env'){
    updateGithubStatus()
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

def isSuccessCurrently(){
    currentBuild.result == "SUCCESS"
}
    
def notifyToSlack(msg, link) {
    def slack_channel = "#patentoffice-lib"
    def slack_color = isSuccessCurrently() ? "good" : "danger"
    def detail_link = link ? "(<${link}|Open>)" : ""
    
    def slack_msg = "job ${env.JOB_NAME}[No.${env.BUILD_NUMBER}] was builded ${currentBuild.result} ${detail_link}.\n\n ${msg}"
//    slackSend channel: slack_channel, color: slack_color, message: slack_msg
    echo "job ${env.JOB_NAME}[No.${env.BUILD_NUMBER}] was builded ${currentBuild.result}.\n${msg} ${detail_link}"
}

def updateGithubStatus(){
    withEnv(["STATUS=${isSuccessCurrently() ? "success" : "failure"}", "GITHUB_REPO=y-maeda-otr/jenkins-test"]){
        withCredentials([string(credentialsId: 'github-token', variable: 'ACCESS_TOKEN')]) {
    	     sh """GIT_COMMIT=\$(git rev-parse HEAD) curl "https://api.github.com/repos/${GITHUB_REPO}/statuses/$GIT_COMMIT?access_token=${ACCESS_TOKEN}"\
                   -H "Content-Type: application/json"\
                   -X POST\
                   -d "{\"state\": \"$STATUS\", \"context\": "ci", \"description\": \"from Jenkins\"}" """
        }
    }
}
