package fish.payara.fishmaps.event;

import fish.payara.fishmaps.event.request.EventFullRequest;
import fish.payara.fishmaps.event.request.ParticipantRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/event")
public class EventResource {
    @Inject
    EventService eventService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object getEvent (@QueryParam("id") Long id) {
        if (id == null) return this.eventService.getAllEvents();
        return this.eventService.getEvent(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addEvent (EventFullRequest request) {
        List<EventParticipation> participationList = request.getParticipation().stream().map(ParticipantRequest::toEventParticipation).toList();
        this.eventService.addEvent(request.getEvent().toEvent(), participationList);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/count")
    public int count () {
        return this.eventService.countEvents();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/participation")
    public Object getEventParticipation (@QueryParam("eventId") Long eventId) {
        if (eventId == null) return this.eventService.getAllEventParticipants();
        return this.eventService.getParticipatingPlayers(eventId);
    }
}
