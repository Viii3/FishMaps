package fish.payara.fishmaps.world;

import fish.payara.fishmaps.world.block.Block;
import fish.payara.fishmaps.world.block.BlockEvent;
import fish.payara.fishmaps.world.block.BlockListEvent;
import fish.payara.fishmaps.world.block.Chunk;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class BlockService {
    @PersistenceContext
    private EntityManager entityManager;

    public Block getBlock (String descriptor) {
        return this.entityManager.find(Block.class, descriptor);
    }

    public Block getBlock (int x, int z, String dimension) {
        return this.entityManager.find(Block.class, Block.getDescriptor(x, z, dimension));
    }

    public List<Block> getBlocksInRange (int minX, int maxX, int minZ, int maxZ, String dimension) {
        List<Block> blocks = this.entityManager.createNamedQuery(Block.QUERY_BLOCKS_IN_RANGE, Block.class)
            .setParameter("minX", minX)
            .setParameter("maxX", maxX)
            .setParameter("minZ", minZ)
            .setParameter("maxZ", maxZ)
            .setParameter("dimension", dimension)
            .getResultList();

        return blocks;
    }

    public long countBlocks () {
        try {
            Number count = (Number) this.entityManager.createNamedQuery(Block.QUERY_BLOCK_COUNT).getSingleResult();
            return count.longValue();
        }
        catch (Exception e) {
            return 0;
        }
    }

    public long countBlocks (String dimension) {
        try {
            Number count = (Number) this.entityManager.createNamedQuery(Block.QUERY_BLOCK_COUNT_DIMENSION).setParameter("dimension", dimension).getSingleResult();
            return count.longValue();
        }
        catch (Exception e) {
            return 0;
        }
    }

    public Chunk getChunk (int chunkX, int chunkZ, String dimension) {
        int minBlockX = chunkX * Chunk.CHUNK_LENGTH;
        int maxBlockX = minBlockX + Chunk.CHUNK_LENGTH - 1;
        int minBlockZ = chunkZ * Chunk.CHUNK_LENGTH;
        int maxBlockZ = chunkZ + Chunk.CHUNK_LENGTH - 1;

        List<Block> blocks = new ArrayList<>();
        for (int x = minBlockX; x <= maxBlockX; ++x) {
            for (int z = minBlockZ; z <= maxBlockZ; ++z) {
                Block block = this.getBlock(x, z, dimension);
                if (block != null) blocks.add(block);
            }
        }
        return new Chunk(blocks, dimension);
    }

    public void add (Block block) {
        Block preexisting = this.getBlock(block.getDescriptor());
        if (preexisting == null) {
            this.entityManager.persist(block);
        }
        else {
            this.entityManager.remove(preexisting);
            this.entityManager.flush();
            this.entityManager.persist(block);
        }
    }

    public void add (@Observes BlockEvent event) {
        this.add(event.block());
    }

    public void add (@Observes BlockListEvent event) {
        for (Block block : event.blocks()) {
            this.add(block);
        }
    }

    public List<String> getDimensions () {
        return this.entityManager.createNamedQuery(Block.QUERY_DIMENSION_LIST, String.class).getResultList();
    }
}
