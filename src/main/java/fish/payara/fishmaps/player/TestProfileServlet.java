package fish.payara.fishmaps.player;

import fish.payara.fishmaps.event.request.EventOutputRequest;
import fish.payara.fishmaps.util.TimeBean;
import fish.payara.fishmaps.world.MapBean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/profile2")
public class TestProfileServlet extends HttpServlet {
    @Inject
    private MapBean mapBean;

    @Inject
    private PlayerBean playerBean;

    @Inject
    private TimeBean timeBean;

    @Inject
    private PlayerService playerService;

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter("player");
        Player player = this.playerService.get(playerName);

        StringBuilder html = new StringBuilder();
        html.append("""
            <html>
                <head>
                    <title>FishMaps | %s</title>
                    <link rel="stylesheet" href="styles/styles.css" />
                    <link rel="stylesheet" href="styles/profilestyle.css" />
                </head>
                <body>
                    <div class="side">
                        <div class="sideBar">
                            <div class="sidePreview">
                                <img class="burgerIcon" src="images/burger.png" />
                            </div>
                            <div class="sideFullView">
                                <p><a href="./">Home</a></p>
                                <p><a href="./playerlist.xhtml">Players</a></p>
                                <p>Map Layers</p>
            """.formatted(playerName));
        for (String dimension : this.mapBean.getDimensions()) {
            html.append("<a href=\"./map.xhtml?dimension=").append(dimension).append("\">")
                .append(this.mapBean.normaliseDimensionName(dimension))
                .append("</a>\n");
        }
        html.append("""
                            </div>
                        </div>
                    </div>
            """);
        html.append("<div class=\"profile\">\n")
            .append("<div class=\"row1\">\n")
                .append("<div class=\"playerSection\">")
                    .append("<img id=\"profileImage\" src=\"images/players?name=").append(playerName).append("\" alt=\"Player preview.\" />\n")
                    .append("<div id=\"profileInfo\">\n")
                        .append("<h2>").append(playerName).append("</h2>\n")
                        .append("<h3>Status: ").append(this.playerBean.getOnlineStatus(playerName)).append("</h3>\n")
                    .append("</div>\n")
                .append("</div>\n")
                .append("<div id=\"mapPreview\">\n")
                    .append("<h2>Location: ").append(this.playerBean.getCoordinates(playerName)).append("</h2>\n")
                    .append(this.createMapPreview(player))
                .append("</div>\n")
            .append("</div>\n")
            .append("<div class=\"row2\">\n")
                .append("<h2>Events</h2>\n")
                .append("<table>\n")
                    .append("<tr>\n")
                        .append("<th>Icon</th>\n")
                        .append("<th>Message</th>\n")
                        .append("<th>X</th>\n")
                        .append("<th>Z</th>\n")
                        .append("<th>Dimension</th>\n")
                        .append("<th>Time</th>\n")
                    .append("</tr>\n");
        for (EventOutputRequest event : this.playerBean.getEvents(playerName)) {
            html.append("<tr>\n")
                    .append("<td><img src=\"").append(event.getIconImage()).append("\" /></td>\n")
                    .append("<td>").append(event.getMessage()).append("</td>\n")
                    .append("<td>").append(event.getX()).append("</td>\n")
                    .append("<td>").append(event.getZ()).append("</td>\n")
                    .append("<td>").append(event.getDimension()).append("</td>\n")
                    .append("<td>").append(this.timeBean.formatMillis(event.getTimestamp())).append("</td>\n")
                .append("</tr>\n");
        }
        html.append("</table>\n")
            .append("</div>\n")
            .append("</div>\n")
            .append("</body>");

        resp.getWriter().println(html);
    }

    private String createMapPreview (Player player) {
        final int MAP_BLOCK_WIDTH = 128;
        final int mapX = (int)(player.getX() - MAP_BLOCK_WIDTH / 2.0);
        final int mapZ = (int)(player.getZ() - MAP_BLOCK_WIDTH / 2.0);

        StringBuilder mapTile = new StringBuilder();
        mapTile.append("<a href=\"map.xhtml?")
            .append("x=").append(mapX)
            .append("&z=").append(mapZ)
            .append("&dimension=").append(player.getDimension()).append("\">");

        mapTile.append("<img id=\"map\" title=\"Go to map\" ").append("alt=\"Map of ").append(player.getName()).append("'s position\" ");

        mapTile.append("src=\"images/map?")
            .append("x=").append(mapX)
            .append("&z=").append(mapZ)
            .append("&dimension=").append(player.getDimension())
            .append("&width=").append(MAP_BLOCK_WIDTH)
            .append("&height=").append(MAP_BLOCK_WIDTH)
            .append("&scale=2\" ");

        mapTile.append(" /></a>");

        return mapTile.toString();
    }
}
