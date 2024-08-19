package fish.payara.fishmaps.event;

import fish.payara.fishmaps.world.block.Block;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "Events")
@NamedQueries({
    @NamedQuery(
        name = Event.QUERY_ALL,
        query = "SELECT e FROM Event e"
    ),
    @NamedQuery(
        name = Event.QUERY_COUNT,
        query = "SELECT COUNT(e) FROM Event e"
    ),
    @NamedQuery(
        name = Event.QUERY_TIME_PERIOD,
        query = "SELECT e FROM Event e WHERE e.timeStamp >= :after AND e.timeStamp <= :before"
    ),
    @NamedQuery(// This relies on the associated block existing, which is maybe not ideal.
        name = Event.QUERY_LOCATION,
        query = "SELECT e FROM Event e INNER JOIN Block b ON e.location = b.descriptor WHERE b.dimension = :dimension AND b.x >= :minX AND b.x <= :maxX AND b.z >= :minZ AND b.z <= :maxZ"
    )
})
public class Event implements Serializable {
    public static final String QUERY_ALL = "Event.all";
    public static final String QUERY_COUNT = "Event.count";
    public static final String QUERY_TIME_PERIOD = "Event.period";
    public static final String QUERY_LOCATION = "Event.location";

    @Id @GeneratedValue
    private long id;
    private long timeStamp;
    private String message;
    private String icon;
    private String location;

    public Event () {

    }

    public Event (long timeStamp, String message, String icon, int x, int z, String dimension) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.icon = icon;
        this.location = Block.getDescriptor(x, z, dimension);
    }

    public long getId () {
        return this.id;
    }

    public long getTimeStamp () {
        return this.timeStamp;
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

    public void setTimeStamp (long timeStamp) {
        this.timeStamp = timeStamp;
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
