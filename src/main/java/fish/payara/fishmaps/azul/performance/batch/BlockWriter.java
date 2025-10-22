package fish.payara.fishmaps.azul.performance.batch;

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

@Named
public class BlockWriter extends AbstractItemWriter {
    private Path saveDirectory;
    
    @Override
    public void writeItems (List<Object> list) throws Exception {
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
        String saveDirVar = System.getenv("azul_json_save_directory");
        if (saveDirVar != null) {
            this.saveDirectory = Path.of(saveDirVar, "batch");
            Files.createDirectories(this.saveDirectory);
        }
    }
}
