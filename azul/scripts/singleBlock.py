import requests, BlockSender

if __name__ == "__main__":
    ses = BlockSender.createSession()
    data = [{"x": 0, "y": 0, "z": 0, "colour": 0xFFFFFF, "dimension": "Azul_Layer1"}]
    resp = ses.post(BlockSender.BASE_URL + "/api/map/block/multiple", json=data)
    print("Posted with response:\n" + str(resp.content))