const INVALID_PLAYER = "Invalid Player";
const MAP_BLOCK_WIDTH = 128;

var currentPlayer = INVALID_PLAYER;
var x = 0;
var z = 0;
var dimension = "minecraft:overworld";

function loadProfile () {
    let mapPreview = document.getElementById("mapPreview");
    let mapX = Math.floor(x - MAP_BLOCK_WIDTH / 2);
    let mapZ = Math.floor(z - MAP_BLOCK_WIDTH / 2);
    let mapImage = document.createElement("img");
    mapImage.id = "map";
    mapImage.alt = "Map of " + currentPlayer + "'s position"
    mapImage.src = "images/map?x=" + mapX + "&z=" + mapZ + "&dimension=" + dimension + "&width=" + MAP_BLOCK_WIDTH + "&height=" + MAP_BLOCK_WIDTH + "&scale=2";
    mapImage.title = "Go to map"
    mapImage.onclick = () => {
        window.location.href = "map.xhtml?x=" + x + "&z=" + z + "&dimension=" + dimension;
    }
    mapPreview.appendChild(mapImage);
}

async function getPlayerData () {
    const query = new URLSearchParams(window.location.search);

    if (query.has("player")) currentPlayer = query.get("player");

    let apiResponse = await fetch(window.location.origin + "/fishmaps/api/player?name=" + currentPlayer);
    let json = await apiResponse.json();

    x = json.x;
    z = json.z;
    dimension = json.dimension;

    loadProfile();
}

async function getPlayerName () {
    if (currentPlayer == INVALID_PLAYER) await getPlayerData();
    return currentPlayer;
}