package fish.payara.fishmaps.world;

import fish.payara.fishmaps.world.block.Block;
import fish.payara.fishmaps.world.block.Chunk;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

@WebServlet("/images/fullMap/*")
public class FullMapServlet extends HttpServlet {
    @Inject
    private BlockService blockService;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Logger logger = Logger.getLogger(FullMapServlet.class.getName());
        String dimension = req.getParameter("dimension");


        if (dimension == null) {
            resp.sendError(404);
            return;
        }

        final int maxX = blockService.getMaxX(dimension);
        final int minX = blockService.getMinX(dimension);
        final int maxZ = blockService.getMaxZ(dimension);
        final int minZ = blockService.getMinZ(dimension);

        final int minChunkX = Chunk.getX(minX);
        final int maxChunkX = Chunk.getX(maxX);
        final int minChunkZ = Chunk.getZ(minZ);
        final int maxChunkZ = Chunk.getZ(maxZ);

        // The map is printed in terms of chunks.
        final int width = Math.ceilDiv(maxX - minX, Chunk.CHUNK_LENGTH) * Chunk.CHUNK_LENGTH;
        final int height = Math.ceilDiv(maxZ - minZ, Chunk.CHUNK_LENGTH) * Chunk.CHUNK_LENGTH;
        BufferedImage largeMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Semaphore semaphore = new Semaphore(4);
        List<Future<?>> futures = new ArrayList<>();

        for (int x = minChunkX; x <= maxChunkX; ++x) {
            for (int z = minChunkZ; z <= maxChunkZ; ++z) {
                futures.add(this.managedExecutorService.submit(new MapFragmentLoader(x, z, dimension, minX, minZ, largeMap, semaphore)));
            }
        }

        while (!futures.isEmpty()) {
            futures = futures.stream().filter(future -> !future.isDone() && !future.isCancelled()).toList();
        }

        resp.setContentType("image/png");
        OutputStream stream = resp.getOutputStream();
        ImageIO.write(largeMap, "png", stream);
        stream.close();
    }

    private final class MapFragmentLoader implements Runnable {
        private final int chunkX, chunkZ;
        private final int mapMinX, mapMinZ;
        private final String dimension;
        private final BufferedImage image;
        private final Semaphore semaphore;

        public MapFragmentLoader (int chunkX, int chunkZ, String dimension, int mapMinX, int mapMinZ, BufferedImage image, Semaphore semaphore) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.dimension = dimension;
            this.mapMinX = mapMinX;
            this.mapMinZ = mapMinZ;
            this.image = image;
            this.semaphore = semaphore;
        }

        @Override
        public void run () {
            try {
                this.semaphore.acquire();
                Chunk chunk = blockService.getChunk(this.chunkX, this.chunkZ, this.dimension);
                synchronized (this.image) {
                    for (Block block : chunk) {
                        this.image.setRGB(block.getX() - this.mapMinX, block.getZ() - this.mapMinZ, block.getColour());
                    }
                }
            }
            catch (InterruptedException ignored) {

            }
            finally {
                this.semaphore.release();
            }

        }
    }
}
