<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<html>
<head>
    <title>FishMaps | ${param.player}</title>
    <link rel="stylesheet" href="styles/styles.css" />
    <link rel="stylesheet" href="styles/profilestyle.css" />
    <script src="scripts/profileLoader.js"></script>
</head>
<body onload="getPlayerData()">
    <div class="side">
        <div class="sideBar">
            <div class="sidePreview">
                <img class="burgerIcon" src="images/burger.png" />
            </div>
            <div class="sideFullView">
                <p><a href="./">Home</a></p>
                <p><a href="./playerlist.xhtml">Players</a></p>
                <p>Map Layers</p>
                <c:forEach var="dim" items="${MapBean.dimensions}">
                    <a href="./map.xhtml?dimension=${dim}">${MapBean.normaliseDimensionName(dim)}</a>
                </c:forEach>
            </div>
        </div>
    </div>
    <div class="profile">
        <div class="row1">
            <div class="playerSection">
                <img id="profileImage" src="images/players?name=${param.player}" alt="Player preview." />
                <div id="profileInfo">
                    <h2>${param.player}</h2>
                    <h3>Status: ${PlayerBean.getOnlineStatus(param.player)}</h3>
                </div>
            </div>
            <div id="mapPreview">
                <h2>Location: ${PlayerBean.getCoordinates(param.player)}</h2>
            </div>
        </div>
        <div class="row2">
            <h2>Events</h2>
            <table>
                <tr>
                    <th>Icon</th>
                    <th>Message</th>
                    <th>X</th>
                    <th>Y</th>
                    <th>Z</th>
                    <th>Dimension</th>
                    <th>Time</th>
                </tr>
                <c:forEach items="${PlayerBean.getEvents(param.player)}" var="playerEvent">
                    <tr>
                        <td><img src="${playerEvent.getIconImage()}"/></td>
                        <td>${playerEvent.message}</td>
                        <td>${playerEvent.x}</td>
                        <td>${playerEvent.y}</td>
                        <td>${playerEvent.z}</td>
                        <td>${MapBean.normaliseDimensionName(playerEvent.dimension)}</td>
                        <td>${TimeBean.formatMillis(playerEvent.timestamp)}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</body>
</html>