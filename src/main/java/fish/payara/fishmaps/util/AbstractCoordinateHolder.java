package fish.payara.fishmaps.util;

import fish.payara.fishmaps.world.block.Chunk;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractCoordinateHolder implements Serializable {
    private int x, y, z;
    private String dimension;

    public AbstractCoordinateHolder () {

    }

    public AbstractCoordinateHolder (int x, int y, int z, String dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public int getChunkX () {
        return Chunk.getX(this.getX());
    }

    public int getChunkZ () {
        return Chunk.getZ(this.getZ());
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
}
