package fish.payara.fishmaps.player;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "Players")
public class Player implements Serializable {
    @Id
    private String name;
    private String location;
    private long timeLastSeen;

    public Player () {

    }

    public Player (String name, String locationDescriptor, long timeLastSeen) {
        this.name = name;
        this.location = locationDescriptor;
        this.timeLastSeen = timeLastSeen;
    }

    public String getName () {
        return this.name;
    }

    public String getLocation () {
        return this.location;
    }

    public long getTimeLastSeen () {
        return this.timeLastSeen;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setLocation (String location) {
        this.location = location;
    }

    public void setTimeLastSeen (long timeLastSeen) {
        this.timeLastSeen = timeLastSeen;
    }
}
