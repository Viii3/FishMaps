package fish.payara.fishmaps.world.block;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;

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
public class Block implements Serializable {
    public static final String QUERY_BLOCKS_IN_RANGE = "Block.blocksInRange";
    public static final String QUERY_DIMENSION_LIST = "Block.getAllDimensions";
    public static final String QUERY_BLOCK_COUNT = "Block.getBlockCount";
    public static final String QUERY_BLOCK_COUNT_DIMENSION = "Block.getBlockCountDimension";

    @Id
    private String descriptor;
    private int x;
    private int y;
    private int z;
    private String dimension;
    private int colour;

    public Block () {

    }

    public Block (int x, int y, int z, String dimension, int colour) {
        this.descriptor = Block.getDescriptor(x, z, dimension);
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.colour = colour;
    }

    public static String getDescriptor (int x, int z, String dimension) {
        return String.format("%d-%d-%s", x, z, dimension);
    }

    public String getDescriptor () {
        return this.descriptor;
    }

    public int getX () {
        return this.x;
    }

    public int getY () {
        return this.y;
    }

    public int getZ () {
        return this.z;
    }

    public String getDimension () {
        return this.dimension;
    }

    public int getColour () {
        return this.colour;
    }

    public void setDescriptor (String descriptor) {
        this.descriptor = descriptor;
    }

    public void setX (int x) {
        this.x = x;
    }

    public void setY (int y) {
        this.y = y;
    }

    public void setZ (int z) {
        this.z = z;
    }

    public void setDimension (String dimension) {
        this.dimension = dimension;
    }

    public void setColour (int colour) {
        this.colour = colour;
    }
}
