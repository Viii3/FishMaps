package fish.payara.fishmaps.world.websocket;

import java.io.Serializable;

public class ChunkMessage implements Serializable {
    private int x, z;
    private String dimension;

    public ChunkMessage () {

    }

    public ChunkMessage (int x, int z, String dimension) {
        this.x = x;
        this.z = z;
        this.dimension = dimension;
    }

    public int getX () {
        return this.x;
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

    public void setZ (int z) {
        this.z = z;
    }

    public void setDimension (String dimension) {
        this.dimension = dimension;
    }
}
