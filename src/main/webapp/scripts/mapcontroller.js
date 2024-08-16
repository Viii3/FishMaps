var width = 1920;
var height = 1080;
const STEP = 32;

var topLeftX = 0;
var topLeftZ = 0;

var currentX = 0;
var currentZ = 0;
var dimension = "minecraft:overworld";
var scale = 1;

var playerData = [];
var eventData = [];
var socket;

var shouldUpdateMap = false;
var tileCache = {};

function renderMap () {
    width = window.outerWidth;
    height = window.outerHeight;

    let map = document.getElementById("mapView");
    map.innerHTML = "";

    const scaledStep = Math.floor(STEP * scale);
    const horizontalTiles = Math.ceil(width / scaledStep);
    const verticalTiles = Math.ceil(height / scaledStep);

    topLeftX = Math.floor(currentX - scaledStep * Math.floor(horizontalTiles / 2));
    topLeftZ = Math.floor(currentZ - scaledStep * Math.floor(verticalTiles / 2));
    for (let z = 0; z < height / scale; z += STEP) {
        for (let x = 0; x < width / scale; x += STEP) {
            let imageX = topLeftX + currentX + x;
            let imageZ = topLeftZ + currentZ + z;

            let image = document.createElement("img");
            image.class = "mapTile";
            image.id = "(" + imageX + "," + imageZ + ")"
            image.style.cssText = "width:" + scaledStep + "px;height:" + scaledStep + "px;"

            let srcString = "images/map?x=" + imageX + "&z=" + imageZ + "&dimension=" + dimension + "&width=" + STEP + "&height=" + STEP + "&scale=" + scale;
            if (image.id in tileCache) srcString += "&time=" + tileCache[image.id]

            image.src = srcString;
            map.appendChild(image);
        }
        map.appendChild(document.createElement("br"));
    }
    renderPlayers();
    renderEvents();
}

function updateMapCache (blockX, blockZ) {
    let tileX = STEP * scale * Math.floor(blockX / STEP) - (currentX % STEP);
    let tileZ = STEP * scale * Math.floor(blockZ / STEP) - (currentZ % STEP);
    let tileId = "(" + tileX + "," + tileZ + ")";
    
    if (document.getElementById(tileId) != null) {
        tileCache[tileId] = Date.now();
    }

    shouldUpdateMap = true;
}

function parseWebsockList (dataList) {
    for (chunk of dataList) {
        if (chunk.dimension == dimension) {
            updateMapCache(chunk.x * 16, chunk.z * 16);
            updateMapCache(chunk.x * 16, chunk.z * 16 + 15);
            updateMapCache(chunk.x * 16 + 15, chunk.z * 16);
            updateMapCache(chunk.x * 16 + 15, chunk.z * 16 + 15);
        }
    }

    if (shouldUpdateMap) renderMap();
}

function partialUpdateMap () {
    if (shouldUpdateMap) {
        renderMap();
        shouldUpdateMap = false;
    }
}

// TODO: Players are not rendered in the correct place and do not move properly with the map.
function renderPlayers () {
    let playerView = document.getElementById("playerView");
    playerView.innerHTML = "";

    for (let player of playerData) {
        let xPos = (player.x - topLeftX) * scale;
        let zPos = (player.z - topLeftZ) * scale;

        let playerHead = document.createElement("img");
        playerHead.className = "player";
        playerHead.style.top = "" + zPos + "px";
        playerHead.style.left = "" + xPos + "px";
        playerHead.src = "images/players?name=" + player.name;
        playerView.appendChild(playerHead);
    }
}

function renderEvents () {
    let eventView = document.getElementById("eventView");
    eventView.innerHTML = "";

    let scaledStep = Math.floor(STEP * scale);
    for (let event of eventData) {
        let xPos = (event.x - topLeftX) * scale;
        let zPos = (event.z - topLeftZ) * scale;

        let eventImg = document.createElement("img");
        eventImg.className = "event";
        eventImg.style.top = "" + zPos + "px";
        eventImg.style.left = "" + xPos + "px";
        eventImg.src = event.image;
        eventView.appendChild(eventImg);
    }
}

async function updatePlayers () {
    return;

    const topLeftX = Math.floor(currentX - (width / 2));
    const topLeftZ = Math.floor(currentZ - (height / 2));
    const bottomRightX = Math.floor(currentX + ((width / 2) / scale));
    const bottomRightZ = Math.floor(currentZ + ((height / 2) / scale));

    try {
        let queryString = "/fishmaps/api/player/map?minX=" + topLeftX + "&maxX=" + bottomRightX + "&minZ=" + topLeftZ + "&maxZ=" + bottomRightZ + "&dimension=" + dimension;
        let response = await fetch(window.location.origin + queryString);
        playerData = await response.json();
    }
    catch {
        playerData = [];
    }

    renderPlayers();
}

// TODO: Put events on the map.
async function updateEvents () {

}

function up () {
    currentZ -= STEP;
    updateURL();
    renderMap();
}

function down () {
    currentZ += STEP;
    updateURL();
    renderMap();
}

function left () {
    currentX -= STEP;
    updateURL();
    renderMap();
}

function right () {
    currentX += STEP;
    updateURL();
    renderMap();
}

function zoomIn () {
    ++scale;
    renderMap();
}

function zoomOut () {
    if (scale > 1) {
        --scale;
        renderMap();
    }
}

function shiftDimension (newDimension) {
    if (dimension != newDimension) {
        dimension = newDimension;
        tileCache = {};
        updateURL();
        renderMap();
    }
}

function parseURL () {
    const query = new URLSearchParams(window.location.search);

    if (query.has("x")) currentX = parseIntParam(query.get("x"));
    if (query.has("z")) currentZ = parseIntParam(query.get("z"));
    if (query.has("dimension")) dimension = query.get("dimension");
}

function updateURL () {
    const query = new URLSearchParams(window.location.search);
    query.set("x", currentX);
    query.set("z", currentZ);
    query.set("dimension", dimension);
    
    window.history.replaceState(null, null, "map.xhtml?" + query.toString());
}

function parseIntParam (param) {
    let num = Number(param);
    if (num == NaN) return 0;
    return Math.floor(num);
}

function startup () {
    parseURL();
    updateURL();
    renderMap();

    socket = new WebSocket("ws://" + document.location.host + "/fishmaps/update/map");
    socket.onmessage = function (event) {
        console.log(event.data);
        parseWebsockList(JSON.parse(event.data));
    }
}

//const playerUpdateIntervalID = setInterval(updatePlayers, 5000);
//const eventUpdateIntervalID = setInterval(updateEvents, 10000);