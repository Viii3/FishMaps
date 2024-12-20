package fish.payara.fishmaps.player;

import fish.payara.fishmaps.RestConfiguration;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/player")
public class PlayerResource {
    @Inject
    private PlayerService playerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object getPlayer (@QueryParam("name") String name) {
        if (name == null) return this.playerService.getAll().stream().map(PlayerRequest::fromPlayer).toList();
        return PlayerRequest.fromPlayer(this.playerService.get(name));
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/count")
    public int countPlayers () {
        return this.playerService.count();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(RestConfiguration.ROLE_ADMIN)
    public void postPlayer (PlayerRequest request) {
        this.playerService.add(request.toPlayer());
    }
}
