package fish.payara.fishmaps.player;

import fish.payara.fishmaps.event.EventService;
import fish.payara.fishmaps.event.request.EventOutputRequest;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.inject.Named;

import java.util.List;

@Named(value = "ProfileBean")
@RequestScoped
public class ProfileBean {
    @EJB
    private EventService eventService;

    @ManagedProperty(value = "#{param.player}")
    private String playerName;

    public String getPlayerName () {
        return this.playerName;
    }

    public void setPlayerName (String playerName) {
        this.playerName = playerName;
    }

    public List<EventOutputRequest> getEvents () {
        return this.eventService.getEventsForPlayer(this.playerName).stream().map(EventOutputRequest::fromEvent).toList();
    }
}
