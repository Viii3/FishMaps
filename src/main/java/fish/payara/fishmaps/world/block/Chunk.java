package fish.payara.fishmaps.world.block;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Chunk implements Iterable<Block>, Serializable {
    public static final int CHUNK_LENGTH = 16;
    private static final int CHUNK_SIZE = CHUNK_LENGTH * CHUNK_LENGTH;

    private final List<Block> blocks;
    public final String dimension;

    public Chunk (List<Block> blocks, String dimension) {
        this.blocks = blocks;
        this.dimension = dimension;
    }

    public static int getX (int blockX) {
        return (blockX / Chunk.CHUNK_LENGTH) - (blockX < 0 ? 1 : 0);
    }

    public static int getZ (int blockZ) {
        return (blockZ / Chunk.CHUNK_LENGTH) - (blockZ < 0 ? 1 : 0);
    }

    @Override
    public Iterator<Block> iterator () {
        return this.blocks.iterator();
    }

    public Block[] getBlockArray () {
        return this.blocks.toArray(Block[]::new);
    }
}
