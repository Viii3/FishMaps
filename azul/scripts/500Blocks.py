import requests, BlockSender

if __name__ == "__main__":
    ses = BlockSender.createSession()
    BlockSender.runAll(ses, 0, 500, 0, 1, "Azul_Layer2", 0xFFFFFF)