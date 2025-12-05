import requests, CountLogs, BlockSender

if __name__ == "__main__":
    ses = BlockSender.createSession()
    countResponse = CountLogs.getLogCount()
    
    print("Found logs: " + str(countResponse))
    if (input("Post to Confluence? [Y/N]: ").lower() == "y"):
        print("Posting logs...")
        ses.post(BlockSender.BASE_URL + "/api/performance")
    else:
        print("Posting has been skipped.")