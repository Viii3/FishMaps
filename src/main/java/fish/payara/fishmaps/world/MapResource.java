package fish.payara.fishmaps.world;

import fish.payara.fishmaps.world.block.Block;
import fish.payara.fishmaps.world.block.BlockEvent;
import fish.payara.fishmaps.world.block.Chunk;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/map")
public class MapResource {
    @Inject
    private BlockService blockService;

    @Inject
    private Event<BlockEvent> event;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/block")
    public Block getBlock (@QueryParam("x") int x, @QueryParam("z") int z, @QueryParam("dimension") String dimension) {
        return this.blockService.getBlock(x, z, dimension);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/blocks")
    public long countBlocks (@QueryParam("dimension") String dimension) {
        if (dimension == null) return this.blockService.countBlocks();
        return this.blockService.countBlocks(dimension);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/chunk")
    public Block[] getChunk (@QueryParam("x") int x, @QueryParam("z") int z, @QueryParam("dimension") String dimension) {
        Chunk chunk = this.blockService.getChunk(x, z, dimension);
        return chunk.getBlockArray();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/block")
    public void postBlock (BlockRequest request) {
        this.event.fire(new BlockEvent(request.toBlock()));
    }
}