package fish.payara.fishmaps.player;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
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
    public Player getPlayer (@QueryParam("name") String name) {
        return this.playerService.get(name);
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
    public void postPlayer (PlayerRequest request) {
        this.playerService.add(request.toPlayer());
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public void updatePlayer (PlayerRequest request) {
        this.playerService.update(request.getName(), request.getX(), request.getZ(), request.getDimension());
    }
}
