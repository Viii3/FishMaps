package fish.payara.fishmaps.world;

import fish.payara.fishmaps.world.block.Block;
import fish.payara.fishmaps.world.block.BlockEvent;
import fish.payara.fishmaps.world.websocket.ChunkMessage;
import fish.payara.fishmaps.world.websocket.MapUpdateWebSocket;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.enterprise.event.Observes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Startup
@Singleton
public class MapUpdateService {
    private final long INTERVAL_MILLISECONDS = 5000;
    private static final Logger LOGGER = Logger.getLogger(MapUpdateService.class.getName());
    private final Set<String> updatedChunks = new HashSet<>();

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void init () {
        timerService.createIntervalTimer(INTERVAL_MILLISECONDS, INTERVAL_MILLISECONDS, new TimerConfig(null, false));
    }

    public void blockUpdate (@Observes BlockEvent event) {
        Block block = event.block();
        this.updatedChunks.add(block.getChunkDescriptor());
    }

    @Timeout
    private void flush (Timer timer) {
        if (this.updatedChunks.isEmpty()) return;

        List<ChunkMessage> messages = this.updatedChunks.stream()
            .map(descriptor -> {
                Block chunkRepresentative = Block.fromDescriptor(descriptor);
                return new ChunkMessage(chunkRepresentative.getX(), chunkRepresentative.getZ(), chunkRepresentative.getDimension());
            })
            .toList();

        MapUpdateWebSocket.broadcastChunkUpdate(messages);
        this.updatedChunks.clear();
    }
}
