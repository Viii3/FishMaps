package fish.payara.fishmaps.event;

import fish.payara.fishmaps.util.AbstractCoordinateHolder;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

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
    @NamedQuery(
        name = Event.QUERY_LOCATION,
        query = "SELECT e FROM Event e WHERE e.dimension = :dimension AND e.x >= :minX AND e.x <= :maxX AND e.z >= :minZ AND e.z <= :maxZ"
    ),
    @NamedQuery(
        name = Event.QUERY_PLAYER,
        query = "SELECT e FROM Event e INNER JOIN EventParticipation v ON e.id = v.eventId WHERE v.playerName = :playerName"
    )
})
public class Event extends AbstractCoordinateHolder {
    public static final String QUERY_ALL = "Event.all";
    public static final String QUERY_COUNT = "Event.count";
    public static final String QUERY_TIME_PERIOD = "Event.period";
    public static final String QUERY_LOCATION = "Event.location";
    public static final String QUERY_PLAYER = "Event.player";

    @Id @GeneratedValue
    private long id;
    private long timeStamp;
    private String message;
    private String icon;

    public Event () {
        super();
    }

    public Event (long timeStamp, String message, String icon, int x, int y, int z, String dimension) {
        super(x, y, z, dimension);
        this.timeStamp = timeStamp;
        this.message = message;
        this.icon = icon;
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
}
