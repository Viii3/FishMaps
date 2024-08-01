package fish.payara.fishmaps.event;

import fish.payara.fishmaps.world.block.Block;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "Events")
public class Event implements Serializable {
    @Id @GeneratedValue
    private long id;
    private String message;
    private String icon;
    private String location;

    public Event () {

    }

    public Event (String message, String icon, int x, int z, String dimension) {
        this.message = message;
        this.icon = icon;
        this.location = Block.getDescriptor(x, z, dimension);
    }

    public long getId () {
        return this.id;
    }

    public String getMessage () {
        return this.message;
    }

    public String getIcon () {
        return this.icon;
    }

    public String getLocation () {
        return this.location;
    }

    public void setId (long id) {
        this.id = id;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public void setIcon (String icon) {
        this.icon = icon;
    }

    public void setLocation (String location) {
        this.location = location;
    }
}
