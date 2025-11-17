package fish.payara.fishmaps.azul.performance;

import fish.payara.fishmaps.player.PlayerRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/performance")
public class PerformanceResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/count")
    public int countLogs () {
        return DurationLogger.size();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void publishLogs () {
        DurationLogger.publish();
    }
}
