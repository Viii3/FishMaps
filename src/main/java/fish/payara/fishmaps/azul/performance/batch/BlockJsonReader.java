package fish.payara.fishmaps.azul.performance.batch;

import fish.payara.fishmaps.azul.performance.DurationLogger;
import fish.payara.fishmaps.world.block.Block;
import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.inject.Named;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Named
public class BlockJsonReader extends AbstractItemReader {
    private int index;
    private Path saveDirectory;
    private File jsonFile;
    private long startTime;
    
    @Override
    public Object readItem () throws Exception {
        if (jsonFile == null || !jsonFile.canRead()) return null;
        JsonReader reader = Json.createReader(Files.newBufferedReader(jsonFile.toPath()));
        JsonObject jsonObject = reader.readObject();
        JsonArray blockArray = jsonObject.getJsonArray("blocks");
        
        if (blockArray == null || this.index >= blockArray.size()) return null;
        
        JsonObject block = blockArray.getJsonObject(this.index);
        int x = block.getInt("x");
        int y = block.getInt("y");
        int z = block.getInt("z");
        String dimension = block.getString("dimension");
        int colour = block.getInt("colour");
        
        ++this.index;
        return new Block(x, y, z, dimension, colour);
    }

    @Override
    public void open (Serializable checkpoint) throws Exception {
        this.index = 0;
        this.startTime = System.nanoTime();
        String directory = System.getenv("azul_json_save_directory");
        if (directory == null) {
            saveDirectory = null;
        }
        else {
            saveDirectory = Path.of(directory);
            if (saveDirectory.toFile().isDirectory()) {
                try (Stream<Path> contents = Files.list(saveDirectory)) {
                    this.jsonFile = contents
                        .filter(path -> path.toFile().isFile() && path.toString().endsWith("json"))
                        .max((path1, path2) -> Math.toIntExact(path1.toFile().lastModified() - path2.toFile().lastModified()))
                        .map(Path::toFile)
                        .orElse(null);
                    
                    if (this.jsonFile != null) {
                        Logger.getLogger(BlockJsonReader.class.getName())
                            .log(Level.INFO, "[Batch] Reading json file: " + this.jsonFile.getName());
                    }
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (this.startTime == 0 || this.index == 0) {
            return;
        }
        
        long duration = System.nanoTime() - this.startTime;
        DurationLogger.log("Batch/Reader", this.index, duration);
    }
}
