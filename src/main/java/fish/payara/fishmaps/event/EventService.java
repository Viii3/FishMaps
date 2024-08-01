package fish.payara.fishmaps.event;

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

    public void addEvent (Event event, List<EventParticipation> participations) {
        Event preexisting = this.entityManager.find(Event.class, event.getId());
        if (preexisting != null) {
            this.entityManager.remove(preexisting);
            this.entityManager.flush();
        }
        this.entityManager.persist(event);

        for (EventParticipation participation : participations) {
            EventParticipation preexistingParticipation = this.entityManager.find(EventParticipation.class, participation.getId());
            if (preexistingParticipation != null) {
                this.entityManager.remove(preexistingParticipation);
                this.entityManager.flush();
            }
            this.entityManager.persist(participation);
        }
    }

    public List<ParticipatingPlayer> getParticipatingPlayers (long id) {
        List<EventParticipation> fromDb = this.entityManager.createQuery("SELECT e FROM EventParticipation e", EventParticipation.class).getResultList();
        return fromDb.stream().filter(entry -> entry.getEventId() == id).map(ParticipatingPlayer::fromEventParticipation).toList();
    }

    public record ParticipatingPlayer (String player, String role) {
        public static ParticipatingPlayer fromEventParticipation (EventParticipation participation) {
            return new ParticipatingPlayer(participation.getPlayerName(), participation.getRole());
        }
    }
}
