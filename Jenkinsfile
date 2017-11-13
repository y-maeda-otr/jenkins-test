#! groovy

node {

  try{
    if(detectBuildBranch(payload)){
      currentBuild.result = "SUCCESS"
      stage ('Checkout'){
        checkout scm
	sh "git reset --hard ${detectBuildBranch(payload)}"
	common = load "${pwd()}@script/common.groovy"

	echo common.isSuccessCurrently()
      }

      stage ('compile'){
        sh 'ls'
	echo common.isSuccessCurrently()
      }
  
      stage ('test'){
        sh 'ls'
      }
      stage ('archive'){
          archiveArtifacts artifacts: 'build/reports/tests/test'
      }
    }
  }catch(ex){
    currentBuild.result = "FAILURE"
    throw new RuntimeException(ex)
  }finally{
    stage ('notify'){
      notifyGithubResult(payload)

      if(detectBuildBranch(payload)){
        updateGithubStatus()
      }
    }
  }
}

@NonCPS
def parseJson(text) {
    return new groovy.json.JsonSlurperClassic().parseText(text)
}

@NonCPS
def detectBuildBranch(payload){
  parseJson(payload)?.after
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
        messageForPush(json)
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
    def jenkinsLink = common.isSuccessCurrently() ? "" : "<${env.BUILD_URL}console|Jenkins>"

    [message: "Push ${json.ref} by ${json.sender.login} : ${json.head_commit.id} (${currentBuild.result}${jenkinsLink})"
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
    def slack_color = common.isSuccessCurrently() ? "good" : "danger"
    def detail_link = link ? "(<${link}|Github>)" : ""
    
    def slack_msg = "job ${env.JOB_NAME}[No.${env.BUILD_NUMBER}] was builded${detail_link}.\n${msg}"
    echo slack_msg
    //slackSend channel: slack_channel, color: slack_color, message: slack_msg
}

def updateGithubStatus(){
   def githubRepo ="y-maeda-otr/jenkins-test"
   def status = common.isSuccessCurrently() ? "success" : "failure"
    
    withCredentials([string(credentialsId: 'github-token', variable: 'accessToken')]) {
        sh """curl \"https://api.github.com/repos/${githubRepo}/statuses/\$(git rev-parse HEAD)?access_token=${accessToken}\"\
             -H \"Content-Type: application/json\"\
             -X POST\
             -d \"{\\\"state\\\": \\\"${status}\\\", \\\"context\\\": \\\"ci\\\", \\\"description\\\": \\\"from Jenkins\\\"}\" """
    }
}