package fish.payara.fishmaps.world;

import fish.payara.fishmaps.world.block.Block;
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
        int blockX = Integer.parseInt(req.getParameter("x"));
        int blockZ = Integer.parseInt(req.getParameter("z"));
        int width = Integer.parseInt(req.getParameter("width"));
        int height = Integer.parseInt(req.getParameter("height"));
        String dimension = req.getParameter("dimension");

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; ++x) {
            for (int z = 0; z < height; ++z) {
                Block block = this.service.getBlock(x + blockX, z + blockZ, dimension);
                if (block != null) image.setRGB(x, z, block.getColour());
                else image.setRGB(x, z, 0);
            }
        }

        resp.setContentType("image/bmp");
        OutputStream stream = resp.getOutputStream();
        ImageIO.write(image, "bmp", stream);
        stream.close();
    }
}
