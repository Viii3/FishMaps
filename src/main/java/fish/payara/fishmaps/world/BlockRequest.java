package fish.payara.fishmaps.world;

import fish.payara.fishmaps.world.block.Block;

import java.io.Serializable;

public class BlockRequest implements Serializable {
    private int x, y, z, colour;
    private String dimension;

    public Block toBlock () {
        return new Block(x, y, z, dimension, colour);
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

    public void setColour (int colour) {
        this.colour = colour;
    }

    public void setDimension (String dimension) {
        this.dimension = dimension;
    }
}
