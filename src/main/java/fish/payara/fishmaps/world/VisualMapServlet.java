package fish.payara.fishmaps.world;

import fish.payara.fishmaps.world.block.Block;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/images/map/*")
public class VisualMapServlet extends HttpServlet {
    @Inject
    BlockService service;

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int blockX = Integer.parseInt(req.getParameter("x"));
        int blockZ = Integer.parseInt(req.getParameter("z"));
        int width = Math.max(1, Integer.parseInt(req.getParameter("width")));
        int height = Math.max(1, Integer.parseInt(req.getParameter("height")));
        int scale = Math.max(1, Integer.parseInt(req.getParameter("scale")));
        String dimension = req.getParameter("dimension");

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        List<Block> blocks = this.service.getBlocksInRange(blockX, blockX + width - 1, blockZ, blockZ + height - 1, dimension);
        for (Block block : blocks) {
            image.setRGB(block.getX() - blockX, block.getZ() - blockZ, block.getColour());
        }

        if (scale > 1) {
            Image scaled = image.getScaledInstance(width * scale, height * scale, BufferedImage.SCALE_SMOOTH);
            image = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.drawImage(scaled, 0, 0, null);
            graphics.dispose();
        }

        resp.setContentType("image/png");
        OutputStream stream = resp.getOutputStream();
        ImageIO.write(image, "png", stream);
        stream.close();
    }
}
