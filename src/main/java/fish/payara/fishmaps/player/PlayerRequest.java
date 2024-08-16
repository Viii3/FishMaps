package fish.payara.fishmaps.player;

import fish.payara.fishmaps.world.block.Block;

import java.io.Serializable;

public class PlayerRequest implements Serializable {
    private String name;
    private int x, z;
    private String dimension;

    public PlayerRequest () {

    }

    public Player toPlayer () {
        return new Player(name, Block.getDescriptor(x, z, dimension), System.currentTimeMillis());
    }

    public static PlayerRequest fromPlayer (Player player) {
        PlayerRequest request = new PlayerRequest();
        Block block = Block.fromDescriptor(player.getLocation());
        request.setName(player.getName());
        request.setX(block.getX());
        request.setZ(block.getZ());
        request.setDimension(block.getDimension());

        return request;
    }

    public void setName (String name) {
        this.name = name;
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

    public String getName () {
        return this.name;
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
}
