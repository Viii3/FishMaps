package fish.payara.fishmaps.azul.performance.batch;

import fish.payara.fishmaps.azul.performance.DurationLogger;
import fish.payara.fishmaps.world.block.Block;
import jakarta.batch.api.chunk.AbstractItemWriter;
import jakarta.inject.Named;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
public class BlockWriter extends AbstractItemWriter {
    private Path saveDirectory;
    private long startTime;
    private int itemCount;
    
    @Override
    public void writeItems (List<Object> list) throws Exception {
        this.itemCount += list.size();
        for (Object object : list) {
            if (object instanceof Block block) {
                JsonObject blockJson = Json.createObjectBuilder()
                    .add("x", block.getX())
                    .add("y", block.getY())
                    .add("z", block.getZ())
                    .add("dimension", block.getDimension())
                    .add("colour", block.getColour())
                    .build();

                String filename = "batch-block-" + System.nanoTime() + ".json";
                try (FileWriter writer = new FileWriter(this.saveDirectory.resolve(filename).toFile())) {
                    Json.createGenerator(writer).write(blockJson).close();
                }
                catch (IOException ignored) {}
            }
        }
    }

    @Override
    public void open (Serializable checkpoint) throws Exception {
        this.startTime = System.nanoTime();
        String saveDirVar = System.getenv("azul_json_save_directory");
        if (saveDirVar != null) {
            this.saveDirectory = Path.of(saveDirVar, "batch");
            Files.createDirectories(this.saveDirectory);
        }
    }

    @Override
    public void close() throws Exception {
        if (this.startTime == 0 || this.itemCount == 0) {
            return;
        }
        
        long duration = System.nanoTime() - this.startTime;
        DurationLogger.log("Batch/Writer", this.itemCount, duration);
    }
}
