package fish.payara.fishmaps.event.request;

import fish.payara.fishmaps.event.EventParticipation;

import java.io.Serializable;

public class ParticipantRequest implements Serializable {
    private String player;
    private String role;

    public ParticipantRequest () {

    }

    public ParticipantRequest (String player, String role) {
        this.player = player;
        this.role = role;
    }

    public static EventParticipation toEventParticipation (ParticipantRequest request) {
        EventParticipation participation = new EventParticipation();
        participation.setPlayerName(request.player);
        participation.setRole(request.role);
        return participation;
    }

    public String getPlayer () {
        return this.player;
    }

    public String getRole () {
        return this.role;
    }

    public void setPlayer (String player) {
        this.player = player;
    }

    public void setRole (String role) {
        this.role = role;
    }
}
