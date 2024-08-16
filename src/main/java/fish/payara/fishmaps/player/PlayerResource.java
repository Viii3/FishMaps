package fish.payara.fishmaps.player;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/map")
    public List<PlayerRequest> getPlayersOnMap (@QueryParam("minX") int minX, @QueryParam("maxX") int maxX, @QueryParam("minZ") int minZ, @QueryParam("maxZ") int maxZ, @QueryParam("dimension") String dimension) {
        return this.playerService.get(minX, maxX, minZ, maxZ, dimension).stream().map(PlayerRequest::fromPlayer).toList();
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
}
