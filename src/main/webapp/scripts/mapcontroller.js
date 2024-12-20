// Map Sizing
var width = 1920;
var height = 1080;
const STEP = 32;

// Icon Sizing
const playerHeadHalfWidth = Math.floor(24 / 2);
const eventIconHalfWidth = Math.floor(24 / 2);

// Tile Data
var topLeftX = 0;
var topLeftZ = 0;
var bottomRightX = 1080;
var bottomRightZ = 1920;
var horizontalTiles = 0;
var verticalTiles = 0;

// Current Map Position
var currentX = 0;
var currentZ = 0;
var dimension = "minecraft:overworld";
var scale = 1;

// Internals
const MAP_MOVE_INTERVAL_MILLIS = 250;
var prevMapMoveTime = 0;
var mapIntervalFunctionID = null;
var playerData = [];
var eventData = [];
var socket;

var shouldUpdateMap = false;
var tileCache = {};

function renderAll () {
    renderMap();
    resetPlayers();
    resetEvents();
}

function renderMap () {
    width = window.outerWidth;
    height = window.outerHeight;

    let map = document.getElementById("mapView");
    map.innerHTML = "";

    const scaledStep = Math.floor(STEP * scale);
    horizontalTiles = Math.ceil(width / scaledStep);
    verticalTiles = Math.ceil(height / scaledStep);

    topLeftX = Math.floor(currentX - STEP * Math.floor(horizontalTiles / 2));
    topLeftZ = Math.floor(currentZ - STEP * Math.floor(verticalTiles / 2));
    bottomRightX = topLeftX + horizontalTiles * scaledStep;
    bottomRightZ = topLeftZ + verticalTiles * scaledStep;

    for (let z = 0; z < height / scale; z += STEP) {
        for (let x = 0; x < width / scale; x += STEP) {
            let imageX = topLeftX + x;
            let imageZ = topLeftZ + z;

            let image = document.createElement("img");
            image.className = "mapTile";
            image.id = "(" + imageX + "," + imageZ + ")";
            image.style.cssText = "width:" + scaledStep + "px;height:" + scaledStep + "px;";
            image.draggable = false;

            let srcString = "images/map?x=" + imageX + "&z=" + imageZ + "&dimension=" + dimension + "&width=" + STEP + "&height=" + STEP + "&scale=" + scale;
            if (image.id in tileCache) srcString += "&time=" + tileCache[image.id]

            image.src = srcString;
            map.appendChild(image);
        }
        map.appendChild(document.createElement("br"));
    }
}

function updateMapCache (blockX, blockZ) {
    let tileX = STEP * Math.floor(blockX / STEP) - (currentX % STEP);
    let tileZ = STEP * Math.floor(blockZ / STEP) - (currentZ % STEP);
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

    partialUpdateMap();
}

function partialUpdateMap () {
    if (shouldUpdateMap) {
        renderMap();
        shouldUpdateMap = false;
    }
}

function addPlayerToMap (player) {
    if (player.dimension != dimension) return;

    let playerView = document.getElementById("playerView");
    let xPos = (player.x - topLeftX) * scale - playerHeadHalfWidth;
    let zPos = (player.z - topLeftZ) * scale - playerHeadHalfWidth;

    let playerHead = document.createElement("img");
    playerHead.id = player.name;
    playerHead.className = "player";
    playerHead.style.top = "" + zPos + "px";
    playerHead.style.left = "" + xPos + "px";
    playerHead.src = "images/players?name=" + player.name;
    playerHead.title = player.name;
    playerHead.draggable = false;
    playerHead.onclick = () => {
        window.location.href = "profile.xhtml?player=" + player.name;
    }
    playerView.appendChild(playerHead);
}

function movePlayers () {
    for (let player of playerData) {
        if (player.dimension != dimension) continue;

        let xPos = (player.x - topLeftX) * scale - playerHeadHalfWidth;
        let zPos = (player.z - topLeftZ) * scale - playerHeadHalfWidth;

        let playerHead = document.getElementById(player.name);
        if (playerHead == null) {
            addPlayerToMap(player);
            continue;
        }

        playerHead.style.top = "" + zPos + "px";
        playerHead.style.left = "" + xPos + "px";
    }
}

function resetPlayers () {
    let playerView = document.getElementById("playerView");
    playerView.innerHTML = "";

    for (let player of playerData) {
        addPlayerToMap(player);
    }
}

function addEventToMap (event) {
    let eventView = document.getElementById("eventView");

    if (event.dimension != dimension) return;
    let xPos = (event.x - topLeftX) * scale - eventIconHalfWidth;
    let zPos = (event.z - topLeftZ) * scale - eventIconHalfWidth;

    let eventImg = document.createElement("img");
    eventImg.id = "" + event.timestamp + "-" + event.message.replaceAll(" ", "");
    eventImg.className = "event";
    eventImg.style.top = "" + zPos + "px";
    eventImg.style.left = "" + xPos + "px";
    eventImg.src = event.icon;
    eventImg.title = event.message;
    eventImg.draggable = false;
    eventView.appendChild(eventImg);
}

