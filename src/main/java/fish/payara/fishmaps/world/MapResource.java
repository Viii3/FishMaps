package fish.payara.fishmaps.world;

import fish.payara.fishmaps.event.EventService;
import fish.payara.fishmaps.event.request.EventOutputRequest;
import fish.payara.fishmaps.player.PlayerRequest;
import fish.payara.fishmaps.player.PlayerService;
import fish.payara.fishmaps.world.block.Block;
import fish.payara.fishmaps.world.block.BlockEvent;
import fish.payara.fishmaps.world.block.BlockListEvent;
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

import java.util.List;
import java.util.Objects;

@Path("/map")
public class MapResource {
    @Inject
    private BlockService blockService;

    @Inject
    private PlayerService playerService;

    @Inject
    private EventService eventService;

    @Inject
    private Event<BlockEvent> blockEvent;

    @Inject
    private Event<BlockListEvent> blockListEvent;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/block")
    public Block getBlock (@QueryParam("x") int x, @QueryParam("z") int z, @QueryParam("dimension") String dimension) {
        return this.blockService.getBlock(x, z, dimension);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/block/count")
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/player")
    public List<PlayerRequest> getPlayers (@QueryParam("minX") int minX, @QueryParam("maxX") int maxX, @QueryParam("minZ") int minZ, @QueryParam("maxZ") int maxZ, @QueryParam("dimension") String dimension) {
        return this.playerService.get(minX, maxX, minZ, maxZ, dimension).stream().map(PlayerRequest::fromPlayer).toList();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/event")
    public List<EventOutputRequest> getEventsOnMap (@QueryParam("minX") int minX, @QueryParam("maxX") int maxX, @QueryParam("minZ") int minZ, @QueryParam("maxZ") int maxZ, @QueryParam("dimension") String dimension) {
        return this.eventService.getEventsInArea(minX, maxX, minZ, maxZ, dimension).stream().map(EventOutputRequest::fromEvent).toList();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/block")
    public void postBlock (BlockRequest request) {
        if (request != null) this.blockEvent.fire(new BlockEvent(request.toBlock()));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/block/multiple")
    public void postBlocks (List<BlockRequest> blocks) {
        List<Block> parsedBlocks = blocks.stream()
            .filter(Objects::nonNull)
            .map(BlockRequest::toBlock)
            .toList();
        this.blockListEvent.fire(new BlockListEvent(parsedBlocks));
    }
}