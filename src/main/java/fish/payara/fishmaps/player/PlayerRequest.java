package fish.payara.fishmaps.player;

import java.io.Serializable;

public class PlayerRequest implements Serializable {
    private String name;
    private int x, y, z;
    private String dimension;

    public PlayerRequest () {

    }

    public Player toPlayer () {
        return new Player(name, System.currentTimeMillis(), x, y, z, dimension);
    }

    public static PlayerRequest fromPlayer (Player player) {
        PlayerRequest request = new PlayerRequest();
        request.setName(player.getName());
        request.setX(player.getX());
        request.setY(player.getY());
        request.setZ(player.getZ());
        request.setDimension(player.getDimension());

        return request;
    }

    public void setName (String name) {
        this.name = name;
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

    public String getName () {
        return this.name;
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
}
