package fish.payara.fishmaps.event;

import fish.payara.fishmaps.event.request.EventOutputRequest;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class EventService {
    @PersistenceContext
    private EntityManager entityManager;

    public Event getEvent (long id) {
        return this.entityManager.find(Event.class, id);
    }

    public List<Event> getAllEvents () {
        return this.entityManager.createNamedQuery(Event.QUERY_ALL, Event.class).getResultList();
    }

    public void addEvent (Event event, List<EventParticipation> participationList) {
        Event preexisting = this.entityManager.find(Event.class, event.getId());
        if (preexisting != null) {
            this.entityManager.remove(preexisting);
            this.entityManager.flush();
        }
        this.entityManager.persist(event);

        for (EventParticipation participation : participationList) {
            EventParticipation preexistingParticipation = this.entityManager.find(EventParticipation.class, participation.getId());
            if (preexistingParticipation != null) {
                this.entityManager.remove(preexistingParticipation);
                this.entityManager.flush();
            }
            participation.setEventId(event.getId());
            this.entityManager.persist(participation);
        }
    }

    public List<EventParticipation> getParticipatingPlayers (long id) {
        return this.entityManager
            .createNamedQuery(EventParticipation.QUERY_GET_ENTRIES_FOR_EVENT, EventParticipation.class)
            .setParameter("id", id)
            .getResultList();
    }

    public List<EventParticipation> getAllEventParticipants () {
        return this.entityManager.createNamedQuery(EventParticipation.QUERY_ALL, EventParticipation.class).getResultList();
    }

    public int countEvents () {
        try {
            Number number = (Number)this.entityManager.createNamedQuery(Event.QUERY_COUNT).getSingleResult();
            return number.intValue();
        }
        catch (Exception e) {
            return 0;
        }
    }

    public List<Event> getEventsInArea (int minX, int maxX, int minZ, int maxZ, String dimension) {
        return this.entityManager
            .createNamedQuery(Event.QUERY_LOCATION, Event.class)
            .setParameter("minX", minX)
            .setParameter("maxX", maxX)
            .setParameter("minZ", minZ)
            .setParameter("maxZ", maxZ)
            .setParameter("dimension", dimension)
            .getResultList();
    }

    public List<Event> getEventsForPlayer (String playerName) {
        return this.entityManager.createNamedQuery(Event.QUERY_PLAYER, Event.class)
            .setParameter("playerName", playerName)
            .getResultList();
    }
}
