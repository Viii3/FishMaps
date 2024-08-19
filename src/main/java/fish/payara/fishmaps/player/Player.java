package fish.payara.fishmaps.player;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "Players")
@NamedQueries({
    @NamedQuery(
      name = Player.QUERY_ALL,
      query = "SELECT p FROM Player p"
    ),
    @NamedQuery(
        name = Player.QUERY_COUNT,
        query = "SELECT COUNT(p) FROM Player p"
    ),
    @NamedQuery(
        name = Player.QUERY_POSITION,
        query = "SELECT p FROM Player p INNER JOIN Block b ON p.location = b.descriptor WHERE b.dimension = :dimension AND b.x >= :minX AND b.x <= :maxX AND b.z >= :minZ AND b.z <= :maxZ"
    )
})
public class Player implements Serializable {
    public static final String QUERY_ALL = "Player.all";
    public static final String QUERY_COUNT = "Player.count";
    public static final String QUERY_POSITION = "Player.position";

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
