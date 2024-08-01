package fish.payara.fishmaps.event;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/event")
public class EventResource {
    @Inject
    EventService eventService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Event getEvent (@QueryParam("id") long id) {
        return this.eventService.getEvent(id);
    }
}
