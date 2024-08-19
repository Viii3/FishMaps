package fish.payara.fishmaps.event.request;

import java.io.Serializable;
import java.util.List;

public class EventFullRequest implements Serializable {
    private EventRequest event;
    private List<ParticipantRequest> participation;

    public EventFullRequest () {

    }

    public EventFullRequest (EventRequest event, List<ParticipantRequest> participationList) {
        this.event = event;
        this.participation = participationList;
    }

    public EventRequest getEvent () {
        return this.event;
    }

    public List<ParticipantRequest> getParticipation () {
        return this.participation;
    }

    public void setEvent (EventRequest event) {
        this.event = event;
    }

    public void setParticipation (List<ParticipantRequest> participation) {
        this.participation = participation;
    }
}
