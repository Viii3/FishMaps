package fish.payara.fishmaps.event.request;

import fish.payara.fishmaps.event.Event;
import fish.payara.fishmaps.event.IconEnum;
import fish.payara.fishmaps.world.block.Block;

import java.io.Serializable;
import java.time.Instant;

public class EventRequest implements Serializable {
    private int x, y, z;
    private String dimension;
    private String message;
    private String icon;

    public EventRequest () {

    }

    public Event toEvent () {
        return new Event(Instant.now().toEpochMilli(), this.message, this.getIconImage(), this.x, this.z, this.dimension);
    }

    public static EventRequest fromEvent (Event event) {
        EventRequest request = new EventRequest();
        Block location = Block.fromDescriptor(event.getLocation());
        request.setDimension(location.getDimension());
        request.setX(location.getX());
        request.setZ(location.getZ());
        request.setIcon(event.getIcon());
        request.setMessage(event.getMessage());
        return request;
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

    public String getIconImage () {
        try {
            return IconEnum.valueOf(this.icon.toUpperCase()).fileName;
        }
        catch (Exception e) {
            return this.icon;
        }
    }
}
