package fish.payara.fishmaps.azul.performance.json;

import fish.payara.fishmaps.azul.performance.DurationLogger;
import fish.payara.fishmaps.world.block.Block;
import fish.payara.fishmaps.world.block.BlockListEvent;
import jakarta.batch.runtime.BatchRuntime;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class JsonWriter {
    public void writeBlocks (@Observes BlockListEvent event) {
        long t1 = System.nanoTime();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Block block : event.blocks()) {
            jsonArrayBuilder.add(
                Json.createObjectBuilder()
                    .add("x", block.getX())
                    .add("y", block.getY())
                    .add("z", block.getZ())
                    .add("dimension", block.getDimension())
                    .add("colour", block.getColour())
            );
        }
        long duration = System.nanoTime() - t1;
        
        JsonObject jsonObject = Json.createObjectBuilder()
            .add("blocks", jsonArrayBuilder)
            .add("array_construction_duration", duration)
            .build();
        
        String saveDir = System.getenv("azul_json_save_directory");
        if (saveDir == null) {
            Logger.getLogger(JsonWriter.class.getName())
                .log(Level.WARNING, "The environment variable \"azul_json_save_directory\" must be set to a valid path.");
            return;
        }
        String filename = "BlockRequest-" + System.currentTimeMillis() + ".json";
        Path savePath = Path.of(saveDir);
        
        try {
            if (!savePath.toFile().exists()) {
                Files.createDirectories(savePath);
            }
            
            try (FileWriter writer = new FileWriter(savePath.resolve(filename).toFile())) {
                Json.createGenerator(writer).write(jsonObject).close();
            }
            DurationLogger.log("JSON", event.blocks().size(), duration);
        }
        catch (IOException e) {
            Logger.getLogger(JsonWriter.class.getName())
                .log(Level.SEVERE, "JSON Performance test failed due to error: ", e);
        }

        BatchRuntime.getJobOperator().start("batch-performance", new Properties());
    }
}
