const WIDTH = 1920;
const HEIGHT = 1080;
const STEP = 40;

var currentX = 0;
var currentZ = 0;
var dimension = "minecraft:overworld";

function renderMap () {
    let map = document.getElementById("mapView");
    map.innerHTML = "";

    const topLeftX = Math.floor(currentX - (WIDTH / 2));
    const topLeftZ = Math.floor(currentZ - (HEIGHT / 2));
    for (let z = 0; z < HEIGHT / STEP; ++z) {
        for (let x = 0; x < WIDTH / STEP; ++x) {
            let image = document.createElement("img");
            image.class = "mapTile";
            image.style = "width:" + STEP + "px;height:" + STEP + "px;"
            image.src = "images/map?x=" + (topLeftX + currentX + x * STEP) + "&z=" + (topLeftZ + currentZ + z * STEP) + "&dimension=" + dimension + "&width=" + STEP + "&height=" + STEP;
            map.appendChild(image);
        }
        map.appendChild(document.createElement("br"));
    }
}

function up () {
    currentZ += STEP;
    updateURL();
    renderMap();
}

function down () {
    currentZ -= STEP;
    updateURL();
    renderMap();
}

function left () {
    currentX += STEP;
    updateURL();
    renderMap();
}

function right () {
    currentX -= STEP;
    updateURL();
    renderMap();
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
}