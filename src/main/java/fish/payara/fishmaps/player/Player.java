package fish.payara.fishmaps.player;

import fish.payara.fishmaps.util.AbstractCoordinateHolder;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

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
        query = "SELECT p FROM Player p WHERE p.dimension = :dimension AND p.x >= :minX AND p.x <= :maxX AND p.z >= :minZ AND p.z <= :maxZ"
    )
})
public class Player extends AbstractCoordinateHolder {
    public static final String QUERY_ALL = "Player.all";
    public static final String QUERY_COUNT = "Player.count";
    public static final String QUERY_POSITION = "Player.position";

    @Id
    private String name;
    private long timeLastSeen;

    public Player () {
        super();
    }

    public Player (String name, long timeLastSeen, int x, int y, int z, String dimension) {
        super(x, y, z, dimension);
        this.name = name;
        this.timeLastSeen = timeLastSeen;
    }

    public String getName () {
        return this.name;
    }

    public long getTimeLastSeen () {
        return this.timeLastSeen;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setTimeLastSeen (long timeLastSeen) {
        this.timeLastSeen = timeLastSeen;
    }
}
