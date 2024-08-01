package fish.payara.fishmaps.world;

import fish.payara.fishmaps.world.block.Chunk;
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

@WebServlet("/images/map/*")
public class VisualMapServlet extends HttpServlet {
    @Inject
    BlockService service;

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int chunkX = Integer.parseInt(req.getParameter("x"));
        int chunkZ = Integer.parseInt(req.getParameter("z"));
        String dimension = req.getParameter("dimension");

        BufferedImage image = new BufferedImage(Chunk.CHUNK_LENGTH, Chunk.CHUNK_LENGTH, BufferedImage.TYPE_INT_RGB);
        Chunk chunk = this.service.getChunk(chunkX, chunkZ, dimension);
        chunk.iterator().forEachRemaining(block -> image.setRGB(block.getX() % 16, block.getZ() % 16, block.getColour()));

        resp.setContentType("image/png");
        OutputStream stream = resp.getOutputStream();
        ImageIO.write(image, "png", stream);
        stream.close();
    }
}
