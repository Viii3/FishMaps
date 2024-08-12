package fish.payara.fishmaps.player;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/images/players/*")
public class PlayerHeadServlet extends HttpServlet {
    private static final String NAME_LOOKUP = "https://api.minecraftservices.com/minecraft/profile/lookup/bulk/byname";
    private static final String PROFILE_LOOKUP = "https://sessionserver.mojang.com/session/minecraft/profile/";

    @Inject
    PlayerCache cache;

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Logger logger = Logger.getLogger(PlayerHeadServlet.class.getName());
        String name = req.getParameter("name");
        BufferedImage image = this.cache.get(name);

        if (image == null) {
            image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest getUUID = HttpRequest.newBuilder(URI.create(NAME_LOOKUP))
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("[\"" + name + "\"]"))
                .build();

            try {
                HttpResponse<String> uuidResponse = client.send(getUUID, HttpResponse.BodyHandlers.ofString());
                String uuidBody = uuidResponse.body();
                String uuid = "";
                if (uuidBody.contains("\"id\"")) {
                    int idIndex = uuidBody.indexOf("\"id\"");
                    int startOfUUID = uuidBody.indexOf('"', idIndex + 4);
                    int endOfUUID = uuidBody.indexOf('"', startOfUUID + 2);
                    uuid = uuidBody.substring(startOfUUID + 1, endOfUUID);
                }
                else {
                    this.cache.put(name, image);
                    this.end(image, resp);
                    return;
                }

                HttpRequest getProfile = HttpRequest.newBuilder(URI.create(PROFILE_LOOKUP + uuid)).GET().build();
                HttpResponse<String> profileResponse = client.send(getProfile, HttpResponse.BodyHandlers.ofString());
                String profileBody = profileResponse.body();
                String skinURL = "";
                if (profileBody.contains("\"value\"")) {
                    int valueIndex = profileBody.indexOf("\"value\"");
                    int startOfBase64 = profileBody.indexOf('"', valueIndex + 9);
                    int endOfBase64 = profileBody.indexOf('"', startOfBase64 + 2);
                    String base64Data = profileBody.substring(startOfBase64 + 1, endOfBase64);

                    String decoded = new String(Base64.getDecoder().decode(base64Data), StandardCharsets.UTF_8);
                    int urlStartIndex = decoded.indexOf("http");
                    skinURL = decoded.substring(urlStartIndex, decoded.indexOf('"', urlStartIndex));
                }
                else {
                    this.cache.put(name, image);
                    this.end(image, resp);
                    return;
                }

                HttpRequest getSkin = HttpRequest.newBuilder(URI.create(skinURL)).GET().build();
                HttpResponse<byte[]> skinResponse = client.send(getSkin, HttpResponse.BodyHandlers.ofByteArray());
                BufferedImage temp = ImageIO.read(new ByteArrayInputStream(skinResponse.body()));

                for (int x = 8; x < 16; ++x) {
                    for (int y = 8; y < 16; ++y) {
                        int rgb = temp.getRGB(x, y);
                        image.setRGB(x - 8, y - 8, rgb);
                    }
                }
                logger.log(Level.INFO, "Successfully cached player head for " + name);
                this.cache.put(name, image);
            }
            catch (InterruptedException e) {
                this.end(image, resp);
                return;
            }

            client.close();
        }

        this.end(image, resp);
    }

    private void end (BufferedImage image, HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        OutputStream stream = resp.getOutputStream();
        ImageIO.write(image, "png", stream);
        stream.close();
    }
}
