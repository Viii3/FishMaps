package fish.payara.fishmaps.world.websocket;

import com.google.gson.Gson;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

import java.util.List;

public class ChunkMessageEncoder implements Encoder.Text<List<ChunkMessage>> {
    private static final Gson gson = new Gson();

    @Override
    public String encode (List<ChunkMessage> message) throws EncodeException {
        return gson.toJson(message);
    }
}
