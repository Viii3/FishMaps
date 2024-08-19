package fish.payara.fishmaps.event;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "EventParticipation")
@NamedQueries({
    @NamedQuery(
        name = EventParticipation.QUERY_ALL,
        query = "SELECT v FROM EventParticipation v"
    ),
    @NamedQuery(
        name = EventParticipation.QUERY_GET_ENTRIES_FOR_EVENT,
        query = "SELECT v FROM EventParticipation v WHERE v.eventId = :id"
    )
})
public class EventParticipation implements Serializable {
    public static final String QUERY_ALL = "EventParticipation.all";
    public static final String QUERY_GET_ENTRIES_FOR_EVENT = "EventParticipation.getPlayers";

    @Id @GeneratedValue
    private long id;
    private long eventId;
    private String playerName;
    private String role;

    public EventParticipation () {

    }

    public EventParticipation (long id, long eventId, String playerName, String role) {
        this.id = id;
        this.eventId = eventId;
        this.playerName = playerName;
        this.role = role;
    }

    public long getId () {
        return this.id;
    }

    public long getEventId () {
        return this.eventId;
    }

    public String getPlayerName () {
        return this.playerName;
    }

    public String getRole () {
        return this.role;
    }

    public void setId (long id) {
        this.id = id;
    }

    public void setEventId (long eventId) {
        this.eventId = eventId;
    }

    public void setPlayerName (String playerName) {
        this.playerName = playerName;
    }

    public void setRole (String role) {
        this.role = role;
    }
}