function addNewEventsToMap () {
    for (let event of eventData) {
        if (document.getElementById("" + event.timestamp + "-" + event.message.replaceAll(" ", "")) == null)
            addEventToMap(event);
    }
}

function resetEvents () {
    let eventView = document.getElementById("eventView");
    eventView.innerHTML = "";

    for (let event of eventData) {
        addEventToMap(event);
    }
}

async function updatePlayers () {
    try {
        let queryString = "/fishmaps/api/map/player?minX=" + topLeftX + "&maxX=" + bottomRightX + "&minZ=" + topLeftZ + "&maxZ=" + bottomRightZ + "&dimension=" + dimension;
        let response = await fetch(window.location.origin + queryString);
        playerData = await response.json();
    }
    catch {
        playerData = [];
    }

    movePlayers();
}

async function updateEvents () {
    try {
        let queryString = "/fishmaps/api/map/event?minX=" + topLeftX + "&maxX=" + bottomRightX + "&minZ=" + topLeftZ + "&maxZ=" + bottomRightZ + "&dimension=" + dimension;
        let response = await fetch(window.location.origin + queryString);
        eventData = await response.json();
    }
    catch {
        eventData = [];
    }

    addNewEventsToMap();
}

function up () {
    currentZ -= STEP;
    updateURL();
    renderAll();
}

function startIntervalUp () {
    prevMapMoveTime += MAP_MOVE_INTERVAL_MILLIS * 2;
    mapIntervalFunctionID = setInterval(intervalUp, MAP_MOVE_INTERVAL_MILLIS / 2);
}

function intervalUp () {
    let currentTime = Date.now();
    if (currentTime - prevMapMoveTime >= MAP_MOVE_INTERVAL_MILLIS) {
        up();
        prevMapMoveTime = currentTime;
    }
}

function down () {
    currentZ += STEP;
    updateURL();
    renderAll();
}

function startIntervalDown () {
    prevMapMoveTime += MAP_MOVE_INTERVAL_MILLIS * 2;
    mapIntervalFunctionID = setInterval(intervalDown, MAP_MOVE_INTERVAL_MILLIS / 2);
}

function intervalDown () {
    let currentTime = Date.now();
    if (currentTime - prevMapMoveTime >= MAP_MOVE_INTERVAL_MILLIS) {
        down();
        prevMapMoveTime = currentTime;
    }
}

function left () {
    currentX -= STEP;
    updateURL();
    renderAll();
}

function startIntervalLeft () {
    prevMapMoveTime += MAP_MOVE_INTERVAL_MILLIS * 2;
    mapIntervalFunctionID = setInterval(intervalLeft, MAP_MOVE_INTERVAL_MILLIS / 2);
}

function intervalLeft () {
    let currentTime = Date.now();
    if (currentTime - prevMapMoveTime >= MAP_MOVE_INTERVAL_MILLIS) {
        left();
        prevMapMoveTime = currentTime;
    }
}

function right () {
    currentX += STEP;
    updateURL();
    renderAll();
}

function startIntervalRight () {
    prevMapMoveTime += MAP_MOVE_INTERVAL_MILLIS * 2;
    mapIntervalFunctionID = setInterval(intervalRight, MAP_MOVE_INTERVAL_MILLIS / 2);
}

function intervalRight () {
    let currentTime = Date.now();
    if (currentTime - prevMapMoveTime >= MAP_MOVE_INTERVAL_MILLIS) {
        right();
        prevMapMoveTime = currentTime;
    }
}

function stopMovementInterval () {
    if (mapIntervalFunctionID == null) return;
    clearInterval(mapIntervalFunctionID);
    mapIntervalFunctionID = null;
}

function zoomIn () {
    ++scale;
    renderAll();
}

function zoomOut () {
    if (scale > 1) {
        --scale;
        renderAll();
    }
}

function shiftDimension (newDimension) {
    if (dimension != newDimension) {
        dimension = newDimension;
        tileCache = {};
        updateURL();
        renderAll();
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

function trackMousePosition (mouseMoveEvent) {
    let x = Math.floor(mouseMoveEvent.clientX / scale) + topLeftX;
    let z = Math.floor(mouseMoveEvent.clientY / scale) + topLeftZ;

    if (x > 0) x = "&nbsp;" + x;
    if (z > 0) z = "&nbsp;" + z;

    let tracker = document.getElementById("mousePos");
    tracker.innerHTML = "<p>X: " + x + "<br/>Z: " + z + "</p>";
}

function parseIntParam (param) {
    let num = Number(param);
    if (num == NaN) return 0;
    return Math.floor(num);
}

function startup () {
    parseURL();
    updateURL();
    renderAll();

    socket = new WebSocket("ws://" + document.location.host + "/fishmaps/update/map");
    socket.onmessage = function (event) {
        parseWebsockList(JSON.parse(event.data));
    }
}

const playerUpdateIntervalID = setInterval(updatePlayers, 5000);
const eventUpdateIntervalID = setInterval(updateEvents, 5000);
document.onmousemove = trackMousePosition;