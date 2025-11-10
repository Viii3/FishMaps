package fish.payara.fishmaps.azul.performance.batch;

import fish.payara.fishmaps.world.block.Block;
import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.inject.Named;

@Named
public class BlockProcessor implements ItemProcessor {
    @Override
    public Object processItem (Object object) throws Exception {
        if (object instanceof Block block) {
            block.setX(block.getX() + block.getChunkX());
            block.setY((int)(Math.random() * 50));
            block.setZ(block.getZ() + block.getChunkZ());
            return block;
        }
        return null;
    }
}
