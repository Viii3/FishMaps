package fish.payara.fishmaps.player;

import fish.payara.fishmaps.event.EventService;
import fish.payara.fishmaps.event.request.EventOutputRequest;
import fish.payara.fishmaps.util.TimeBean;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named(value = "PlayerBean")
@RequestScoped
public class PlayerBean {
    private static final long MILLISECONDS_UNTIL_OFFLINE = 30000;
    private static final String OFFLINE_PREFIX = "Offline, last seen: ";

    @EJB
    private PlayerService playerService;

    @EJB
    private EventService eventService;

    @Inject
    private TimeBean timeBean;

    public String createProfileURL (String playerName) {
        return "./profile.xhtml?player=" + playerName;
    }

    public List<String> getPlayerList () {
        return this.playerService.getAll().stream().map(Player::getName).toList();
    }

    public String getOnlineStatus (String playerName) {
        Player player = this.playerService.get(playerName);
        if (player == null) return OFFLINE_PREFIX + "never";

        if (System.currentTimeMillis() - player.getTimeLastSeen() < MILLISECONDS_UNTIL_OFFLINE) return "Online";
        else return OFFLINE_PREFIX + this.timeBean.formatMillis(player.getTimeLastSeen());
    }

    public List<EventOutputRequest> getEvents (String playerName) {
        return this.eventService.getEventsForPlayer(playerName).stream().map(EventOutputRequest::fromEvent).toList();
    }

    public String getCoordinates (String playerName) {
        Player player = this.playerService.get(playerName);
        if (player == null) return "(0, 0)";
        return "(" + player.getX() + ", " + player.getZ() + ")";
    }
}
