import requests, time

PORT = "8080"
BASE_URL = "http://localhost:" + PORT + "/fishmaps"

def fillArea (session : requests.Session, minX : int, maxX : int, minZ : int, maxZ : int, dimension : int, colour : int):
    count = 0
    maxCount = (maxX - minX) * (maxZ - minZ)
    print("Generating %i blocks." % maxCount)
    data = []
    for x in range(minX, maxX):
        for z in range(minZ, maxZ):
            count += 1
            data.append({"x": x, "y": 0, "z": z, "colour": colour, "dimension": dimension})

    print("Posting...")
    resp = session.post(BASE_URL + "/api/map/block/multiple", json=data)
    print("Posted with response:\n" + str(resp.content))

def getArea (session : requests.Session, minX : int, minZ : int, width : int, height : int, dimension : str):
    url = BASE_URL + "/images/map?x=" + str(minX) + "&z=" + str(minZ) + "&dimension=" + dimension + "&width=" + str(width) + "&height=" + str(height) + "&scale=1"
    session.get(url)

def getAreaWithConcurrency (session : requests.Session, dimension : str):
    session.get(BASE_URL + "/images/fullMap?dimension=" + dimension)

def clearDimension (session : requests.Session, dimension : str):
    session.delete(BASE_URL + "/api/map?dimension=" + dimension)

def countDimension (session : requests.Session, dimension : str) -> int:
    resp = session.get(BASE_URL + "/api/map/block/count?dimension=" + dimension)
    return int(resp.content)

def createSession ():
    ses = requests.Session()
    ses.headers = {
        "Host": "localhost:" + PORT,
        "User-Agent": "curl/8.7.1",
        "Accept": "*/*",
        "Authorization": "Basic ZmlzaG1hcHNfYWRtaW46ZmlzaG1hcHM="
    }
    return ses

def runAll (session : requests.Session, minX : int, maxX : int, minZ : int, maxZ : int, dimension : int, colour : int):
    if (countDimension(session, dimension) > 0):
        print("Existing blocks found in database, clearing...")
        clearDimension(session, dimension)

    fillArea(session, minX, maxX, minZ, maxZ, dimension, colour)
    sleepAmount = 5
    print("Sleeping for " + str(sleepAmount) + " seconds...")
    time.sleep(sleepAmount)

    print("Getting image of area...")
    getArea(session, minX, minZ, maxX - minX, maxZ - minZ, dimension)

    print("Getting concurrent image of area...")
    getAreaWithConcurrency(session, dimension)