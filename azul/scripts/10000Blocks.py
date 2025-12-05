import requests, BlockSender

if __name__ == "__main__":
    ses = BlockSender.createSession()
    BlockSender.runAll(ses, 0, 625, 0, 16, "Azul_Layer2", 0xFFFFFF)