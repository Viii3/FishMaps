package fish.payara.fishmaps.event;

import fish.payara.fishmaps.world.block.Block;

import java.io.Serializable;

public class EventRequest implements Serializable {
    private int x, y, z;
    private String dimension;
    private String message;
    private String icon;

    public EventRequest () {

    }

    public Event toEvent () {
        return new Event(this.message, this.icon, this.x, this. z, this.dimension);
    }

    public String getLocation () {
        return Block.getDescriptor(this.x, this.z, this.dimension);
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

    public String getMessage () {
        return this.message;
    }

    public String getIcon () {
        return this.icon;
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

    public void setMessage (String message) {
        this.message = message;
    }

    public void setIcon (String icon) {
        this.icon = icon;
    }
}
