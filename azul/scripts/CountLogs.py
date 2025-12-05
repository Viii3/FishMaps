import requests, BlockSender

def getLogCount ():
    ses = BlockSender.createSession()
    countResponse = ses.get(BlockSender.BASE_URL + "/api/performance/count")
    return int(countResponse.content)

def getAllLogs ():
    ses = BlockSender.createSession()
    resp = ses.get(BlockSender.BASE_URL + "/api/performance/logs")
    return str(resp.content)

if __name__ == "__main__":
    print("Found logs: " + str(getLogCount()))
    print(getAllLogs())