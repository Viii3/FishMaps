const INVALID_PLAYER = "Invalid Player";

var currentPlayer = INVALID_PLAYER;
var x = 0;
var z = 0;
var dimension = "minecraft:overworld";

function loadProfile () {
    let image = document.getElementById("profileImage");
    image.src = "images/players?name=" + currentPlayer;

    let mapPreview = document.getElementById("mapPreview");
    mapPreview.innerHTML = "";
    mapPreview.onclick = () => {
        window.location.href = "map.xhtml?x=" + x + "&z=" + z + "&dimension=" + dimension;
    }

    let mapImage = document.createElement("img");
    mapImage.id = "map";
    mapImage.src = "images/map?x=" + (x - 64) + "&z=" + (z - 64) + "&dimension=" + dimension + "&width=128&height=128&scale=1";
    
    mapPreview.appendChild(mapImage);

    let profileView = document.getElementById("profileInfo");
    profileView.innerHTML = "";
    let playerName = document.createElement("h2");
    playerName.textContent = currentPlayer;
    profileView.appendChild(playerName);
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