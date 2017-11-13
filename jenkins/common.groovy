def isSuccessCurrently(){
    currentBuild.result == "SUCCESS"
}

def echoPayload(){
  echo "${payload}"
}

return this

