package fish.payara.fishmaps.player;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Named(value = "PlayerBean")
@RequestScoped
public class PlayerBean {
    private static final long MILLISECONDS_UNTIL_OFFLINE = 30000;
    private static final String OFFLINE_PREFIX = "Offline, last seen: ";

    @EJB
    private PlayerService service;

    public String createProfileURL (String playerName) {
        return "./profile.xhtml?player=" + playerName;
    }

    public List<String> getPlayerList () {
        return this.service.getAll().stream().map(Player::getName).toList();
    }

    public String getOnlineStatus (String playerName) {
        Player player = this.service.get(playerName);
        if (player == null) return OFFLINE_PREFIX + "never";

        if (System.currentTimeMillis() - player.getTimeLastSeen() < MILLISECONDS_UNTIL_OFFLINE) return "Online";
        else {
            Instant lastSeenTime = Instant.ofEpochMilli(player.getTimeLastSeen());
            ZonedDateTime zonedDateTime = lastSeenTime.atZone(ZoneId.systemDefault());
            return OFFLINE_PREFIX + zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        }
    }
}
