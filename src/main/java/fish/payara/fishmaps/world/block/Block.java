package fish.payara.fishmaps.world.block;

import fish.payara.fishmaps.util.AbstractCoordinateHolder;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "blocks")
@NamedQueries({
    @NamedQuery(
        name = Block.QUERY_BLOCKS_IN_RANGE,
        query = "SELECT b FROM Block b WHERE b.x >= :minX AND b.x <= :maxX AND b.z >= :minZ AND b.z <= :maxZ AND b.dimension = :dimension"
    ),
    @NamedQuery(
        name = Block.QUERY_DIMENSION_LIST,
        query = "SELECT DISTINCT b.dimension FROM Block b"
    ),
    @NamedQuery(
        name = Block.QUERY_BLOCK_COUNT,
        query = "SELECT COUNT(b) FROM Block b"
    ),
    @NamedQuery(
        name = Block.QUERY_BLOCK_COUNT_DIMENSION,
        query = "SELECT COUNT(b) FROM Block b WHERE b.dimension = :dimension"
    )
})
public class Block extends AbstractCoordinateHolder {
    public static final String QUERY_BLOCKS_IN_RANGE = "Block.blocksInRange";
    public static final String QUERY_DIMENSION_LIST = "Block.getAllDimensions";
    public static final String QUERY_BLOCK_COUNT = "Block.getBlockCount";
    public static final String QUERY_BLOCK_COUNT_DIMENSION = "Block.getBlockCountDimension";

    @Id
    private String descriptor;
    private int colour;

    public Block () {
        super();
    }

    public Block (int x, int y, int z, String dimension, int colour) {
        super(x, y, z, dimension);
        this.descriptor = Block.getDescriptor(x, z, dimension);
        this.colour = colour;
    }

    public static String getDescriptor (int x, int z, String dimension) {
        return String.format("%d %d %s", x, z, dimension);
    }

    public static Block fromDescriptor (String descriptor) {
        Block block = new Block();
        String[] split = descriptor.split(" ", 3);
        block.setDimension(split[2]);
        try {
            block.setX(Integer.parseInt(split[0]));
            block.setZ(Integer.parseInt(split[1]));
        }
        catch (Exception ignored) {}

        return block;
    }

    public String getChunkDescriptor () {
        return Block.getDescriptor(this.getChunkX(), this.getChunkZ(), this.getDimension());
    }

    public String getDescriptor () {
        return this.descriptor;
    }

    public int getColour () {
        return this.colour;
    }

    public void setDescriptor (String descriptor) {
        this.descriptor = descriptor;
    }

    public void setColour (int colour) {
        this.colour = colour;
    }
}
